

INSERT INTO USERS (NAME, EMAIL, PASSWORD)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('Guest', 'guest@gmail.com', '{noop}guest');

INSERT INTO USER_ROLE (ROLE, USER_ID)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO RESTAURANT (NAME)
VALUES  ('Kalimera'),
        ('Nero'),
        ('Zahar');

INSERT INTO DISH (NAME, PRICE, DISH_DATE, REST_ID)
VALUES  ('Meat', 200.00, '2024-01-01', 1),
        ('Vegetables', 100.00, '2024-01-02', 1),
        ('Chicken', 200.00, '2024-02-01', 2),
        ('Raviolli', 300.00, '2024-02-02', 2);
