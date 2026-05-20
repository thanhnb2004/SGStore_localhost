FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl payment-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/payment-service/target/*.jar /payment-service.jar
ENTRYPOINT ["java","-jar","/payment-service.jar"]
