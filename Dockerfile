FROM openjdk:8-jre-alpine
EXPOSE 8075
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
