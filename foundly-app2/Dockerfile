# Use an official OpenJDK base image
FROM openjdk:21-jdk

# Copy the built JAR file (assumes 'target/*.jar' exists)
COPY target/*.jar app.jar

# Set entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
