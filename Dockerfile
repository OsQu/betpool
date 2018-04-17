FROM openjdk:8-jre-alpine

ADD build/libs/betpool.jar /app/betpool.jar

EXPOSE 8080

WORKDIR /app
CMD ["java", "-jar", "betpool.jar", "prod"]
