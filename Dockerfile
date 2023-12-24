FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradle/ gradle/

COPY src/ src/

RUN ./gradlew build

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app-0.0.1-SNAPSHOT.jar"]