-- Seed demo merchants (idempotent)
-- Password for both demo merchants: 123456
INSERT INTO merchants (username, password_hash, name, phone, address, status, latitude, longitude, rating, finished_orders, tags)
SELECT 'kfc_owner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'KFC-NUS',
       '88886666',
       'COM2, NUS',
       'OPEN',
       1.2965,
       103.7761,
       4.6,
       0,
       JSON_ARRAY('chicken', 'set meal')
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE username = 'kfc_owner');

INSERT INTO merchants (username, password_hash, name, phone, address, status, latitude, longitude, rating, finished_orders, tags)
SELECT 'mcdonald_owner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'McDonalds-NUS',
       '88887777',
       'COM2, NUS',
       'OPEN',
       1.2972,
       103.7733,
       4.7,
       0,
       JSON_ARRAY('burger', 'fast food')
WHERE NOT EXISTS (SELECT 1 FROM merchants WHERE username = 'mcdonald_owner');

-- Seed menu items for demo merchants (idempotent)

-- McDonalds-NUS
INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Big Mac Set', 890, 1
FROM merchants m
WHERE m.name = 'McDonalds-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Big Mac Set'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'McSpicy Burger', 720, 1
FROM merchants m
WHERE m.name = 'McDonalds-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'McSpicy Burger'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Fries (M)', 320, 1
FROM merchants m
WHERE m.name = 'McDonalds-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Fries (M)'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Coke Zero', 250, 1
FROM merchants m
WHERE m.name = 'McDonalds-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Coke Zero'
  );

-- KFC-NUS
INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, '2pc Chicken Meal', 950, 1
FROM merchants m
WHERE m.name = 'KFC-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = '2pc Chicken Meal'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Zinger Burger', 740, 1
FROM merchants m
WHERE m.name = 'KFC-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Zinger Burger'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Whipped Potato', 280, 1
FROM merchants m
WHERE m.name = 'KFC-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Whipped Potato'
  );

INSERT INTO menu_items (merchant_id, name, price_cents, enabled)
SELECT m.id, 'Pepsi', 250, 1
FROM merchants m
WHERE m.name = 'KFC-NUS'
  AND NOT EXISTS (
      SELECT 1 FROM menu_items mi
      WHERE mi.merchant_id = m.id AND mi.name = 'Pepsi'
  );
