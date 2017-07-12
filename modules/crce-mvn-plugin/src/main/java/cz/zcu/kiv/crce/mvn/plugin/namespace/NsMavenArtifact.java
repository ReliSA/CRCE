package cz.zcu.kiv.crce.mvn.plugin.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * A namespace for resource's capabilities.
 *
 * @author Zdenek Vales
 */
public interface NsMavenArtifact {

    String NAMESPACE__MAVEN_ARTIFACT = "mvn.artifact";

    String CATEGORY__MAVEN_ARTIFACT = "mvn";
    String CATEGORY__MAVEN_CORRUPTED = "corrupted";

    AttributeType<String> ATTRIBUTE__MODEL_VERSION = new SimpleAttributeType<>("modelVersion",String.class);
}
