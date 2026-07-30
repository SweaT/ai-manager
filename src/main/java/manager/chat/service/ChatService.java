package manager.chat.service;

import manager.chat.api.dto.ChatRequest;
import manager.chat.api.dto.ChatResponse;
import manager.ollama.OllamaClient;
import manager.rag.RagService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ChatService {

    private final RagService ragService;
    private final OllamaClient ollamaClient;

    public ChatService(RagService ragService, OllamaClient ollamaClient) {
        this.ragService = ragService;
        this.ollamaClient = ollamaClient;
    }

    public Mono<ChatResponse> answer(ChatRequest request) {
        String sessionId = request.sessionId() == null || request.sessionId().isBlank()
                ? UUID.randomUUID().toString()
                : request.sessionId();

        return ragService.retrieve(request.message())
                .collectList()
                .flatMap(chunks -> ollamaClient.chat(ragService.buildPrompt(request.message(), chunks))
                        .map(answer -> new ChatResponse(sessionId, answer, chunks)));
    }
}
