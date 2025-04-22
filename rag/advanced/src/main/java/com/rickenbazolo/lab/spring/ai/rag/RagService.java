package com.rickenbazolo.lab.spring.ai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final Resource rewritePrompt;

    public RagService(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            @Value("classpath:/data/rewrite-qa.st") Resource rewritePrompt) {
        this.rewritePrompt = rewritePrompt;
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor())
                .build();
    }

    public String withQueryRewrite(String input) {
        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(chatClient.mutate())
                        .promptTemplate(new PromptTemplate(rewritePrompt))
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(advisor)
                .advisors(ad -> ad.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1234")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .user(input)
                .call()
                .content();
    }

    public String withQueryExpansion(String input) {
        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(chatClient.mutate())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(advisor)
                .advisors(ad -> ad.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1234")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .user(input)
                .call()
                .content();
    }

    public String withQueryCompression(String input) {
        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(CompressionQueryTransformer.builder()
                        .chatClientBuilder(chatClient.mutate())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(
                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()), advisor)
                .advisors(ad -> ad.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1234")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .user(input)
                .call()
                .content();
    }
}
