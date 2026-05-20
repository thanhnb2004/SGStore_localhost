package com.ptit.clone.config.keycloak;

import com.ptit.clone.config.properties.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminClientFactory {
    private final KeycloakProperties keycloakProperties;
    public Keycloak buildAdminClient() {
        log.info("Building Keycloak Admin Client - serverUrl={}, realm={}, clientId={}",
                keycloakProperties.getServerUrl(),
                keycloakProperties.getRealm());
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm(keycloakProperties.getAdminRealm())
                .clientId(keycloakProperties.getAdminClientId())
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .grantType(OAuth2Constants.PASSWORD);
        return builder.build();
    }
//    fix conffig's keycloak second
}
