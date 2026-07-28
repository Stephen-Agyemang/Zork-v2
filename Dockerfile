FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# A container build is a release candidate: compile, run the complete test
# suite, and package only if verification succeeds.
RUN cd myproject && mvn --batch-mode clean verify

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/myproject/target/myproject-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
