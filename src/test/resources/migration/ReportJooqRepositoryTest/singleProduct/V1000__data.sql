INSERT INTO users (id, email, preferred_language, protein_aim, fat_aim, carbohydrates_aim, calories_aim,
                   email_confirmed, password, roles, name)
VALUES (1, 'test1@mail.com', 'EN', 120, 50, 180, 1300, TRUE, 'test', 'USER', 'test1');

INSERT INTO food (id, name, type, protein, fat, carbohydrates, calories, is_hidden, owner_id, description, deleted)
VALUES (1, 'Product 1 u1', 'INGREDIENT', 24, 6, 12, 240, FALSE, 1, NULL, FALSE);

INSERT INTO meal (id, weight_in_gram, protein, fat, carbohydrates, calories, food_id, eaten_on, owner_id)
VALUES (1, 200, 48, 12, 24, 480, 1, '2024-01-05 10:15:34', 1);
