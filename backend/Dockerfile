FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/backend-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]