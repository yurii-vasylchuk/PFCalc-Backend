ALTER TABLE security_tokens
    ADD COLUMN created_at  TIMESTAMP NOT NULL,
    ADD COLUMN modified_at TIMESTAMP NOT NULL;
