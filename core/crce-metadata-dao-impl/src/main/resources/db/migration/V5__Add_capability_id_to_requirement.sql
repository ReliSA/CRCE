ALTER TABLE requirement ADD capability_id BIGINT;

ALTER TABLE requirement ADD FOREIGN KEY(capability_id) REFERENCES capability(capability_id) ON DELETE CASCADE;
