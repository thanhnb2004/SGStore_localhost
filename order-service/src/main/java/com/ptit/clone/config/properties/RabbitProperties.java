package com.ptit.clone.config.properties;

import com.ptit.clone.properties.RabbitConfigProperties;
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
@ConfigurationProperties(prefix = "queue")
public class RabbitProperties {
    private RabbitConfigProperties cartQuery;
    private RabbitConfigProperties cartClear;
    private RabbitConfigProperties paymentCompleted;
}
