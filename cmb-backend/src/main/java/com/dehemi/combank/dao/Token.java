package com.dehemi.combank.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    String accessToken;
}
