package com.dehemi.combank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "selenium")
@Data
public class SeleniumConfig {
    List<String> flags;
}
