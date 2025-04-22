package com.rickenbazolo.lab.spring.ai.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ChatClient.Builder chatClientBuilder,
                             List<McpSyncClient> mcpSyncClients) {
        return _ -> {
            var chatClient = chatClientBuilder
                    .defaultSystem("""
                            Vous êtes un assistant utile et pouvez effectuer des recherches sur le web pour répondre à vos questions.
                            """)
                    .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
                    .defaultAdvisors(
                            new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                            new SimpleLoggerAdvisor()
                    )
                    .build();

            String input = "";
            try(var scanner = new Scanner(System.in)) {
                do {
                    System.out.print("USER: ");
                    input = scanner.nextLine();
                    System.out.printf("ASSISTANT: %s\n%n", chatClient.prompt(input).call().content());
                } while (!input.equals("stop"));
            }

        };
    }

}
