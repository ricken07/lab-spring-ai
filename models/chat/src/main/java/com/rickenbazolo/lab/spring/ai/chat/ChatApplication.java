package com.rickenbazolo.lab.spring.ai.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ChatClient.Builder chatClientBuilder) {
        return _ -> {
            var chatClient = chatClientBuilder.build();
            var response = chatClient.prompt("Peux-tu m'expliquer brièvement le fonctionnement des LLM ?").call().content();
            System.out.println(response);
        };
    }

}
