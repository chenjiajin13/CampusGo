-- Fill address for existing users when missing
UPDATE users
SET address = 'COM2, NUS'
WHERE username = 'Alice' AND (address IS NULL OR address = '');

UPDATE users
SET address = 'UTown, NUS'
WHERE username = 'Bob' AND (address IS NULL OR address = '');

-- Seed demo users (idempotent)
INSERT INTO users (username, password_hash, email, phone, address, enabled)
SELECT 'Alice',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA7A6C1Y9qsJh0kGugHgd6N53BJ38q',
       'alice@campusgo.local',
       '91234567',
       'COM2, NUS',
       1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Alice');

INSERT INTO users (username, password_hash, email, phone, address, enabled)
SELECT 'Bob',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA7A6C1Y9qsJh0kGugHgd6N53BJ38q',
       'bob@campusgo.local',
       '92345678',
       'UTown, NUS',
       1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Bob');

INSERT INTO users (username, password_hash, email, phone, address, enabled)
SELECT 'Charlie',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA7A6C1Y9qsJh0kGugHgd6N53BJ38q',
       'charlie@campusgo.local',
       '93456789',
       'PGP, NUS',
       1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'Charlie');

