package com.rickenbazolo.lab.spring.ai.rag;

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

@SpringBootApplication
public class AdvancedRAGApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedRAGApplication.class, args);
    }

    @Bean
    CommandLineRunner cli(
            VectorStore vectorStore,
            RagService ragService,
            JdbcTemplate jdbcTemplate,
            @Value("classpath:/data/recettes_cuisine_europe_africaine.txt") Resource file) {
        return _ -> {
            jdbcTemplate.update("delete from vector_store");

            // 1. Extract text from file
            var documents = new TextReader(file).read();

            // 2. Split text into tokens (chunks of text)
            var chunks = new TokenTextSplitter().apply(documents);

            // 3. Load to vector store
            vectorStore.add(chunks);

            // Chat with file
            String input = "";
            try(var scanner = new Scanner(System.in)) {
                do {
                    System.out.print("USER: ");
                    input = scanner.nextLine();
                    System.out.printf("ASSISTANT: %s\n%n", ragService.withQueryRewrite(input));
                } while (!input.equals("stop"));
            }

        };
    }

}
