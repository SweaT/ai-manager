package manager.dto.chat.response;

import manager.db.model.Chunk;

import java.util.List;

public record ChatResponse(
        String sessionId,
        String answer,
        List<Chunk> sources
) {
}
