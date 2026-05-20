FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl order-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/order-service/target/*.jar /order-service.jar
ENTRYPOINT ["java","-jar","/order-service.jar"]
