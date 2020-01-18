package cz.zcu.kiv.crce.example.indexer.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;

import java.util.List;

/**
 * This class represents namespace. Namespaces helps hierarchically order indexed information about artifacts.
 * This namespace gathers information about imported packages in jar. Every artifact which is uploaded into CRCE
 * will be implicitly indexed by file indexer and these information is saved under crce.identity namespace.
 */
public interface NsImportPackage {
    //Full name of nasmespace
    String NAMESPACE__JAR_IMPORT = "jar.import.package";

    //Attribute which will be saved under this namespace
    AttributeType<List<String>> ATTRIBUTE__IMPORTED_PACKAGES = new ListAttributeType("imported packages");
}
