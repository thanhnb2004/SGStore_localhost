package com.ptit.clone.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.image")
public class ImageStorageProperties {

    private String uploadDir = "/home/ubuntu/sgstore/images";

    private String publicBaseUrl = "/internal/v1/images";

    private long maxFileSizeBytes = 10L * 1024 * 1024;
}
