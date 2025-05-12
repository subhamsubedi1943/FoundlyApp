FROM openjdk:latest
EXPOSE 8080
COPY ./target/foundly-app-0.0.1-SNAPSHOT.jar ./target/foundly-app-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","./target/foundly-app-0.0.1-SNAPSHOT.jar"]
