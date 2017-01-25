CREATE TABLE IF NOT EXISTS repository (
  repository_id BIGINT NOT NULL,
  uri VARCHAR(512) NOT NULL,
  PRIMARY KEY (repository_id)
);

CREATE TABLE IF NOT EXISTS resource (
  resource_id BIGINT NOT NULL,
  repository_id BIGINT NOT NULL,
  id VARCHAR(48) NOT NULL,
  uri VARCHAR(512) NOT NULL,
  PRIMARY KEY (resource_id),
  FOREIGN KEY (repository_id) REFERENCES repository (repository_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS capability (
  capability_id BIGINT NOT NULL,
  parent_capability_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  level SMALLINT DEFAULT 0 NOT NULL,
  id VARCHAR(48) NOT NULL,
  namespace VARCHAR(64) NOT NULL,
  PRIMARY KEY (capability_id),
  FOREIGN KEY (resource_id) REFERENCES resource (resource_id) ON DELETE CASCADE,
  FOREIGN KEY (parent_capability_id) REFERENCES capability (capability_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requirement (
  requirement_id BIGINT NOT NULL,
  parent_requirement_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  level SMALLINT DEFAULT 0 NOT NULL,
  id VARCHAR(48) NOT NULL,
  namespace VARCHAR(64) NOT NULL,
  PRIMARY KEY (requirement_id),
  FOREIGN KEY (resource_id) REFERENCES resource (resource_id) ON DELETE CASCADE,
  FOREIGN KEY (parent_requirement_id) REFERENCES requirement (requirement_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resource_property (
  property_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  id VARCHAR(48) NOT NULL,
  namespace VARCHAR(64) NOT NULL,
  PRIMARY KEY (property_id),
  FOREIGN KEY (resource_id) REFERENCES resource (resource_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS capability_property (
  property_id BIGINT NOT NULL,
  capability_id BIGINT NOT NULL,
  id VARCHAR(48) NOT NULL,
  namespace VARCHAR(64) NOT NULL,
  PRIMARY KEY (property_id),
  FOREIGN KEY (capability_id) REFERENCES capability (capability_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS capability_attribute (
  capability_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  list_index SMALLINT DEFAULT 0 NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  operator SMALLINT DEFAULT 0 NOT NULL,
  string_value VARCHAR(1024),
  long_value BIGINT,
  double_value DOUBLE,
  version_major_value INTEGER,
  version_minor_value INTEGER,
  version_micro_value INTEGER,
  PRIMARY KEY (capability_id, name, list_index),
  FOREIGN KEY (capability_id) REFERENCES capability (capability_id) ON DELETE CASCADE,
  CONSTRAINT capability_attribute_ck1 CHECK
    string_value IS NOT NULL
    OR long_value IS NOT NULL
    OR double_value IS NOT NULL,
  CONSTRAINT capability_attribute_ck2 CHECK
    (string_value IS NOT NULL
        AND version_major_value IS NOT NULL
        AND version_minor_value IS NOT NULL
        AND version_micro_value IS NOT NULL)
    OR (version_major_value IS NULL
        AND version_minor_value IS NULL
        AND version_micro_value IS NULL)
);

CREATE TABLE IF NOT EXISTS requirement_attribute (
  requirement_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  attribute_index SMALLINT DEFAULT 0 NOT NULL,
  list_index SMALLINT DEFAULT 0 NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  operator SMALLINT DEFAULT 0 NOT NULL,
  string_value VARCHAR(1024),
  long_value BIGINT,
  double_value DOUBLE,
  version_major_value INTEGER,
  version_minor_value INTEGER,
  version_micro_value INTEGER,
  PRIMARY KEY (requirement_id, name, attribute_index, list_index),
  FOREIGN KEY (requirement_id) REFERENCES requirement (requirement_id) ON DELETE CASCADE,
  CONSTRAINT requirement_attribute_ck1 CHECK
    string_value IS NOT NULL
    OR long_value IS NOT NULL
    OR double_value IS NOT NULL,
  CONSTRAINT requirement_attribute_ck2 CHECK
    (string_value IS NOT NULL
        AND version_major_value IS NOT NULL
        AND version_minor_value IS NOT NULL
        AND version_micro_value IS NOT NULL)
    OR (version_major_value IS NULL
        AND version_minor_value IS NULL
        AND version_micro_value IS NULL)
);

CREATE TABLE IF NOT EXISTS resource_property_attribute (
  property_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  list_index SMALLINT DEFAULT 0 NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  operator SMALLINT DEFAULT 0 NOT NULL,
  string_value VARCHAR(1024),
  long_value BIGINT,
  double_value DOUBLE,
  version_major_value INTEGER,
  version_minor_value INTEGER,
  version_micro_value INTEGER,
  PRIMARY KEY (property_id, name, list_index),
  FOREIGN KEY (property_id) REFERENCES resource_property (property_id) ON DELETE CASCADE,
  CONSTRAINT res_property_attribute_ck1 CHECK
    string_value IS NOT NULL
    OR long_value IS NOT NULL
    OR double_value IS NOT NULL,
  CONSTRAINT res_property_attribute_ck2 CHECK
    (string_value IS NOT NULL
        AND version_major_value IS NOT NULL
        AND version_minor_value IS NOT NULL
        AND version_micro_value IS NOT NULL)
    OR (version_major_value IS NULL
        AND version_minor_value IS NULL
        AND version_micro_value IS NULL)
);

CREATE TABLE IF NOT EXISTS capability_property_attribute (
  property_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  list_index SMALLINT DEFAULT 0 NOT NULL,
  type SMALLINT DEFAULT 0 NOT NULL,
  operator SMALLINT DEFAULT 0 NOT NULL,
  string_value VARCHAR(1024),
  long_value BIGINT,
  double_value DOUBLE,
  version_major_value INTEGER,
  version_minor_value INTEGER,
  version_micro_value INTEGER,
  PRIMARY KEY (property_id, name, list_index),
  FOREIGN KEY (property_id) REFERENCES capability_property (property_id) ON DELETE CASCADE,
  CONSTRAINT cap_property_attribute_ck1 CHECK
    string_value IS NOT NULL
    OR long_value IS NOT NULL
    OR double_value IS NOT NULL,
  CONSTRAINT cap_property_attribute_ck2 CHECK
    (string_value IS NOT NULL
        AND version_major_value IS NOT NULL
        AND version_minor_value IS NOT NULL
        AND version_micro_value IS NOT NULL)
    OR (version_major_value IS NULL
        AND version_minor_value IS NULL
        AND version_micro_value IS NULL)
);

CREATE TABLE IF NOT EXISTS capability_directive (
  capability_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  value VARCHAR(1024) NOT NULL,
  PRIMARY KEY (capability_id, name),
  FOREIGN KEY (capability_id) REFERENCES capability (capability_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS requirement_directive (
  requirement_id BIGINT NOT NULL,
  name VARCHAR(16) NOT NULL,
  value VARCHAR(1024) NOT NULL,
  PRIMARY KEY (requirement_id, name),
  FOREIGN KEY (requirement_id) REFERENCES requirement (requirement_id) ON DELETE CASCADE,
);

CREATE SEQUENCE IF NOT EXISTS repository_seq;
CREATE SEQUENCE IF NOT EXISTS resource_seq;
CREATE SEQUENCE IF NOT EXISTS capability_seq;
CREATE SEQUENCE IF NOT EXISTS requirement_seq;
CREATE SEQUENCE IF NOT EXISTS resource_property_seq;
CREATE SEQUENCE IF NOT EXISTS capability_property_seq;
