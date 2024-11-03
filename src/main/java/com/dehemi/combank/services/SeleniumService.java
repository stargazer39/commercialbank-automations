package com.dehemi.combank.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class SeleniumService {
    public ChromeDriver getInstance() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        return new ChromeDriver(options);
    }
}
