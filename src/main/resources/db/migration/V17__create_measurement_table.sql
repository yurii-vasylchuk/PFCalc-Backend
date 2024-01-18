CREATE SEQUENCE measurement_id_seq INCREMENT BY 1;

CREATE TABLE measurement
(
    id                 BIGINT        NOT NULL PRIMARY KEY DEFAULT NEXT VALUE FOR measurement_id_seq,
    food_id            BIGINT,
    to_gram_multiplier DECIMAL(9, 4) NOT NULL,
    name               VARCHAR(255)  NOT NULL,
    default_value      DECIMAL(9, 4) NOT NULL,
    FOREIGN KEY (food_id) REFERENCES pfcc.food (id)
)