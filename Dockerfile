FROM openjdk:8-jre-alpine

ENV APPLICATION_USER dirk
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/server.jar /app/server.jar
WORKDIR /app

CMD ["java", "-server", "-jar", "server.jar"]