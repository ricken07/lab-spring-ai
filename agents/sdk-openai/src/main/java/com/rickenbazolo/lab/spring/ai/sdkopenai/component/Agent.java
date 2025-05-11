package com.rickenbazolo.lab.spring.ai.sdkopenai.component;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.Assert;

import java.util.List;

public class Agent {

    private final String name;
    private final String instruction;
    private final List<Object> tools;
    private final List<Object> mcpTools;
    private final ChatClient.Builder chatClientBuilder;

    public Agent(String name, String instruction, ChatClient.Builder chatClientBuilder) {
        this(name, instruction, null, null, chatClientBuilder);
    }

    public Agent(String name, String instruction, List<Object> tools, List<Object> mcpTools, ChatClient.Builder chatClientBuilder) {
        this.name = name;
        this.instruction = instruction;
        this.tools = tools;
        this.mcpTools = mcpTools;
        this.chatClientBuilder = chatClientBuilder;
    }

    public String run() {
        Assert.notNull(chatClientBuilder, "chatClientBuilder must not be null");
        Assert.notNull(instruction, "instruction must not be null");
        Assert.notNull(name, "name must not be null");

        if (this.mcpTools != null && !this.mcpTools.isEmpty()) {
            this.chatClientBuilder.defaultTools(this.mcpTools);
        }
        if (this.tools != null && !this.tools.isEmpty()) {
            this.chatClientBuilder.defaultTools(this.tools);
        }

        this.chatClientBuilder.defaultSystem(this.instruction);

        return this.chatClientBuilder.build()
                .prompt()
                .call()
                .content();
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String instruction;
        private List<Object> tools;
        private List<Object> mcpTools;
        private ChatClient.Builder chatClientBuilder;

        private Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder instruction(String instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder tools(List<Object> tools) {
            this.tools = tools;
            return this;
        }

        public Builder mcpTools(List<Object> mcpTools) {
            this.mcpTools = mcpTools;
            return this;
        }
        public Builder chatClientBuilder(ChatClient.Builder chatClientBuilder) {
            this.chatClientBuilder = chatClientBuilder;
            return this;
        }

        public Agent build() {
            return new Agent(name, instruction, tools, mcpTools, chatClientBuilder);
        }
    }


}
