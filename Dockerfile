FROM openjdk:17

WORKDIR /redisWebSocket

COPY redisWebSocket-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
