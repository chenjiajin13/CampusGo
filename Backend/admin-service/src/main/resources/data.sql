-- Seed demo admins (idempotent)
-- Password for all demo admins: 123456
INSERT INTO admins (username, password_hash, email, phone, role, enabled)
SELECT 'super_admin',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'super_admin@campusgo.local',
       '90000000',
       'SUPER_ADMIN',
       1
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'super_admin');

INSERT INTO admins (username, password_hash, email, phone, role, enabled)
SELECT 'ops_admin',
       '$2a$10$1m.HS.55Rq8pL2iry7qU4OeUyLnemFRMG1KZIHU4MCAubp5i/sdVO',
       'ops_admin@campusgo.local',
       '90000001',
       'OPERATOR',
       1
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'ops_admin');

