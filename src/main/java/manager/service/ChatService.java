package manager.service;

import manager.dto.chat.request.ChatRequest;
import manager.dto.chat.response.ChatResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Сервис отвечает за непосредственный чат с клиентом
 */
@Service
public class ChatService {

    private final RagService ragService;
    private final OllamaService ollamaService;

    public ChatService(RagService ragService, OllamaService ollamaService) {
        this.ragService = ragService;
        this.ollamaService = ollamaService;
    }

    public Mono<ChatResponse> answer(ChatRequest request) {
        String sessionId = StringUtils.isBlank(request.sessionId())
                ? UUID.randomUUID().toString()
                : request.sessionId();

        return ragService.retrieve(request.message())
                .collectList()
                .flatMap(chunks -> ollamaService.chat(ragService.buildPrompt(request.message(), chunks))
                        .map(answer -> new ChatResponse(sessionId, answer, chunks)));
    }
}
