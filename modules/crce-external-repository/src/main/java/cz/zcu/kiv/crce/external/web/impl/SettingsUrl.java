package cz.zcu.kiv.crce.external.web.impl;

import java.io.File;

import cz.zcu.kiv.crce.external.web.inf.InfSettingsUrl;

public class SettingsUrl implements InfSettingsUrl {

	private String localMavenUrl = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository";
	private String centralMavenUrl = "http://repo.maven.apache.org/maven2/";
	private String localAetherUrl = "target/local-repo";
	private String externalAetherUrl = "http://repo.maven.apache.org/maven2/";
	
	@Override
	public String getLocalMavenUrl() {
		return localMavenUrl;
	}

	@Override
	public String getCentralMavenUrl() {
		return centralMavenUrl;
	}

	@Override
	public String getLocalAetherUrl() {
		return localAetherUrl;
	}

	@Override
	public String getExternalAetherUrl() {
		return externalAetherUrl;
	}

	@Override
	public void setLocalMavenUrl(String url) {
		this.localMavenUrl = url;
	}

	@Override
	public void setCentralMavenUrl(String url) {
		this.centralMavenUrl = url;
	}

	@Override
	public void setLocalAetherUrl(String url) {
		this.localAetherUrl = url;
	}

	@Override
	public void setExternalAetherUrl(String url) {
		this.externalAetherUrl = url;
	}

}
