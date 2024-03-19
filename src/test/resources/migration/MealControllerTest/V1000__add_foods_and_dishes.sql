INSERT INTO users (id, email, preferred_language, protein_aim, fat_aim, carbohydrates_aim, calories_aim,
                   email_confirmed, password, roles, name)
VALUES (1, 'test1@mail.com', 'EN', 120, 50, 180, null, 1, 'test', 'USER', 'test1'),
       (2, 'test2@mail.com', 'EN', 120, 50, 180, null, 1, 'test', 'USER', 'test2');

INSERT INTO food (id, name, type, protein, fat, carbohydrates, calories, owner_id, is_hidden, deleted)
VALUES (1, 'Food1', 'INGREDIENT', 23.0, 3.8, 14.5, 285, 1, TRUE, FALSE),#owned INGREDIENT
       (2, 'Food2', 'INGREDIENT', 5.2, 5.8, 77.5, 320, 1, FALSE, FALSE),#owned INGREDIENT
       (3, 'Food3', 'RECIPE', 34, 12, 55, 301, 1, TRUE, FALSE),#owned RECIPE
       (4, 'Food4', 'INGREDIENT', 1, 2, 3, 4, 2, TRUE, FALSE),#not owned, hidden
       (5, 'Food5', 'INGREDIENT', 1, 2, 3, 4, 2, FALSE, FALSE),#not owned, not hidden
       (6, 'Food6', 'INGREDIENT', 1, 2, 3, 4, 1, FALSE, TRUE),#owned, deleted
       (7, 'Food7', 'INGREDIENT', 1, 2, 3, 4, 2, FALSE, TRUE),#not owned, deleted
       (8, 'Food8', 'RECIPE', 1, 2, 3, 4, 2, FALSE, FALSE);#not owned RECIPE

INSERT INTO ingredients (recipe_id, ingredient_id, ingredient_weight, ingredient_index)
VALUES (3, 1, 100,1),
       (3, 2, 200,2),
       (8, 6, 120,3),
       (8, 7, 55,4);

INSERT INTO dish(id, name, food_id, recipe_weight, cooked_weight, protein, fat, carbohydrates, calories, owner_id,
                 deleted)
VALUES (1, 'Dish1', 3, 100, 100, 23.0, 3.8, 14.5, 285, 1, FALSE),#from own recipe, not deleted
       (2, 'Dish2', 8, 200, 200, 5.2, 5.8, 77.5, 320, 1, FALSE),#from not own recipe, not deleted
       (3, 'Dish3', 3, 300, 300, 34, 12, 55, 301, 1, TRUE),#from own recipe, deleted
       (4, 'Dish4', 8, 400, 400, 1, 2, 3, 4, 1, TRUE),#from not own recipe, deleted
       (5, 'Dish5', 3, 500, 500, 1, 2, 3, 4, 2, FALSE),#not owned, by user's recipe not deleted
       (6, 'Dish5', 8, 500, 500, 1, 2, 3, 4, 2, FALSE),#not owned, not deleted
       (7, 'Dish6', 8, 600, 600, 1, 2, 3, 4, 2, TRUE);#not owned, deleted
