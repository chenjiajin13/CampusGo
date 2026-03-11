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

