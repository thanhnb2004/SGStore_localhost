FROM eclipse-temurin:25-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /catalog-service.jar
ENTRYPOINT ["java","-jar","/catalog-service.jar"]