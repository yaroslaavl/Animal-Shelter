FROM openjdk:17-jdk-slim

WORKDIR /app
COPY animal-shelter/target/animal-shelter-0.0.1-SNAPSHOT.jar /app/animal-shelter.jar

COPY animal-shelter/src/main/resources/application.yml /app/application.yml

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "animal-shelter.jar"]
CMD ["--spring.config.location=file:/app/application.yml"]