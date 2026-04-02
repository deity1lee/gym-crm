FROM maven:3.9.10-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn -pl trainer-workload-service -am clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /build/trainer-workload-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]