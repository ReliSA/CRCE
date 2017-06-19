package cz.zcu.kiv.crce.external.web.inf;

public interface InfSettingsUrl {
	String getLocalMavenUrl();
	String getCentralMavenUrl();
	String getLocalAetherUrl();
	String getExternalAetherUrl();
	
	void setLocalMavenUrl(String url);
	void setCentralMavenUrl(String url);
	void setLocalAetherUrl(String url);
	void setExternalAetherUrl(String url);
}
