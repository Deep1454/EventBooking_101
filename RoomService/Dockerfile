FROM gradle:8.10-jdk23-alpine AS builder

WORKDIR /home/gradle/src
COPY . .

RUN gradle build -x test

FROM openjdk:23-jdk-slim

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/RoomService.jar

ENV POSTGRES_USER=admin \
    POSTGRES_PWD=password

EXPOSE 4011

ENTRYPOINT ["java", "-jar", "/app/RoomService.jar"]