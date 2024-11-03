package com.dehemi.combank.dao;

import lombok.Data;

@Data
public class User {
    String username;
    String password;
    Combank combank;

    @Data
    public static class Combank {
        String username;
        String password;
    }
}
