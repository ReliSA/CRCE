package cz.zcu.kiv.crce.test.plugin.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * A namespace for resource's capabilities.
 *
 * @author Zdenek Vales
 */
public interface NsMvnArtifactIdentity {

    String NAMESPACE__MVN_ARTIFACT_IDENTITY = "osgi.identity";

    AttributeType<String> ATTRIBUTE__GROUP_ID = new SimpleAttributeType<>("groupId", String.class);
    AttributeType<String> ATTRIBUTE__ARTIFACT_ID = new SimpleAttributeType<>("artifactId", String.class);
    AttributeType<String> ATTRIBUTE__VERSION = new SimpleAttributeType<>("version", String.class);
}
