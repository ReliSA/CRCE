package cz.zcu.kiv.crce.crce_external_repository.api.impl;

import java.util.List;

/**
 * The class represents a list of found artifacts (objects of class ArtifactTree) in the external storage.
 * This is the return data type of the CentralMavenApi interface.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class ResultSearchArtifactTree {
	private String status;
	private List<ArtifactTree> artifactTreeList;
	public ResultSearchArtifactTree(List<ArtifactTree> artifactTreeList, String status) {
		super();
		this.status = status;
		this.artifactTreeList = artifactTreeList;
	}
	public String getStatus() {
		return status;
	}
	public List<ArtifactTree> getArtifactTreeList() {
		return artifactTreeList;
	}
}
