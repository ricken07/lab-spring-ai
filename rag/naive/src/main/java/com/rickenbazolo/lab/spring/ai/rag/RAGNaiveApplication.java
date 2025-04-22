package com.rickenbazolo.lab.spring.ai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class RAGNaiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(RAGNaiveApplication.class, args);
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
            var chatClient = chatClientBuilder.build();
            try(var scanner = new Scanner(System.in)) {
                do {
                    System.out.print("USER: ");
                    input = scanner.nextLine();
                    var response = rag(input, vectorStore, chatClient);
                    System.out.printf("ASSISTANT: %s\n%n", response);
                } while (!input.equals("stop"));
            }

        };
    }

    private String rag(String input, VectorStore vectorStore, ChatClient chatClient) {
        // 1 - Search for similar documents in the vector store
        var context = vectorStore.similaritySearch(SearchRequest.builder()
                .query(input)
                .similarityThreshold(0.7) // Seuil de similarité pour filtrer la réponse de la recherche.
                .topK(2) // les "k" premiers résultats similaires à renvoyer.
                .build());

        var systemMessage = new SystemPromptTemplate("""
                Context information is below.
                CONTEXT: {context}
                Given the context information and not prior knowledge, answer the question in the same language.
                QUESTION: {question}
                """).createMessage(Map.of("question", input, "context", context));

        var userMessage = new UserMessage(input);

        var prompt = new Prompt(List.of(systemMessage, userMessage));

        return chatClient.prompt(prompt).call().content();
    }

}
