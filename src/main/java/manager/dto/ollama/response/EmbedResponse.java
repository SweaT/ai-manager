package manager.dto.ollama.response;

import java.util.List;

public record EmbedResponse(
        List<List<Double>> embeddings) {
}
