ALTER TABLE meal
    RENAME COLUMN weight TO weight_in_gram;

ALTER TABLE meal
    ADD COLUMN measurement_id     BIGINT REFERENCES measurement (id),
    ADD COLUMN measurement_name   VARCHAR(255),
    ADD COLUMN measurement_weight DECIMAL(9, 4);

ALTER TABLE food_ingredients
    RENAME COLUMN ingredient_weight TO weight_in_gram;

ALTER TABLE food_ingredients
    ADD COLUMN measurement_id     BIGINT REFERENCES measurement (id),
    ADD COLUMN measurement_name   VARCHAR(255),
    ADD COLUMN measurement_weight DECIMAL(9, 4);
