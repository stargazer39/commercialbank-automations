package com.dehemi.combank.clients;

import com.dehemi.combank.config.GotifyConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class Gotify {
    private final RestClient restClient;

    public Gotify(GotifyConfig gotifyConfig, RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(gotifyConfig.getEndpoint()).build();
    }

    public void sendNotification(String title, String message, int priority, String token) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("title", title);
        formData.add("message", message);
        formData.add("priority", String.valueOf(priority));

        this.restClient.post()
                .uri("/message?token={token}", token)
                .body(formData)
                .retrieve().toBodilessEntity();
    }
}
