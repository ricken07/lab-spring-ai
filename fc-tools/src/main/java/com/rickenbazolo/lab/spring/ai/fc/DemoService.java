package com.rickenbazolo.lab.spring.ai.fc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Component;

@Component
public class DemoService {

    private final ChatClient chatClient;

    public DemoService(
            ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new SimpleLoggerAdvisor())
                .build();
    }

    public String withTolls(String input) {
        return chatClient.prompt(input)
                .tools("getUserAccountByName", "getCurrentDateTime")
                .tools(new MethodAsTools())
                .call()
                .content();
    }
}
