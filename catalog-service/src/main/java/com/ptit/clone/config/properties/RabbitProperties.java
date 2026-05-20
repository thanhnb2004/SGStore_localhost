package com.ptit.clone.config.properties;

import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "queue")
public class RabbitProperties {
    private RabbitConfigProperties elasticUpsert;
    private RabbitConfigProperties productArchived;
    private RabbitConfigProperties productDeleted;
    private RabbitConfigProperties variantPaused;
    private RabbitConfigProperties variantDeleted;
}
