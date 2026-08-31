package manager.dto.ollama;

public record Message(
        String role,
        String content) {
}
