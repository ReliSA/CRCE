package cz.zcu.kiv.crce.crce_external_repository.api;

public class SettingsUrl{
	private String centralMavenUrl = "http://repo.maven.apache.org/maven2/";
	private String localAetherUrl = "aether-local-repo";
	private String externalAetherUrl = "http://repo.maven.apache.org/maven2/";
	private boolean enableGroupSearch = false;
	
	public String getCentralMavenUrl() {
		return centralMavenUrl;
	}

	public String getLocalAetherUrl() {
		return localAetherUrl;
	}

	public String getExternalAetherUrl() {
		return externalAetherUrl;
	}

	public void setCentralMavenUrl(String url) {
		this.centralMavenUrl = url;
	}

	public void setLocalAetherUrl(String url) {
		this.localAetherUrl = url;
	}

	public void setExternalAetherUrl(String url) {
		this.externalAetherUrl = url;
	}

	public boolean isEnableGroupSearch() {
		return enableGroupSearch;
	}

	public void setEnableGroupSearch(boolean enableGroupSearch) {
		this.enableGroupSearch = enableGroupSearch;
	}
}

