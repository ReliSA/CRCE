package cz.zcu.kiv.crce.crce_external_repository.api.impl;

import java.util.List;

/**
 * The class represents the artifact in the external Maven repository. The goupId, artefactId, package, list,
 * available versions, and location url parameters uniquely identify the object. The return type of interface
 * methods DefinedMavenApi
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class ArtifactTree {
	private String groupId;
	private String artefactId;
	private List<String> versions;
	private String packaging;
	private String url;
	
	public ArtifactTree(String groupId, String artefactId, List<String> versions, String packaging) {
		super();
		this.groupId = groupId;
		this.artefactId = artefactId;
		this.versions = versions;
		this.packaging = packaging;
	}
	public String getGroupId() {
		return groupId;
	}
	public String getArtefactId() {
		return artefactId;
	}
	public List<String> getVersions() {
		return versions;
	}
	public String getPackaging() {
		return packaging;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCaption(String version) {
		for(String v : this.versions) {
			if(v.equals(version)){
				return groupId + ":" + artefactId + ":" + version + ":" + packaging;
			}
		}
		return null;
	}
}
