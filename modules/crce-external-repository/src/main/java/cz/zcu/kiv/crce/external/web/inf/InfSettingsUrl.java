package cz.zcu.kiv.crce.external.web.inf;

public interface InfSettingsUrl {
	public String getLocalMavenUrl();
	public String getCentralMavenUrl();
	public String getLocalAetherUrl();
	public String getExternalAetherUrl();
	
	public void setLocalMavenUrl(String url);
	public void setCentralMavenUrl(String url);
	public void setLocalAetherUrl(String url);
	public void setExternalAetherUrl(String url);
}
