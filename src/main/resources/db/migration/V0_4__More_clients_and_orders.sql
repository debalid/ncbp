INSERT INTO clients (title, phone) VALUES ('Fomichev Nikita Andreevich', '+7 (968) 546-29-11');
INSERT INTO clients (title, phone) VALUES ('Ivanov Ivan Ivanovich', '+7 (900) 555-44-11');
INSERT INTO clients (title, phone) VALUES ('Tmatarakanov Tarakan Tarakanovich', '+7 (934) 626-12-42');
INSERT INTO clients (title, phone) VALUES ('Aleksandrov Aleksandr Aleksandrovich', '+7 (922) 111-22-11');

INSERT INTO orders (priceTotal, date, client_id) VALUES (100, CURRENT_DATE, 6);
INSERT INTO orders (priceTotal, date, client_id) VALUES (500, CURRENT_DATE, 6);
INSERT INTO orders (priceTotal, date, client_id) VALUES (900, CURRENT_DATE, 7);
INSERT INTO orders (priceTotal, date, client_id) VALUES (111, CURRENT_DATE, 8);
INSERT INTO orders (priceTotal, date, client_id) VALUES (222, CURRENT_DATE, 8);