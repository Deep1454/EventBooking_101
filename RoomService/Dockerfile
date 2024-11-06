FROM gradle:8.10-jdk23-alpine AS builder

WORKDIR /home/gradle/src
COPY . .

RUN gradle build -x test

FROM openjdk:23-jdk-slim

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/RoomService.jar

EXPOSE 2010

ENTRYPOINT ["java", "-jar", "/app/RoomService.jar"]