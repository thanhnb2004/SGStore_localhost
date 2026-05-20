FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl cart-service -am clean package -DskipTests

FROM eclipse-temurin:25-jdk
COPY --from=build /workspace/cart-service/target/*.jar /cart-service.jar
ENTRYPOINT ["java","-jar","/cart-service.jar"]
