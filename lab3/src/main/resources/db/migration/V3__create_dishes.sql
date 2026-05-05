CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    restaurant_id BIGINT REFERENCES restaurants(id)
);