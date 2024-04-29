CREATE SEQUENCE report_id_seq INCREMENT BY 1;

CREATE TABLE reports
(
    id         BIGINT PRIMARY KEY           DEFAULT NEXTVAL(report_id_seq),
    user_id    BIGINT              NOT NULL,
    name       VARCHAR(255) UNIQUE NOT NULL,
    file_path  VARCHAR(255) UNIQUE,
    status     VARCHAR(30)         NOT NULL,
    type       VARCHAR(30)         NOT NULL,
    created_at TIMESTAMP           NOT NULL DEFAULT NOW(),

    FOREIGN KEY (user_id) REFERENCES users (id)
);
