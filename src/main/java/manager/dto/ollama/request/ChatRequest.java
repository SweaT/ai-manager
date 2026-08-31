package manager.dto.ollama.request;

import manager.dto.ollama.Message;

import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages,
        boolean stream) {

    public static ChatRequest forUserPrompt(String model, String prompt) {
        return new ChatRequest(model, List.of(new Message("user", prompt)), false);
    }
}
