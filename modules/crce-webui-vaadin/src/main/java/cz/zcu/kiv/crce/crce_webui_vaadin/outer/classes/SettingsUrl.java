package cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes;

public class SettingsUrl{
	private String centralMavenUrl = "http://repo.maven.apache.org/maven2/";
	private String localAetherUrl = "aether-local-repo";
	private String externalAetherUrl = "http://repo.maven.apache.org/maven2/";
	
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

}
