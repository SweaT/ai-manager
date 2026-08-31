CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE document (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    source_path TEXT NOT NULL DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chunk (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    chunk_index INT,
--     768 т.к. использую nomic-embed-text (она требует такую размерность)
    embedding vector(768) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Создаем индекс hnsw чтобы добиться O(log N) при поиске вектора
CREATE INDEX IF NOT EXISTS chunk_embedding_hnsw_idx
    ON chunk USING hnsw (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS chat_session (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    external_user_id varchar(200),
    --NEW_MESSAGE_00 в соотв. с ssm state
    state varchar(100) NOT NULL DEFAULT 'NEW_MESSAGE_00',
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

CREATE INDEX IF NOT EXISTS chat_session_updated_at_idx
    ON chat_session (updated_at);
