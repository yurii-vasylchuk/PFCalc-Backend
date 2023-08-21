CREATE SEQUENCE food_id_seq INCREMENT BY 50;
CREATE SEQUENCE dish_id_seq INCREMENT BY 50;
CREATE SEQUENCE meal_id_seq INCREMENT BY 50;

CREATE TABLE food
(id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXT VALUE FOR food_id_seq,
name VARCHAR(255) NOT NULL,
type VARCHAR(10) NOT NULL,
protein DECIMAL(9,4) NOT NULL,
fat DECIMAL(9,4) NOT NULL,
carbohydrates DECIMAL(9,4) NOT NULL,
calories DECIMAL(9,4) NOT NULL,
is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
owner_id BIGINT,
description TEXT,
deleted BOOLEAN NOT NULL DEFAULT FALSE,
                FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE dish
(id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXT VALUE FOR dish_id_seq,
name VARCHAR(255) NOT NULL,
food_id BIGINT NOT NULL,
recipe_weight DECIMAL (9,4) NOT NULL,
cooked_weight DECIMAL(9,4) NOT NULL,
protein DECIMAL(9,4) NOT NULL,
fat DECIMAL(9,4) NOT NULL,
carbohydrates DECIMAL(9,4) NOT NULL,
calories DECIMAL(9,4) NOT NULL,
cooked_on TIMESTAMP DEFAULT NOW(),
deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       FOREIGN KEY (food_id) REFERENCES food(id)
);

CREATE TABLE meal
(id BIGINT NOT NULL PRIMARY KEY DEFAULT NEXT VALUE FOR meal_id_seq,
weight DECIMAL (9,4) NOT NULL,
protein DECIMAL(9,4) NOT NULL,
fat DECIMAL(9,4) NOT NULL,
carbohydrates DECIMAL(9,4) NOT NULL,
calories DECIMAL(9,4) NOT NULL,
food_id BIGINT NOT NULL,
dish_id BIGINT,
eaten_on TIMESTAMP DEFAULT NOW(),
                        FOREIGN KEY (food_id) REFERENCES food(id),
                        FOREIGN KEY (dish_id) REFERENCES dish(id)
);

CREATE TABLE ingredients
(
recipe_id BIGINT NOT NULL,
ingredient_id BIGINT NOT NULL,
ingredient_weight DECIMAL (9,4) NOT NULL,
                  PRIMARY KEY (recipe_id, ingredient_id),
                  FOREIGN KEY (recipe_id) REFERENCES food(id),
                  FOREIGN KEY (ingredient_id) REFERENCES food(id)
)