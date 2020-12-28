package cz.zcu.kiv.crce.crce_external_repository.api.impl;

/**
 * A class for setting external, central, and local Maven repository links. It also maintains user preference settings
 * to enable group content listing and editing in the local currency repository..
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class SettingsUrl{
	private String centralMavenUrl = "http://repo.maven.apache.org/maven2/";
	private String localAetherUrl = "aether-local-repo";
	private String externalAetherUrl = "http://repo.maven.apache.org/maven2/";
	private boolean enableDeleteLocalMaven = false;
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
	
	public boolean isEnableDeleteLocalMaven() {
		return enableDeleteLocalMaven;
	}

	public void setEnableDeleteLocalMaven(boolean enableDeleteLocalMaven) {
		this.enableDeleteLocalMaven = enableDeleteLocalMaven;
	}

	public boolean isEnableGroupSearch() {
		return enableGroupSearch;
	}

	public void setEnableGroupSearch(boolean enableGroupSearch) {
		this.enableGroupSearch = enableGroupSearch;
	}
}

