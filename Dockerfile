FROM openjdk:8-jre-alpine

ENV APPLICATION_USER dirk

RUN mkdir /app

COPY ./build/libs/server.jar /app/server.jar
WORKDIR /app

CMD ["java", "-server", "-jar", "server.jar"]