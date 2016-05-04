RENAME COLUMN ncbp.orders.priceTotal TO price_total;

DELETE FROM ncbp.orders WHERE price_total < 0;
DELETE FROM ncbp.orders WHERE order_number <= 0;

ALTER TABLE ncbp.orders ADD CHECK (price_total >= 0);
ALTER TABLE ncbp.orders ADD CHECK (order_number > 0);