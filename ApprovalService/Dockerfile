FROM gradle:8.10-jdk23-alpine AS builder

WORKDIR /home/gradle/src
COPY . .

RUN gradle build -x test

FROM openjdk:23-jdk-slim

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/BookingService.jar

ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=password

EXPOSE 4015

ENTRYPOINT ["java", "-jar", "/app/BookingService.jar"]