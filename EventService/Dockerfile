FROM gradle:8.10-jdk23-alpine AS builder

WORKDIR /home/gradle/src
COPY . .

RUN gradle build -x test

FROM openjdk:23-jdk-slim

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/EventService.jar

ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=password

EXPOSE 4014

ENTRYPOINT ["java", "-jar", "/app/EventService.jar"]