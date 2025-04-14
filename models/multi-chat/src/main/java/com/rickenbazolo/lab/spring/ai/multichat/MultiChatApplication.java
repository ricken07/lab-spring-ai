package com.rickenbazolo.lab.spring.ai.multichat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MultiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiChatApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(MistralAiChatModel mistralAiChatModel,
                             OpenAiChatModel openAiChatModel) {
        return _ -> {
            var prompt = "Qui occupe actuellement le poste de président ?";
            // ========== Chat with Mistral AI model
            var chatClient = ChatClient.create(mistralAiChatModel);
            var response = chatClient.prompt(prompt).call().content();
            System.out.printf("MISTRAL MODEL RESPONSE : %s \n", response);

            // ========= Chat with OpenAI model
            chatClient = ChatClient.create(openAiChatModel);
            response = chatClient.prompt(prompt).call().content();
            System.out.printf("OPENAI MODEL RESPONSE : %s \n", response);
        };
    }

}
