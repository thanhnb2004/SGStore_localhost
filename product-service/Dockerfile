FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl product-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/product-service/target/*.jar /product-service.jar
ENTRYPOINT ["java","-jar","/product-service.jar"]
