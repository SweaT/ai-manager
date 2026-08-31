package manager.service;

import manager.dto.ollama.request.ChatRequest;
import manager.dto.ollama.request.EmbedRequest;
import manager.dto.ollama.response.ChatResponse;
import manager.dto.ollama.response.EmbedResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Сервис отвечает за взаимодействие с api Ollama
 */
@Service
public class OllamaService {

    private final WebClient webClient;
    private final String chatModel;
    private final String embeddingModel;

    public OllamaService(
            WebClient.Builder webClientBuilder,
            @Value("${spring.ai.ollama.base-url}") String baseUrl,
            @Value("${spring.ai.ollama.chat.model}") String chatModel,
            @Value("${spring.ai.ollama.embedding.model}") String embeddingModel
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    public Mono<List<Double>> embed(String input) {
        return webClient.post()
                .uri("/api/embed")
                .bodyValue(new EmbedRequest(embeddingModel, input))
                .retrieve()
                .bodyToMono(EmbedResponse.class)
                .map(response -> response.embeddings().getFirst());
    }

    public Mono<String> chat(String prompt) {
        return webClient.post()
                .uri("/api/chat")
                .bodyValue(ChatRequest.forUserPrompt(chatModel, prompt))
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(response -> response.message().content());
    }

}
