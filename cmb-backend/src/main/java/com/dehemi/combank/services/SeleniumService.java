package com.dehemi.combank.services;

import com.dehemi.combank.config.SeleniumConfig;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
public class SeleniumService {
    final SeleniumConfig config;

    public SeleniumService(SeleniumConfig config) {
        this.config = config;
    }

    public ChromeDriver getInstance() {
        ChromeOptions options = new ChromeOptions();
        for(String f : this.config.getFlags()) {
            options.addArguments(f);
        }

        return new ChromeDriver(options);
    }
}
