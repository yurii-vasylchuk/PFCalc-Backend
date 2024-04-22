INSERT INTO pfcc.users (id, email, preferred_language, protein_aim, fat_aim, carbohydrates_aim, calories_aim,
                        email_confirmed, password, roles, name)
VALUES (50001, 'yriy.vasilchuk@gmail.com', 'UA', 130.0000, 50.0000, 160.0000, NULL, 1,
        '$2a$10$cu8AyzJ7kkLecns3MAnFYeakQDzGfPSXbCo7kbWgZVWfT6wh.N93O', 'USER,ADMIN', 'Юрій -_.Vasylchuk');

INSERT INTO pfcc.food (id, name, type, protein, fat, carbohydrates, calories, is_hidden, owner_id, description, deleted)
VALUES (11,  'Рис',                     'INGREDIENT', 6.0600,  0.5300,   72.6800, 330.0000,  0, 50001, NULL, 0),
       (26,  'Олія ',                   'INGREDIENT', 0.0000,  100.0000, 0.0000,  884.0000,  0, 50001, NULL, 0),
       (46,  'Хліб білий тостовий ',    'INGREDIENT', 8.8000,  3.7000,   56.5000, 294.0000,  0, 50001, NULL, 0),
       (62,  'Вершкова курка',          'RECIPE',     52.7300, 122.1100, 14.1100, 1361.0000, 0, 50001, NULL, 0),
       (76,  'Нут',                     'INGREDIENT', 19.3000, 6.0400,   60.6500, 364.0000,  0, 50001, NULL, 0),
       (104, 'Тістечко картопля',       'INGREDIENT', 6.4500,  20.6200,  47.3200, 382.0000,  0, 50001, NULL, 0),
       (105, 'Дорадо',                  'INGREDIENT', 18.0000, 0.9800,   0.0000,  85.0000,   0, 50001, NULL, 0),
       (106, 'Манго',                   'INGREDIENT', 0.5100,  0.2700,   17.0000, 65.0000,   0, 50001, NULL, 0),
       (14,  'Куряче філе',             'INGREDIENT', 23.0900, 1.2400,   0.0000,  110.0000,  0, 50001, NULL, 0),
       (28,  'Філе стегна (без шкіри)', 'INGREDIENT', 25.7200, 10.7900,  0.0000,  207.0000,  0, 50001, NULL, 0),
       (34,  'Цибуля',                  'INGREDIENT', 0.9200,  0.0800,   10.1100, 42.0000,   0, 50001, NULL, 0),
       (51,  'Вершки 10%',              'INGREDIENT', 3.0000,  10.0000,  4.0000,  118.0000,  0, 50001, NULL, 0),
       (68,  'Морква',                  'INGREDIENT', 0.9300,  0.2400,   9.5800,  41.0000,   0, 50001, NULL, 0),
       (82,  'Вершки 30%',              'INGREDIENT', 3.3000,  30.0000,  2.6000,  294.0000,  0, 50001, NULL, 0);

INSERT INTO pfcc.ingredients (recipe_id, ingredient_id, ingredient_weight, ingredient_index)
VALUES (62, 14, 1000.0000, 0),
       (62, 26, 5.0000,    0),
       (62, 28, 500.0000,  0),
       (62, 34, 340.0000,  0),
       (62, 51, 150.0000,  0);

INSERT INTO pfcc.dish (id, name, food_id, recipe_weight, cooked_weight, protein, fat, carbohydrates, calories,
                       cooked_on, deleted, owner_id)
VALUES (51, 'Вершкова курка Mar 25', 62, 3080.0000, 2500.0000, 22.9296, 4.3462, 2.3773, 145.9320, '2024-03-25 22:12:42',
        1, 50001);

INSERT INTO pfcc.dish_ingredients (dish_id, ingredient_id, ingredient_weight, ingredient_index)
VALUES (51, 14, 2000.0000, 0),
       (51, 26, 10.0000,   1),
       (51, 28, 400.0000,  2),
       (51, 34, 420.0000,  3),
       (51, 68, 150.0000,  5),
       (51, 82, 100.0000,  4);

INSERT INTO pfcc.meal (id, weight, protein, fat, carbohydrates, calories, food_id, dish_id, eaten_on, owner_id)
VALUES (125, 450.0000, 103.1832, 19.5579, 10.6979, 656.6940, 62,  51,   '2024-03-25 00:00:00', 50001),
       (124, 300.0000, 54.0000,  2.9400,  0.0000,  255.0000, 105, NULL, '2024-03-25 00:00:00', 50001),
       (123, 50.0000,  0.2550,   0.1350,  8.5000,  32.5000,  106, NULL, '2024-03-25 00:00:00', 50001),
       (122, 100.0000, 8.8000,   3.7000,  56.5000, 294.0000, 46,  NULL, '2024-03-25 00:00:00', 50001),
       (121, 130.0000, 8.3850,   26.8060, 61.5160, 496.6000, 104, NULL, '2024-03-25 00:00:00', 50001),
       (126, 500.0000, 114.6480, 21.7310, 11.8865, 729.6600, 62,  51,   '2024-03-26 00:00:00', 50001),
       (127, 135.0000, 8.1810,   0.7155,  98.1180, 445.5000, 11,  NULL, '2024-03-26 00:00:00', 50001),
       (130, 110.0000, 7.0950,   22.6820, 52.0520, 420.2000, 104, NULL, '2024-03-27 00:00:00', 50001),
       (129, 135.0000, 8.1810,   0.7155,  98.1180, 445.5000, 11,  NULL, '2024-03-27 00:00:00', 50001),
       (128, 500.0000, 114.6480, 21.7310, 11.8865, 729.6600, 62,  51,   '2024-03-27 00:00:00', 50001),
       (131, 500.0000, 114.6480, 21.7310, 11.8865, 729.6600, 62,  51,   '2024-03-28 00:00:00', 50001),
       (132, 135.0000, 8.1810,   0.7155,  98.1180, 445.5000, 11,  NULL, '2024-03-28 00:00:00', 50001),
       (133, 500.0000, 114.6480, 21.7310, 11.8865, 729.6600, 62,  51,   '2024-03-29 00:00:00', 50001),
       (134, 135.0000, 8.1810,   0.7155,  98.1180, 445.5000, 11,  NULL, '2024-03-29 00:00:00', 50001),
       (136, 5.0000,   0.0000,   5.0000,  0.0000,  44.2000,  26,  NULL, '2024-03-30 00:00:00', 50001),
       (135, 100.0000, 19.3000,  6.0400,  60.6500, 364.0000, 76,  NULL, '2024-03-30 00:00:00', 50001);