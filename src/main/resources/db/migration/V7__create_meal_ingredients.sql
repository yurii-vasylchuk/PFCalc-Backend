CREATE TABLE meal_ingredients
(
    meal_id            BIGINT        NOT NULL REFERENCES meal (id),
    ingredient_id      BIGINT        NOT NULL REFERENCES food (id),
    measurement_id     BIGINT REFERENCES measurement (id),
    measurement_weight DECIMAL(9, 4),
    measurement_name   VARCHAR(255),
    weight_in_gram     DECIMAL(9, 4) NOT NULL,
    ingredient_index   BIGINT        NOT NULL,

    PRIMARY KEY (meal_id, ingredient_id)
);
