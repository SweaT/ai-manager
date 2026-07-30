package manager.ollama;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class OllamaClient {

    private final WebClient webClient;
    private final String chatModel;
    private final String embeddingModel;

    public OllamaClient(
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

    private record EmbedRequest(String model, String input) {
    }

    private record EmbedResponse(List<List<Double>> embeddings) {
    }

    private record ChatRequest(String model, List<Message> messages, boolean stream) {

        private static ChatRequest forUserPrompt(String model, String prompt) {
            return new ChatRequest(model, List.of(new Message("user", prompt)), false);
        }
    }

    private record ChatResponse(Message message) {
    }

    private record Message(String role, String content) {
    }
}
