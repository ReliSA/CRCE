package cz.zcu.kiv.crce.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * A namespace for resource's capabilities.
 *
 * @author Zdenek Vales
 */
public interface NsMavenArtifact {

    String NAMESPACE__MAVEN_ARTIFACT = "mvn.artifact";

    AttributeType<String> ATTRIBUTE__MODEL_VERSION = new SimpleAttributeType<>("modelVersion",String.class);
}
