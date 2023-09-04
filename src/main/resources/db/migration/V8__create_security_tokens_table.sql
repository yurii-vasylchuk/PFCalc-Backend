CREATE SEQUENCE security_token_id_seq INCREMENT BY 50;

CREATE TABLE security_tokens
(
    id        BIGINT       NOT NULL PRIMARY KEY DEFAULT NEXT VALUE FOR security_token_id_seq,
    code      VARCHAR(255) NOT NULL UNIQUE,
    user_id   BIGINT REFERENCES users (id),
    type      VARCHAR(30)  NOT NULL,
    is_active BOOLEAN
);
