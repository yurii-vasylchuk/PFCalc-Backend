INSERT INTO users (id, email, preferred_language, protein_aim, fat_aim, carbohydrates_aim, calories_aim,
                   email_confirmed, password, roles, name)
VALUES (1, 'test1@mail.com', 'UA', 120, 50, 180, 1300, 1, 'test', 'USER', 'test1');

INSERT INTO food (id, name, type, protein, fat, carbohydrates, calories, is_hidden, owner_id, description, deleted)
VALUES (1, 'Recipe 1',  'RECIPE',     18, 11, 15, 293, FALSE, 1, NULL, FALSE),
       (2, 'Product 2', 'INGREDIENT', 24, 6,  12, 240, FALSE, 1, NULL, FALSE),
       (3, 'Product 3', 'INGREDIENT', 18, 9,  6,  189, FALSE, 1, NULL, FALSE),
       (4, 'Product 4', 'INGREDIENT', 10, 14, 22, 300, FALSE, 1, NULL, FALSE);

INSERT INTO ingredients (recipe_id, ingredient_id, ingredient_weight, ingredient_index)
VALUES (1, 2, 100, 1),
       (1, 3, 200, 2),
       (1, 4, 300, 3);

INSERT INTO meal (id, weight, protein, fat, carbohydrates, calories, food_id, dish_id, eaten_on, owner_id)
VALUES (1, 200, 36, 22, 30, 586, 1, NULL, '2024-01-05 10:15:34', 1);
