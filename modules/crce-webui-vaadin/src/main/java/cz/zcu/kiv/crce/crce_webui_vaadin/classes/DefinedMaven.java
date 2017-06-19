package cz.zcu.kiv.crce.crce_webui_vaadin.classes;

import java.util.Arrays;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Tree;

import cz.zcu.kiv.crce.external.web.impl.SettingsUrl;

public class DefinedMaven{
	private Tree definedMavenTree = new Tree("Result Search");
	private String groupText;
	private String artifactText;
	private String versionText;
	private String packagingText;
	private SettingsUrl settings;
	
	public DefinedMaven(SettingsUrl settings){
		this.settings = settings;
	}

	public Tree getTree(String group, String artifact, String version, Object packaging) {
		// validator empty entries
		if(group.equals("") || artifact.equals("")){
			return null;
		}
		else{
			// reset tree
			definedMavenTree.removeAllItems();
			this.groupText = group;
			this.artifactText = artifact;
			this.versionText = version;
			this.packagingText = packaging.toString();
			callAetherLib();
			return definedMavenTree;
		}
	}

	private void callAetherLib() {
		RemoteRepository central = new RemoteRepository.Builder("nexus", "default", settings.getExternalAetherUrl()).build();
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession(repoSystem, settings.getLocalAetherUrl());

		// Artifact artifact = new DefaultArtifact("groupId:artifactId:(0,]");
		Artifact artifact;
		if(!versionText.equals("")){
			artifact = new DefaultArtifact(groupText + ":" + artifactText + ":" + "[" + versionText + "]");
		}
		else{
			artifact = new DefaultArtifact(groupText + ":" + artifactText + ":" + "(0,]");
		}
		VersionRangeRequest request = new VersionRangeRequest(artifact, Arrays.asList(central), null);
		try {
			VersionRangeResult versionResult = repoSystem.resolveVersionRange(session, request);
			if(versionResult.getHighestVersion() == null) {
				definedMavenTree = null;
			}
			else {
				String[] pom = artifact.getGroupId().split("\\.");
				definedMavenTree.addItem(pom[0]);
				for(int i=1; i< pom.length; i++){
					definedMavenTree.addItem(pom[i]);
					definedMavenTree.setParent(pom[i], pom[i-1]);
				}
				
				if(versionResult.getVersions().size() > 1){
					for(Version v : versionResult.getVersions()){
						addArtefactToTree(artifact, v, pom[pom.length-1]);
					}
				}
				else{
					addArtefactToTree(artifact, versionResult.getHighestVersion(), pom[pom.length-1]);
				}
			}
			
		} catch (VersionRangeResolutionException e) {
			e.printStackTrace();
		}
	}
	
	private void addArtefactToTree(Artifact artifact, Version version, String parent){
		definedMavenTree.addItem(artifact.getArtifactId());
		definedMavenTree.setParent(artifact.getArtifactId(), parent);
		definedMavenTree.addItem(version.toString());
		definedMavenTree.setParent(version.toString(),artifact.getArtifactId());
		
		//end artefact
		//konečný artefact je komplet url link např. pro wget - UPRAVIT DLE POTŘEBY
		String urlArtefact = settings.getExternalAetherUrl() + "/" + groupText + "/" + artifactText +
				"/" + version + "." + packagingText;
		
		definedMavenTree.addItem(urlArtefact);
		definedMavenTree.setParent(urlArtefact, version.toString());
		definedMavenTree.setItemCaption(urlArtefact, artifact.getArtifactId() + 
				"-" + version.toString() + "." + packagingText);
		definedMavenTree.setChildrenAllowed(urlArtefact, false);
		if(packagingText.equals("jar") || packagingText.equals("war")){
    		definedMavenTree.setItemIcon(urlArtefact, FontAwesome.GIFT);
    	}
    	else if(packagingText.equals("xml") || packagingText.equals("pom")){
    		definedMavenTree.setItemIcon(urlArtefact, FontAwesome.CODE);
    	}
    	else{
    		definedMavenTree.setItemIcon(urlArtefact, FontAwesome.FILE);
    	}
	}

	private static RepositorySystem newRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		return locator.getService(RepositorySystem.class);
	}

	private static RepositorySystemSession newSession(RepositorySystem system, String localAetherUrl) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository(localAetherUrl);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
		return session;
	}
}
