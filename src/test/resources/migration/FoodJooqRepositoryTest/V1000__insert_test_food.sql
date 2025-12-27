INSERT INTO users (id, email, name, preferred_language, protein_aim,
                   fat_aim, carbohydrates_aim, calories_aim,
                   email_confirmed, password, roles)
VALUES (1,
        'yva@test.com',
        'yva1',
        'UA',
        120,
        50,
        200,
        1000,
        TRUE,
        'pass',
        'USER'),
       (2,
        '2@test.com',
        'yva2',
        'UA',
        130,
        60,
        400,
        1500,
        TRUE,
        'pass2',
        'USER');
INSERT INTO food
(id, name, type, protein, fat, carbohydrates, calories, is_hidden, owner_id, description, deleted)
VALUES (1, 'food1', 'INGREDIENT', 30, 50, 90, 200, FALSE, 1, 'description', FALSE),
       (2, 'food2', 'INGREDIENT', 40, 60, 40, 250, TRUE, 2, 'description', FALSE),
       (3, 'food3', 'RECIPE', 35, 55, 95, 250, FALSE, 1, 'description', FALSE),
       (4, 'food4', 'RECIPE', 45, 65, 65, 280, FALSE, 1, 'description', FALSE),
       (5, 'food5', 'RECIPE', 36, 56, 96, 260, FALSE, 1, 'description', TRUE),
       (6, 'food6', 'RECIPE', 66, 57, 97, 270, FALSE, 1, 'description', TRUE);

INSERT INTO meal
(id, weight_in_gram, protein, fat, carbohydrates, calories, food_id, eaten_on, owner_id)
VALUES (1, 51, 21, 31, 41, 150, 1, now() + (1 - extract(dow from now())) * interval '1 day', 1),
       (2, 52, 22, 32, 42, 150, 2, '2023-08-11', 2),
       (3, 53, 23, 33, 43, 150, 1, '2023-08-09', 1),
       (4, 54, 24, 34, 44, 150, 3, now() + (1 - extract(dow from now())) * interval '1 day', 2),
       (5, 55, 25, 35, 45, 150, 2, now() + (1 - extract(dow from now())) * interval '1 day', 1),
       (6, 56, 26, 36, 46, 150, 2, now() + (1 - extract(dow from now())) * interval '1 day', 2);
