FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the app using a lightweight Java environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose standard web port
EXPOSE 8081

# Run the app with the anti-crash memory limits built-in!
ENTRYPOINT ["java", "-Xmx256m", "-Xms256m", "-jar", "app.jar"]