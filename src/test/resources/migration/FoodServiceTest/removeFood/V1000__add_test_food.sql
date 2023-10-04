INSERT INTO users (id, email, name, preferred_language, protein_aim,
                   fat_aim, carbohydrates_aim, calories_aim,
                   profile_configured, email_confirmed, password, roles)
VALUES (1,
        'yva@test.com',
        'yva1',
        'UA',
        120,
        50,
        200,
        1000,
        TRUE,
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
        TRUE,
        'pass2',
        'USER');

INSERT INTO food
(id, name, type, protein, fat, carbohydrates, calories, is_hidden, owner_id, description, deleted)
VALUES (1, 'food1', 'INGREDIENT', 30, 50, 90, 200, FALSE, 1, 'description', FALSE),
       (2, 'food2', 'INGREDIENT', 40, 60, 40, 250, FALSE, 2, 'description', FALSE),
       (3, 'food3', 'RECIPE', 35, 55, 95, 250, FALSE, 1, 'description', FALSE);
