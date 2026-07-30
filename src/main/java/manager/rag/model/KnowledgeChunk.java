package manager.rag.model;

import java.util.UUID;

public record KnowledgeChunk(
        UUID id,
        String sourceType,
        String sourceId,
        String title,
        String content,
        double similarity
) {
}
