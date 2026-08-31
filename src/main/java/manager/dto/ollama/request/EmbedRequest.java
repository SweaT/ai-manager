package manager.dto.ollama.request;

public record EmbedRequest(
        String model,
        String input) {
}
