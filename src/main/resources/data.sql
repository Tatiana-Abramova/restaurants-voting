

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

/*INSERT INTO DISH (NAME, PRICE, REST_ID)
VALUES  ('Meat', 200.00, 3),
        ('Vegetables', 100.00, 3),
        ('Chicken', 200.00, 4),
        ('Raviolli', 300.00, 4);*/
