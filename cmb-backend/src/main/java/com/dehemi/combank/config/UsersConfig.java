package com.dehemi.combank.config;

import com.dehemi.combank.dao.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "users")
@Data
public class UsersConfig {
    Map<String, User> users;
}

