package com.ptit.clone.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String apiEndpoint;
    private String redirectUrl;
    private String ipnUrl;
}
