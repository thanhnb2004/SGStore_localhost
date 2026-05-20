package com.ptit.clone.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String serverUrl;
    private String realm;
    private String adminUsername;
    private String adminPassword;
    private String adminRealm;
    private String adminClientId;
//    private String adminClientSecret;
}
