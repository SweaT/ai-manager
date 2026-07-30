package manager.chat.api.dto;

import manager.rag.model.KnowledgeChunk;

import java.util.List;

public record ChatResponse(
        String sessionId,
        String answer,
        List<KnowledgeChunk> sources
) {
}
