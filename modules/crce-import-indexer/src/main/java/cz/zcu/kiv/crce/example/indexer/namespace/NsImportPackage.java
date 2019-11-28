package cz.zcu.kiv.crce.example.indexer.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;

import java.util.List;

public interface NsImportPackage {
    String NAMESPACE__JAR_IMPORT = "jar.import.package";

    AttributeType<List<String>> ATTRIBUTE__IMPORTED_PACKAGES = new ListAttributeType("imported packages");
}
