FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl user-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/user-service/target/*.jar /user-service.jar
ENTRYPOINT ["java","-jar","/user-service.jar"]
