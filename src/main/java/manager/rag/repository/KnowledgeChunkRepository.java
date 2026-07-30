package manager.rag.repository;

import manager.rag.model.KnowledgeChunk;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class KnowledgeChunkRepository {

    private final DatabaseClient databaseClient;

    public KnowledgeChunkRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Flux<KnowledgeChunk> findSimilar(List<Double> embedding, int limit) {
        String vector = embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        return databaseClient.sql("""
                        SELECT id,
                               source_type,
                               source_id,
                               title,
                               content,
                               1 - (embedding <=> CAST(:embedding AS vector)) AS similarity
                        FROM rag_knowledge_chunk
                        ORDER BY embedding <=> CAST(:embedding AS vector)
                        LIMIT :limit
                        """)
                .bind("embedding", vector)
                .bind("limit", limit)
                .map((row, metadata) -> new KnowledgeChunk(
                        row.get("id", UUID.class),
                        row.get("source_type", String.class),
                        row.get("source_id", String.class),
                        row.get("title", String.class),
                        row.get("content", String.class),
                        row.get("similarity", Double.class)
                ))
                .all();
    }
}
