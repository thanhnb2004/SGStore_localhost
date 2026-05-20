FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl keycloak-email-verify-listener -am clean package -DskipTests

FROM quay.io/keycloak/keycloak:26.5.5
COPY --from=build /workspace/keycloak-email-verify-listener/target/*-all.jar /opt/keycloak/providers/keycloak-rabbitmq-listener.jar
RUN /opt/keycloak/bin/kc.sh build
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev"]