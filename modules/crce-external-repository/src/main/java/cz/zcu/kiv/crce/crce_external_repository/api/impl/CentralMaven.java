package cz.zcu.kiv.crce.crce_external_repository.api.impl;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.crce.crce_external_repository.api.CentralMavenApi;
import org.apache.maven.index.ArtifactInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CentralMaven implements CentralMavenApi {
	private String centralMavenUrl;
	private boolean enableGroupSearch;
	private List<ArtifactTree> artifactTreeList = new ArrayList<ArtifactTree>();
	private SettingsUrl settings;

	public CentralMaven(SettingsUrl settings) {
		this.settings = settings;
	}

	@Override
	public ResultSearchArtifactTree getArtifactTree(String group, String artifact, String version, Object packaging, Object directIndex,
													String range) {
		if (settings == null) {
			SettingsUrl settings = new SettingsUrl();
			centralMavenUrl = settings.getCentralMavenUrl();
			enableGroupSearch = settings.isEnableGroupSearch();
		} else {
			centralMavenUrl = settings.getCentralMavenUrl();
			enableGroupSearch = settings.isEnableGroupSearch();
		}
		// reset tree
		artifactTreeList.clear();
		// direct search
		if (directIndex.equals("Direct")) {
			if (!artifact.isEmpty() || !version.isEmpty()) {
				directSearch(group, artifact, version, packaging.toString());
			} else if (enableGroupSearch && !group.isEmpty()) {
				onlyGroupSearch(group);
				if(artifactTreeList.size() == 0) {
					return new ResultSearchArtifactTree(null, "not found");
				}
				else {
					return new ResultSearchArtifactTree(artifactTreeList, "group");
				}
			}
			if (artifactTreeList.size() == 0) {
				return new ResultSearchArtifactTree(null, "artifact not found");
			} else {
				return new ResultSearchArtifactTree(artifactTreeList, "direct");
			}
		}
		// index search
		else {
			indexSearch(group, artifact, version, packaging.toString(), range);
			if (artifactTreeList.size() == 0) {
				return new ResultSearchArtifactTree(null, "notfound");
			} else {
				return new ResultSearchArtifactTree(artifactTreeList, "found");
			}
		}
	}

	private void directSearch(String group, String artifact, String version, String packaging) {
		URL website;
		try {
			website = new URL(centralMavenUrl + group.replace('.', '/') + "/" + artifact + "/" + version + "/"
					+ artifact + "-" + version + "." + packaging);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			if (rbc.isOpen()) {
				List<String> versions = new ArrayList<String>();
				versions.add(version);
				ArtifactTree artifactTree = new ArtifactTree(group, artifact, versions, packaging);
				artifactTree.setUrl(centralMavenUrl);
				artifactTreeList.add(artifactTree);
				rbc.close();
			}
		} catch (IOException e) {
			artifactTreeList.clear();
		}
	}

	private void onlyGroupSearch(String group) {
		try {
			list(new URL(centralMavenUrl + group.replace('.', '/') + "/"));
		} catch (IOException e) {
			artifactTreeList.clear();
		}
	}
	
	private void list(URL path) throws IOException {
		Document doc = Jsoup.connect(path.toString()).get();
		for (Element file : doc.select("a").not("[href=../]")) {
			if (file.text().endsWith("/")) {
				list(new URL(path.toString() + file.text()));
			} else {
				ArtifactTree artifactTree = new ArtifactTree(path.getPath(), file.text(), null, file.text()
						.substring(file.text().lastIndexOf('.') +  1));
				if(path.getPort() == -1) {
					artifactTree.setUrl(path.getProtocol() + "://" + path.getHost() + path.getPath() + file.text());
				}
				else {
					artifactTree.setUrl(path.getProtocol() + "://" + path.getHost() + ":" + path.getPort() + path.getPath() + file.text());
				}
				artifactTreeList.add(artifactTree);
			}
		}
	}

	private void indexSearch(String group, String artifact, String version, String packaging, String range){
		MavenIndex mavenIndex = null;
		try {
			mavenIndex = new MavenIndex();
			List<ArtifactInfo> artifactInfoList = mavenIndex.searchArtefact(settings, group, artifact, version,
					packaging, range);
			for (ArtifactInfo ai : artifactInfoList) {
				// list už obsahuje artefakt ? ano přidáme verzi
				boolean find = false;
				for(ArtifactTree artefactTree : artifactTreeList) {
					if(artefactTree .getGroupId().equals(ai.groupId) && artefactTree.getArtefactId().equals(ai.artifactId) && 
							artefactTree.getPackaging().equals(ai.packaging)) {
						find = true;
						List<String> versions = artefactTree .getVersions();
						versions.add(ai.version);
						ArtifactTree newArtefactTree = new ArtifactTree(ai.groupId, ai.artifactId, versions, ai.packaging);
						newArtefactTree.setUrl(artefactTree.getUrl());
						artifactTreeList.remove(artefactTree);
						artifactTreeList.add(newArtefactTree);
						break;
					}
				}
				if(!find) {
					List<String> versions = new ArrayList<String>();
					versions.add(ai.version);
					ArtifactTree artifactTree = new ArtifactTree(ai.groupId, ai.artifactId, versions, ai.packaging);
					artifactTree.setUrl(centralMavenUrl);
					artifactTreeList.add(artifactTree);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
