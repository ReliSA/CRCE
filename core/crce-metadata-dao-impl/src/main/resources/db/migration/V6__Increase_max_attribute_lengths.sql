ALTER TABLE capability_attribute ALTER COLUMN name VARCHAR(64) NOT NULL;

ALTER TABLE capability_attribute ALTER COLUMN string_value VARCHAR(2048);

ALTER TABLE capability_directive ALTER COLUMN name VARCHAR(64) NOT NULL;

ALTER TABLE requirement_directive ALTER COLUMN name VARCHAR(64) NOT NULL;
