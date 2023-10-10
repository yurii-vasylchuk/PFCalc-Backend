CREATE TABLE dish_ingredients
(
    dish_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    ingredient_weight DECIMAL (9,4) NOT NULL,
    PRIMARY KEY (dish_id, ingredient_id),
    FOREIGN KEY (dish_id) REFERENCES dish(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
)