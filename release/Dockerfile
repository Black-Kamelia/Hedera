FROM --platform=linux/amd64 amazoncorretto:17-al2023-headless

WORKDIR /app
VOLUME /app
EXPOSE 8080

COPY app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
