CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS rag_knowledge_chunk (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    source_type varchar(100) NOT NULL,
    source_id varchar(200),
    title varchar(500) NOT NULL,
    content text NOT NULL,
    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
    embedding vector(768) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS rag_knowledge_chunk_embedding_hnsw_idx
    ON rag_knowledge_chunk USING hnsw (embedding vector_cosine_ops);

CREATE INDEX IF NOT EXISTS rag_knowledge_chunk_source_idx
    ON rag_knowledge_chunk (source_type, source_id);

CREATE TABLE IF NOT EXISTS chat_session (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    external_user_id varchar(200),
    state varchar(100) NOT NULL DEFAULT 'NEW_MESSAGE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chat_message (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id uuid NOT NULL REFERENCES chat_session(id) ON DELETE CASCADE,
    role varchar(30) NOT NULL,
    content text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS chat_message_session_created_idx
    ON chat_message (session_id, created_at);
