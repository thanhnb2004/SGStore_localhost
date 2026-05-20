FROM eclipse-temurin:25-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /payment-service.jar
ENTRYPOINT ["java","-jar","/payment-service.jar"]