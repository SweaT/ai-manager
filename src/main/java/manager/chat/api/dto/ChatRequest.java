package manager.chat.api.dto;

public record ChatRequest(
        String sessionId,
        String message
) {
}
