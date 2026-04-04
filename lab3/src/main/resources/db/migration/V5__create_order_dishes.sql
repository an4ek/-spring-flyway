CREATE TABLE order_dishes (
    order_id BIGINT NOT NULL REFERENCES orders(id),
    dish_id BIGINT NOT NULL REFERENCES dishes(id),
    PRIMARY KEY (order_id, dish_id)
);