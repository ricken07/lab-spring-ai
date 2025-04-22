package com.rickenbazolo.lab.spring.ai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Scanner;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@SpringBootApplication
public class RAGNaiveWithAdvisorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RAGNaiveWithAdvisorsApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            JdbcTemplate jdbcTemplate,
            @Value("classpath:/data/recettes_cuisine_europe_africaine.txt") Resource file) {
        return _ -> {
            jdbcTemplate.update("delete from vector_store");

            // 1. Extract text from file
            var documents = new TextReader(file).read();

            // 2. Split text into tokens (chunks of text)
            var chunks = new TokenTextSplitter().apply(documents);

            // 3. Load to vector store
            //vectorStore.add(chunks);

            // Chat with file
            String input = "";
            var chatClient = chatClientBuilder
                    .defaultAdvisors(
                            new QuestionAnswerAdvisor(vectorStore), // RAG advisor
                            new PromptChatMemoryAdvisor(new InMemoryChatMemory()) // chat-memory advisor
                    )
                    .build();
            try(var scanner = new Scanner(System.in)) {
                do {
                    System.out.print("USER: ");
                    input = scanner.nextLine();
                    var response = chatClient.prompt()
                            .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1234")
                                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                            .user(input)
                            .call()
                            .content();
                    System.out.printf("ASSISTANT: %s\n%n", response);
                } while (!input.equals("stop"));
            }

        };
    }

}
