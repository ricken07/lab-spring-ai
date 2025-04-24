package com.rickenbazolo.lab.spring.ai.fc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final ChatClient chatClient;

    private final DemoService demoService;

    public DemoController(
            ChatClient.Builder chatClientBuilder, DemoService demoService) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new SimpleLoggerAdvisor())
                .build();
        this.demoService = demoService;
    }

    @PostMapping("/question")
    public String ask(@RequestBody Question body) {
        return chatClient.prompt(body.input())
                .tools("getUserAccountByName", "getCurrentDateTime")
                .tools(new MethodAsTools(this.demoService))
                .call()
                .content();
    }

    public record Question(String input) {}
}
