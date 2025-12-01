CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  reference VARCHAR(255) NOT NULL UNIQUE,
  producer_keycloak_id VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  total_amount NUMERIC(19,2) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  version BIGINT DEFAULT 0,
  customer_id BIGINT,
  delivery_mode VARCHAR(255),
  producer_internal_id BIGINT
);

INSERT INTO orders (
  id, reference, producer_keycloak_id, status, total_amount, created_at, updated_at, version, customer_id, delivery_mode, producer_internal_id
) VALUES
  (1, 'ORD-1001', '195b8e84-514a-4912-a7f2-443c0fb131a8', 'pending', 150.00, '2025-11-29 17:36:46.527851', '2025-11-29 17:36:46.527851', 0, 7, 'pickup', 6),
  (2, 'ORD-1002', '195b8e84-514a-4912-a7f2-443c0fb131a8', 'delivered', 200.00, '2025-11-29 17:36:46.527851', '2025-11-29 17:36:46.527851', 0, 7, 'pickup', 6);


CREATE TABLE IF NOT EXISTS order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id BIGINT NOT NULL,
  quantity INTEGER NOT NULL,
  unit_price NUMERIC(38,2) NOT NULL,
  subtotal NUMERIC(38,2) NOT NULL
);

INSERT INTO order_items (
  id, order_id, product_id, quantity, unit_price, subtotal
) VALUES
  (1, 1, 101, 2, 50.00, 100.00),
  (2, 1, 102, 1, 50.00, 50.00),
  (3, 2, 103, 4, 50.00, 200.00);

ALTER TABLE orders
ADD COLUMN consumer_keycloak_id VARCHAR(255);

UPDATE orders
SET consumer_keycloak_id = '3386dc17-3f9e-4cc5-a7aa-d64b72a9f3d1';