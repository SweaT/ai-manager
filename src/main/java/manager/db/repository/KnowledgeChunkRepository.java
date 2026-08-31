package manager.db.repository;

import lombok.RequiredArgsConstructor;
import manager.db.model.Chunk;
import manager.db.model.Document;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

import static manager.utils.RagUtils.getStringVector;

@Repository
@RequiredArgsConstructor
public class KnowledgeChunkRepository {

    private final DatabaseClient databaseClient;

    public Flux<Chunk> findSimilar(List<Double> embedding, int limit) {
        return databaseClient.sql("""
                        SELECT id,
                               document_id,
                               content,
                               chunk_index,
                               embedding,
                               created_at,
                               updated_at,
                               1 - (embedding <=> CAST(:embedding AS vector)) AS similarity
                        FROM chunk
                        ORDER BY embedding <=> CAST(:embedding AS vector)
                        LIMIT :limit
                        """)
                .bind("embedding", getStringVector(embedding))
                .bind("limit", limit)
                .map((row, metadata) -> new Chunk(
                        row.get("id", Long.class),
                        row.get("document_id", Long.class),
                        row.get("content", String.class),
                        row.get("chunk_index", Long.class)
                ))
                .all();
    }

    public Mono<Chunk> uploadChunk(Chunk chunk, List<Double> embedding) {
        long chunkIndex = chunk.getChunkIndex() == null
                ? 1
                : chunk.getChunkIndex();

        return databaseClient.sql("""
                        INSERT INTO chunk (document_id, content, chunk_index, embedding) 
                        VALUES ($1, $2, $3, CAST($4 AS vector))
                        RETURNING *
                        """)
                .bind("$1", chunk.getDocumentId())
                .bind("$2", chunk.getContent())
                .bind("$3", chunkIndex)
                .bind("$4", getStringVector(embedding))
                .map((row, metadata) -> new Chunk(
                        row.get("id", Long.class),
                        row.get("document_id", Long.class),
                        row.get("content", String.class),
                        row.get("chunk_index", Long.class))
                )
                .one();
    }

    public Mono<Document> uploadDocument(String title, String sourcePath) {
        return databaseClient.sql("""
                        INSERT INTO document (title, source_path) 
                        VALUES ($1, $2)
                        RETURNING *
                        """)
                .bind("$1", title)
                .bind("$2", sourcePath)
                .map((row, metadata) -> new Document(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("source_path", String.class),
                        row.get("created_at", OffsetDateTime.class))
                )
                .one();
    }

}
