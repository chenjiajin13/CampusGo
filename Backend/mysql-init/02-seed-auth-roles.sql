-- Baseline role tables and demo accounts after a fresh MySQL volume.
-- Demo password for all seeded merchant/runner/admin accounts: 123456

CREATE TABLE IF NOT EXISTS campus_merchant.merchants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(32),
    address VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'PAUSED',
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    rating DOUBLE NOT NULL DEFAULT 4.6,
    finished_orders INT NOT NULL DEFAULT 0,
    tags JSON NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_merchants_username (username)
);

CREATE TABLE IF NOT EXISTS campus_runner.runners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(32),
    vehicle_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OFFLINE',
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    rating DOUBLE NOT NULL DEFAULT 5.0,
    completed_orders INT NOT NULL DEFAULT 0,
    total_earnings_cents BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_runners_username (username)
);

CREATE TABLE IF NOT EXISTS campus_admin.admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(32),
    role VARCHAR(32) NOT NULL DEFAULT 'OPERATOR',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admins_username (username)
);

INSERT INTO campus_merchant.merchants
    (username, password_hash, name, phone, address, status, latitude, longitude, rating, finished_orders, tags)
SELECT 'kfc_owner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'KFC-NUS', '88886666', 'COM2, NUS', 'OPEN',
       1.2965, 103.7761, 4.6, 0, JSON_ARRAY('chicken', 'set meal')
WHERE NOT EXISTS (SELECT 1 FROM campus_merchant.merchants WHERE username = 'kfc_owner');

INSERT INTO campus_merchant.merchants
    (username, password_hash, name, phone, address, status, latitude, longitude, rating, finished_orders, tags)
SELECT 'mcdonald_owner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'McDonalds-NUS', '88887777', 'COM2, NUS', 'OPEN',
       1.2972, 103.7733, 4.7, 0, JSON_ARRAY('burger', 'fast food')
WHERE NOT EXISTS (SELECT 1 FROM campus_merchant.merchants WHERE username = 'mcdonald_owner');

INSERT INTO campus_runner.runners
    (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'alice_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880001', 'BICYCLE', 'AVAILABLE', 1.2968, 103.7801, 4.9, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM campus_runner.runners WHERE username = 'alice_runner');

INSERT INTO campus_runner.runners
    (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'bob_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880002', 'MOTORBIKE', 'AVAILABLE', 1.3000, 103.7810, 4.8, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM campus_runner.runners WHERE username = 'bob_runner');

INSERT INTO campus_runner.runners
    (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'charlie_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880003', 'E_SCOOTER', 'AVAILABLE', 1.3100, 103.7822, 4.7, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM campus_runner.runners WHERE username = 'charlie_runner');

INSERT INTO campus_admin.admins
    (username, password_hash, email, phone, role, enabled)
SELECT 'super_admin',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'super_admin@campusgo.local', '90000000', 'SUPER_ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM campus_admin.admins WHERE username = 'super_admin');

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, '2pc Chicken Meal', 950, 1
FROM campus_merchant.merchants m
WHERE m.username = 'kfc_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = '2pc Chicken Meal'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Zinger Burger', 740, 1
FROM campus_merchant.merchants m
WHERE m.username = 'kfc_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Zinger Burger'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Whipped Potato', 280, 1
FROM campus_merchant.merchants m
WHERE m.username = 'kfc_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Whipped Potato'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Pepsi', 250, 1
FROM campus_merchant.merchants m
WHERE m.username = 'kfc_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Pepsi'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Big Mac Set', 890, 1
FROM campus_merchant.merchants m
WHERE m.username = 'mcdonald_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Big Mac Set'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'McSpicy Burger', 720, 1
FROM campus_merchant.merchants m
WHERE m.username = 'mcdonald_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'McSpicy Burger'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Fries (M)', 320, 1
FROM campus_merchant.merchants m
WHERE m.username = 'mcdonald_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Fries (M)'
  );

INSERT INTO campus_merchant.menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Coke Zero', 250, 1
FROM campus_merchant.merchants m
WHERE m.username = 'mcdonald_owner'
  AND NOT EXISTS (
      SELECT 1 FROM campus_merchant.menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Coke Zero'
  );

