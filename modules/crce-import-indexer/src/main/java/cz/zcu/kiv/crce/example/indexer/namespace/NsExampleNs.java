package cz.zcu.kiv.crce.example.indexer.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

public interface NsExampleNs {
    String NAMESPACE__EXAMPLE_NS = "crce.example";

    AttributeType<String> ATTRIBUTE__IMPORT_COUNT = new SimpleAttributeType<>("import-count", String.class);
}
