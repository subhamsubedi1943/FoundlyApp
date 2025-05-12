# # Use an official OpenJDK base image
# FROM openjdk:21-jdk

# # Copy the built JAR file (assumes 'target/*.jar' exists)
# COPY target/foundly-app-0.0.1-SNAPSHOT.jar app.jar

# # Set entrypoint to run the JAR file
# ENTRYPOINT ["java", "-jar", "/app.jar"]
# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Copy the JAR file from your target directory to the image
COPY target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
