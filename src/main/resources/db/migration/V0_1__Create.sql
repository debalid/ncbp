--CREATE SEQUENCE seq_clients_id START WITH 1 INCREMENT BY 1;
--CREATE SEQUENCE seq_orders_id START WITH 1 INCREMENT BY 1;

CREATE TABLE clients (
  --client_id INTEGER NOT NULL DEFAULT NEXTVAL('seq_clients_id'),
  client_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  title     VARCHAR(140),
  phone     VARCHAR(20),
  PRIMARY KEY (client_id)
);

CREATE TABLE orders (
  --order_number BIGINT NOT NULL DEFAULT NEXTVAL('seq_orders_id'),
  order_number BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  priceTotal   INT,
  date         DATE,
  client_id    INTEGER,
  PRIMARY KEY (order_number),
  FOREIGN KEY (client_id) REFERENCES clients
);

