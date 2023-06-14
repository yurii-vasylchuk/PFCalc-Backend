CREATE SEQUENCE user_id_seq INCREMENT BY 50;

CREATE TABLE users
( id BIGINT NOT NULL PRIMARY KEY,
email VARCHAR(255) NOT NULL UNIQUE,
preferred_language VARCHAR(3) NOT NULL,
protein_aim DECIMAL(9,4) NOT NULL,
fat_aim DECIMAL(9,4) NOT NULL,
carbohydrates_aim DECIMAL(9,4) NOT NULL,
calories_aim DECIMAL(9,4) NOT NULL,
profile_configured BOOLEAN,
email_confirmed BOOLEAN
);