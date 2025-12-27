INSERT INTO users (id, email, preferred_language, protein_aim, fat_aim, carbohydrates_aim, calories_aim,
                   email_confirmed, password, roles, name)
VALUES (1, 'test1@mail.com', 'EN', 120, 50, 180, NULL, TRUE, 'test', 'USER', 'test1'),
       (2, 'test2@mail.com', 'EN', 120, 50, 180, NULL, TRUE, 'test', 'USER', 'test2');

INSERT INTO food (id, name, type, protein, fat, carbohydrates, calories, owner_id, is_hidden, deleted)
VALUES (1, 'Food1', 'INGREDIENT', 23.0, 3.8, 14.5, 285, 1, TRUE, FALSE),-- owned INGREDIENT
       (2, 'Food2', 'INGREDIENT', 5.2, 5.8, 77.5, 320, 1, FALSE, FALSE),-- owned INGREDIENT
       (3, 'Food3', 'RECIPE', 34, 12, 55, 301, 1, TRUE, FALSE),-- owned RECIPE
       (4, 'Food4', 'INGREDIENT', 1, 2, 3, 4, 2, TRUE, FALSE),-- not owned, hidden
       (5, 'Food5', 'INGREDIENT', 1, 2, 3, 4, 2, FALSE, FALSE),-- not owned, not hidden
       (6, 'Food6', 'INGREDIENT', 1, 2, 3, 4, 1, FALSE, TRUE),-- owned, deleted
       (7, 'Food7', 'INGREDIENT', 1, 2, 3, 4, 2, FALSE, TRUE),-- not owned, deleted
       (8, 'Food8', 'RECIPE', 1, 2, 3, 4, 2, FALSE, FALSE);-- not owned RECIPE

INSERT INTO food_ingredients (recipe_id, ingredient_id, weight_in_gram, ingredient_index, is_default)
VALUES (3, 1, 100,1, true),
       (3, 2, 200,2, true),
       (8, 6, 120,3, true),
       (8, 7, 55,4, true);
