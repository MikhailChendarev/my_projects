FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew build -x test

CMD ["java", "-jar", "build/libs/taskmanagementsystem-0.0.1-SNAPSHOT.jar"]
