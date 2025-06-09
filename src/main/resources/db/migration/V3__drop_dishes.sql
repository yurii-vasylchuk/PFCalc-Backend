DELETE FROM meal m WHERE m.dish_id IS NOT NULL;

ALTER TABLE meal DROP COLUMN dish_id;

DROP TABLE dish_ingredients, dish;
