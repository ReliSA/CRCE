package cz.zcu.kiv.crce.crce_external_repository.api;

import cz.zcu.kiv.crce.crce_external_repository.api.impl.ArtifactTree;

public interface DefinedMavenApi {
    /**
     * The interface specifies the form of the storage artefact retrieval method with Aether support.
     * <p/>
     * Date: 02.05.19
     *
     * @author Roman Pesek
     */
    ArtifactTree getArtifact(String group, String idText, String version, Object packaging);
}
