package com.rickenbazolo.lab.spring.ai.fc;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class MethodAsTools {

    private final DemoService demoService;

    public MethodAsTools(DemoService demoService) {
        this.demoService = demoService;
    }

    @Tool(description = "Get current Java version")
    String getCurrentJavaVersion() {
        System.out.println("Call getCurrentJavaVersion tool");
        return demoService.getCurrentJavaVersion();
    }
}
