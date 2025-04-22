package com.rickenbazolo.lab.spring.ai.fc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class ToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsApplication.class, args);
    }

    @Bean
    CommandLineRunner cli(DemoService demoService) {
        return _ -> {
            String input = "";
            try(var scanner = new Scanner(System.in)) {
                do {
                    System.out.print("USER: ");
                    input = scanner.nextLine();
                    System.out.printf("ASSISTANT: %s\n%n", demoService.withTolls(input));
                } while (!input.equals("stop"));
            }

        };
    }

}
