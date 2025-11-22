-- Flyway V1 migration: orders schema
CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  reference VARCHAR(64) NOT NULL UNIQUE,
  producer_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  total_amount NUMERIC(19,2) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id BIGINT NOT NULL,
  quantity INTEGER NOT NULL,
  unit_price NUMERIC(19,2) NOT NULL,
  subtotal NUMERIC(19,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_payments (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  payment_intent_id VARCHAR(128),
  status VARCHAR(32),
  amount NUMERIC(19,2) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_orders_producer ON orders(producer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_payments_order ON order_payments(order_id);
