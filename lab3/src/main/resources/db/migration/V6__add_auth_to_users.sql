ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password VARCHAR(255),
    ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'USER';

UPDATE users SET password = '$2a$10$dummyhashfortestingpurposesonly123456' WHERE password IS NULL;

ALTER TABLE users ALTER COLUMN password SET NOT NULL;
