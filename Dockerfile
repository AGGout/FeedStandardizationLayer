FROM amazoncorretto:17

WORKDIR /app

COPY build/libs/FeedStandardizationLayer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
