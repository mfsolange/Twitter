# Use Azul Zulu OpenJDK 21 (lightweight Alpine version)
FROM azul/zulu-openjdk-alpine:21

# Set working directory inside container
WORKDIR /app

# Copy your Spring Boot JAR file
COPY target/Twitter-1.0-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
