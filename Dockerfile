FROM openjdk:latest
EXPOSE 8080
COPY ./target/foundly-app-0.0.1-SNAPSHOT.jar.original ./target/foundly-app-0.0.1-SNAPSHOT.jar.original 
CMD ["java","-jar","./target/foundly-app-0.0.1-SNAPSHOT.jar.original"]
