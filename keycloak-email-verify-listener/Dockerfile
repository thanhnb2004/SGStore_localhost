FROM quay.io/keycloak/keycloak:26.5.5
ARG JAR_FILE=target/*-all.jar
COPY ${JAR_FILE} /opt/keycloak/providers/keycloak-rabbitmq-listener.jar
RUN /opt/keycloak/bin/kc.sh build
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev"]