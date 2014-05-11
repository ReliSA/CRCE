ALTER TABLE repository ADD id VARCHAR(48);

UPDATE repository SET id = repository_id;

ALTER TABLE repository ALTER COLUMN id SET NOT NULL;
