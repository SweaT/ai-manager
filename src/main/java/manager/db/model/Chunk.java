package manager.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chunk {
    private Long id;
    private Long documentId;
    private String content;
    private Long chunkIndex;
}
