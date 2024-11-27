package com.dehemi.combank.clients;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.KeyCredential;
import com.dehemi.combank.config.OpenAIConfig;
import com.dehemi.combank.dao.AITransaction;
import com.dehemi.combank.dao.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OpenAI {
    private OpenAIClient openAIClient;
    private OpenAIConfig openAIConfig;

    public OpenAI(OpenAIConfig openAIConfig) {
        openAIClient = new OpenAIClientBuilder()
                .credential(new KeyCredential(openAIConfig.getSecret()))
                .buildClient();
        this.openAIConfig = openAIConfig;
    }

    public List<AITransaction> categorizeTransactions(List<Transaction> transactionList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        log.info("tags batch list - {}",transactionList.size());
        String listOfTransactionJSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transactionList.stream().map(AITransaction::from));
        String prompt = String.format(openAIConfig.getTransactionCategorizePrompt(),listOfTransactionJSON);

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(prompt));
        log.info("prompt {}",prompt);
        ChatCompletions completions = openAIClient.getChatCompletions("gpt-4o-mini-2024-07-18", new ChatCompletionsOptions(chatMessages));
        String response = completions.getChoices().stream().map(chatChoice -> chatChoice.getMessage().getContent()).collect(Collectors.joining("|"));
        log.info("gpt said {}",response);

        ObjectMapper objectMapper2 = new ObjectMapper();
        return objectMapper2.readValue(response, new TypeReference<>() {});
    }
}
