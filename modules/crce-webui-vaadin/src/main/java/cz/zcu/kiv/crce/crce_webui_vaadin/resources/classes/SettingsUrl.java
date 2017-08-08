package cz.zcu.kiv.crce.crce_webui_vaadin.resources.classes;

import java.io.File;

public class SettingsUrl{

	private String localMavenUrl = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository";
	private String centralMavenUrl = "http://repo.maven.apache.org/maven2/";
	private String localAetherUrl = "target/local-repo";
	private String externalAetherUrl = "http://repo.maven.apache.org/maven2/";
	
	public String getLocalMavenUrl() {
		return localMavenUrl;
	}

	public String getCentralMavenUrl() {
		return centralMavenUrl;
	}

	public String getLocalAetherUrl() {
		return localAetherUrl;
	}

	public String getExternalAetherUrl() {
		return externalAetherUrl;
	}

	public void setLocalMavenUrl(String url) {
		this.localMavenUrl = url;
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
