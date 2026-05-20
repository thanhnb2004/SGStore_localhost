FROM eclipse-temurin:25-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /cart-service.jar
ENTRYPOINT ["java","-jar","/cart-service.jar"]