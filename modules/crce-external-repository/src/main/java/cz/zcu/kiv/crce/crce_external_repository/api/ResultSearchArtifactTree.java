package cz.zcu.kiv.crce.crce_external_repository.api;

import java.util.List;

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
