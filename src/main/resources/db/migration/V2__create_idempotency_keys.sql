-- Flyway V2 migration: idempotency tracking
CREATE TABLE IF NOT EXISTS idempotency_keys (
  id BIGSERIAL PRIMARY KEY,
  idempotency_key VARCHAR(255) NOT NULL UNIQUE,
  response_body TEXT,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_idempotency_key ON idempotency_keys(idempotency_key);
