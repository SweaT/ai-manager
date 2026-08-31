package manager.dto.chat.request;

public record ChatRequest(
        String sessionId,
        String message
) {
}
