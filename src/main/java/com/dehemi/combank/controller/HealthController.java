package com.dehemi.combank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("health")
public class HealthController {
    @GetMapping("check")
    public String check() {
        return "health";
    }
}
