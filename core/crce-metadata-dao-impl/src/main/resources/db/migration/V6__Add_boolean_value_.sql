ALTER TABLE capability_attribute ADD boolean_value BOOLEAN;
ALTER TABLE capability_attribute DROP CONSTRAINT IF EXISTS capability_attribute_ck1;
ALTER TABLE capability_attribute ADD CONSTRAINT capability_attribute_ck1 CHECK
  string_value IS NOT NULL
  OR long_value IS NOT NULL
  OR double_value IS NOT NULL
  OR boolean_value IS NOT NULL;

ALTER TABLE requirement_attribute ADD boolean_value BOOLEAN;
ALTER TABLE requirement_attribute DROP CONSTRAINT IF EXISTS requirement_attribute_ck1;
ALTER TABLE requirement_attribute ADD CONSTRAINT requirement_attribute_ck1 CHECK
  string_value IS NOT NULL
  OR long_value IS NOT NULL
  OR double_value IS NOT NULL
  OR boolean_value IS NOT NULL;

ALTER TABLE resource_property_attribute ADD boolean_value BOOLEAN;
ALTER TABLE resource_property_attribute DROP CONSTRAINT IF EXISTS res_property_attribute_ck1;
ALTER TABLE resource_property_attribute ADD CONSTRAINT res_property_attribute_ck1 CHECK
  string_value IS NOT NULL
  OR long_value IS NOT NULL
  OR double_value IS NOT NULL
  OR boolean_value IS NOT NULL;

ALTER TABLE capability_property_attribute ADD boolean_value BOOLEAN;
ALTER TABLE capability_property_attribute DROP CONSTRAINT IF EXISTS cap_property_attribute_ck1;
ALTER TABLE capability_property_attribute ADD CONSTRAINT cap_property_attribute_ck1 CHECK
  string_value IS NOT NULL
  OR long_value IS NOT NULL
  OR double_value IS NOT NULL
  OR boolean_value IS NOT NULL;

