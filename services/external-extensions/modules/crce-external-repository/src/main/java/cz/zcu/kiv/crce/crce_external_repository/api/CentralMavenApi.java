package cz.zcu.kiv.crce.crce_external_repository.api;

import cz.zcu.kiv.crce.crce_external_repository.api.impl.ResultSearchArtifactTree;

/**
 * The interface prescribes the method of obtaining an artifact from a central Maven repository.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public interface CentralMavenApi {
    ResultSearchArtifactTree getArtifactTree(String group, String artifact, String version, Object packaging, Object directIndex,
                                                    String range);
}
