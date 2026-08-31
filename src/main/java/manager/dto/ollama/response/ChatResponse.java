package manager.dto.ollama.response;

import manager.dto.ollama.Message;

public record ChatResponse(
        Message message) {
}
