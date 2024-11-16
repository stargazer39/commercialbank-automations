package com.dehemi.combank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "timeouts")
@Data
public class TimeoutConfig {
    int waitForIntercept;
}
