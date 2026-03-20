-- Seed demo runners (idempotent)
-- Password for all demo runners: 123456
INSERT INTO runners (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'alice_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880001',
       'BICYCLE',
       'AVAILABLE',
       1.2968,
       103.7801,
       4.9,
       0,
       0
WHERE NOT EXISTS (SELECT 1 FROM runners WHERE username = 'alice_runner');

INSERT INTO runners (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'bob_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880002',
       'MOTORBIKE',
       'AVAILABLE',
       1.3000,
       103.7810,
       4.8,
       0,
       0
WHERE NOT EXISTS (SELECT 1 FROM runners WHERE username = 'bob_runner');

INSERT INTO runners (username, password_hash, phone, vehicle_type, status, latitude, longitude, rating, completed_orders, total_earnings_cents)
SELECT 'charlie_runner',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       '88880003',
       'E_SCOOTER',
       'AVAILABLE',
       1.3100,
       103.7822,
       4.7,
       0,
       0
WHERE NOT EXISTS (SELECT 1 FROM runners WHERE username = 'charlie_runner');

