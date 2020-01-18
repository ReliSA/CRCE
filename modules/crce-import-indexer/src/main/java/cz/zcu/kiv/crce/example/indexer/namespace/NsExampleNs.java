package cz.zcu.kiv.crce.example.indexer.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * This is example namespace which saves count of imports in jar. Namespaces helps hierarchically order indexed
 * information about artifacts. Every artifact which is uploaded into CRCE will be implicitly indexed by file
 * indexer and these information is saved under crce.identity namespace.
 */
public interface NsExampleNs {
    //Full name of namespace
    String NAMESPACE__EXAMPLE_NS = "crce.example";

    //Attribute for count of imports
    AttributeType<String> ATTRIBUTE__IMPORT_COUNT = new SimpleAttributeType<>("import-count", String.class);
}
