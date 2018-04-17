FROM openjdk:8-jre-alpine

ADD build/libs/betpool.jar /app/betpool.jar

EXPOSE 8080

WORKDIR /app
ENTRYPOINT ["java", "-jar", "betpool.jar"]
