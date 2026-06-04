FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN cd myproject && mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/myproject/target/myproject-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
