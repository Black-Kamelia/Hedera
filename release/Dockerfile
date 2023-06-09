FROM --platform=linux/amd64 openjdk:17-alpine

WORKDIR /app
VOLUME /app
EXPOSE 8080

COPY ./app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
