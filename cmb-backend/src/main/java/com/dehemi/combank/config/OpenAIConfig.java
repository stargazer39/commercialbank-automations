package com.dehemi.combank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAIConfig {
    private String secret;
    private String transactionCategorizePrompt;
}
