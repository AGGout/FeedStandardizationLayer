# Feed Standardization Layer

A Spring Boot service that accepts raw sports feed messages from multiple external providers, normalizes them into a
canonical format, and forwards them to a downstream messaging system.

## Running the service

**Prerequisites:** Docker, Docker Compose, and JDK 17+ installed.

Build the jar locally first (fast — uses your local Gradle cache), then let Docker copy it into the image:

```bash
./gradlew build -x test
docker compose up --build
```

The service will be available at `http://localhost:8080`.

> **Why build locally?** The Dockerfile intentionally skips the Gradle build step inside Docker to avoid downloading all
> dependencies from scratch on every image build. For CI/CD pipelines a multi-stage Dockerfile with a cached dependency
> layer is more appropriate.

### Test requests

**Alpha — odds update:**

```bash
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{"msg_type": "odds_update", "event_id": "ev123", "values": {"1": 2.0, "X": 3.1, "2": 3.8}}'
```

**Alpha — bet settlement:**

```bash
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{"msg_type": "settlement", "event_id": "ev123", "outcome": "1"}'
```

**Beta — odds update:**

```bash
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{"type": "ODDS", "event_id": "ev456", "odds": {"home": 1.95, "draw": 3.2, "away": 4.0}}'
```

**Beta — bet settlement:**

```bash
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{"type": "SETTLEMENT", "event_id": "ev456", "result": "away"}'
```

A successful request returns `202 Accepted`. The normalized message is logged to stdout as JSON (current stub
implementation). An invalid request returns `400 Bad Request` with a description of the problem.

## What it does

Different sports data providers send the same types of events (odds changes, bet settlements) in different formats —
different field names, different value representations, different message type identifiers. This service acts as an
adapter layer, translating each provider's proprietary format into a single consistent model that downstream consumers
can rely on.

```
POST /provider-alpha/feed  ──┐
                             ├──▶  Normalize  ──▶  Messenger  ──▶  (broker / log)
POST /provider-beta/feed   ──┘
```

## Supported providers and message types

| Provider | Message type field | Odds change   | Bet settlement |
|----------|--------------------|---------------|----------------|
| Alpha    | `msg_type`         | `odds_update` | `settlement`   |
| Beta     | `type`             | `ODDS`        | `SETTLEMENT`   |

Normalized odds are always expressed as a `Map<String, Double>` keyed by 1X2 symbol (`"1"`, `"X"`, `"2"`).

## Design decisions

### Normalizer registry

Each provider+message-type combination is handled by a dedicated `FeedNormalizer` implementation. All normalizers are
registered in a `FeedNormalizerRegistry` at startup, keyed by `"source:rawMessageType"` (e.g. `"alpha:odds_update"`).

This means **adding a new provider or message type requires only adding a new `@Component` class** — no existing code
needs to change. The registry enforces uniqueness and consistency at startup, failing fast if two normalizers claim the
same key or if normalizers for the same source disagree on which JSON field carries the message type.

### Sealed model

`NormalizedMessage` is a `sealed interface` permitting only `NormalizedOddsChangeMessage` and
`NormalizedBetSettlementMessage`. This makes the set of message types explicit at the type system level and enables
exhaustive pattern matching for any code that handles normalized messages.

### Deterministic idempotency key

Each forwarded message carries an `IdempotencyKey` header — a UUID v7 derived deterministically from
`(source, eventId, requestTimestamp)` using SHA-1. The same event arriving more than once within the same millisecond
produces the same UUID, allowing downstream consumers to deduplicate safely.

### Error handling

A `@RestControllerAdvice` centralises exception-to-HTTP mapping. Validation failures (unknown provider, unrecognised
message type, missing fields) return `400 Bad Request` with a descriptive message. Unexpected errors return `500` with a
generic message to avoid leaking internals.

## Scaling

This service is completely stateless — no sessions, no in-memory state, no distributed cache. Every request is
independent. This makes horizontal scaling trivial: deploy multiple replicas behind a load balancer and they will all
behave identically.

On **AWS with Kubernetes (EKS)** this is straightforward:

- Set the desired replica count in the `Deployment` and let the Kubernetes scheduler distribute pods across availability
  zones for resilience.
- Use the **Horizontal Pod Autoscaler (HPA)** to scale automatically on CPU or request throughput, so the fleet grows
  during peak feed activity (e.g. live match windows) and shrinks when quiet.
- The Docker image is already self-contained, so rolling updates and rollbacks work with zero configuration changes.

The only external coordination point is the message broker downstream — ensure its throughput and partition count can
accommodate the traffic from multiple replicas publishing in parallel.

## Production considerations

### Serialization — use Protobuf over JSON

The current `JsonLogMessenger` is a stub. When replacing it with a real broker integration, **Protobuf is strongly
recommended over JSON** for the wire format:

- **Smaller payload** — binary encoding is typically 3–10× smaller than equivalent JSON, reducing network bandwidth and
  broker storage costs at high throughput.
- **Faster serialization/deserialization** — no string parsing; field access is direct and type-safe.
- **Schema enforcement** — the `.proto` contract is explicit and versioned, catching breaking changes at compile time
  rather than at runtime.
- **Language agnostic** — downstream consumers in any language can deserialize without custom parsing logic.

A `ProtobufMessenger` implementation would replace `JsonLogMessenger` and serialize `NormalizedMessage` to a generated
Protobuf type before publishing to Kafka/RabbitMQ. The `Messenger` interface is already the only integration point that
needs changing.

### Partitioning and ordering

When scaling out to multiple broker partitions (Kafka) or multiple bindings (RabbitMQ), messages must be **routed to the
correct partition to preserve ordering** for a given event. The natural partition key is `eventId` — a single event (
match) produces many messages over its lifetime (multiple odds updates followed by a settlement), and all of them must
land on the same partition so consumers process them in order.

**On ordering guarantees and timestamps:** the normalized model does not currently carry an event timestamp from the
source payload. If ordering matters to consumers (e.g. they must not apply an older odds update after a newer one), a
`sourceTimestamp` field should be added to `NormalizedMessage` and populated by each normalizer from the provider's own
timestamp field. Consumers can then use this to detect and discard out-of-order messages rather than relying solely on
broker ordering, which only holds within a single partition and can break during rebalances or failover.

The `requestTimestamp` already present in the idempotency key reflects when *we* received the message, not when the
provider generated it — these can differ significantly under load or network delay, making it unsuitable as an ordering
signal.

### Garbage collection

For this service **low latency is more important than throughput**. A GC pause that delays odds updates reaching
consumers means those consumers may act on stale data — unacceptable in a live betting context. Throughput-oriented
collectors (Parallel GC, throughput-tuned G1) trade pause time for higher overall processing capacity, which is the
wrong tradeoff here.

Recommended collectors:

- **ZGC** (`-XX:+UseZGC`) — sub-millisecond pauses regardless of heap size; the default choice for latency-sensitive JVM
  services on JDK 17+.
- **Shenandoah** (`-XX:+UseShenandoahGC`) — similar goals to ZGC, available on OpenJDK builds.

Avoid **Parallel GC** and throughput-optimised G1 tuning in production, as they can introduce stop-the-world pauses of
tens to hundreds of milliseconds under load.

### Security

No authentication or transport security is implemented at the application level. This is an intentional deferral pending
clarity on the infrastructure topology. The right approach depends heavily on the deployment context:

- **Internal network / service mesh** — if providers call this service over a private network or through a mesh like
  Istio, mTLS can be enforced at the infrastructure level with no application changes needed.
- **Public or semi-public endpoints** — if providers reach this service over the internet or an untrusted network, API
  key validation or OAuth2 client credentials should be added (e.g. via a Spring Security filter or an API gateway in
  front of the service).
- **IP allowlisting** — a lightweight alternative if each provider has a fixed egress IP range; can be handled at the
  load balancer or firewall level without touching the application.

The `Messenger` layer similarly assumes the broker connection is secured at the infrastructure level (TLS, SASL for
Kafka, or equivalent for RabbitMQ).

## Adding a new provider

1. Create a package under `normalizer/<provider-name>/`
2. Implement `FeedNormalizer` for each message type the provider supports
3. Annotate each class with `@Component`

The registry picks them up automatically on the next startup.

## Assumptions

During development I made a few assumptions I'd like to flag:

- Event identity — I assumed eventId refers to a sporting event (match), not to the uniqueness of an individual message.
  A
  single event  
  produces multiple messages over its lifetime — repeated odds updates followed by a final settlement — and I treated
  eventId as the     
  natural key for grouping and ordering those messages.

- Latency requirements — I assumed the service is time-critical and that feed messages must reach downstream consumers
  as
  quickly as
  possible, since consumers may be acting on live odds. This influenced some technical choices, most notably
  recommending
  ZGC as the
  garbage collector to minimise pause times.

- Extensibility — I assumed that new feed providers and message types are likely to be added over time. The architecture
  was designed
  around this: adding a new provider or message type requires only a new self-contained class, with no changes to
  existing
  code.

## AI assistance

[Claude](https://claude.ai) (Anthropic) was used as a development assistant throughout this project. Concretely, it
helped with:

- **Code generation** — scaffolding boilerplate such as normalizer stubs, the registry, improving and rewording Javadoc,
  and test structure, which was then reviewed and adjusted.
- **Debugging** — identifying issues such as mismatched `getRawMessageType()` values, and Java record constructor
  ambiguity due to type erasure.
- **Refactoring** — moving & renaming, extracting.
- **Documentation** — structuring and improving README sections on production considerations (Protobuf, GC,
  partitioning, security).

All generated code was reviewed, tested, and fixed or adjusted before being accepted.
