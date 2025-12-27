ALTER TABLE food_ingredients
    ADD COLUMN is_default BOOLEAN;

UPDATE food_ingredients
SET is_default = TRUE
WHERE 1 = 1;

ALTER TABLE food_ingredients
    ALTER COLUMN is_default SET NOT NULL;
