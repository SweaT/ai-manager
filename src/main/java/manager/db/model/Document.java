package manager.db.model;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public record Document(
        Long id,
        String title,
        String sourcePath,
        OffsetDateTime createdAt
){}
