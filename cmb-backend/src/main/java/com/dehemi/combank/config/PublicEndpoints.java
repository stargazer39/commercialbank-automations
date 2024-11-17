package com.dehemi.combank.config;

import com.dehemi.combank.dao.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "public-endpoints")
@Data
public class PublicEndpoints {
    Set<String> endpoints;
}

