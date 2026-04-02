# Build stage
FROM maven:3.9.10-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn -pl discovery-service -am clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /build/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]
