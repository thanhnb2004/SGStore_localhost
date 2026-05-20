FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl catalog-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/catalog-service/target/*.jar /catalog-service.jar
ENTRYPOINT ["java","-jar","/catalog-service.jar"]
