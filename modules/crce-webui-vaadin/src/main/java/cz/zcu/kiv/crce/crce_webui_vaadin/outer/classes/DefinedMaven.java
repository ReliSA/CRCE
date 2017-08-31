package cz.zcu.kiv.crce.crce_webui_vaadin.outer.classes;

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
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
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

public class DefinedMaven {
	private Tree definedMavenTree = new Tree("Result Search");
	private String groupText;
	private String idText;
	private String versionText;
	private String packagingText;
	private SettingsUrl settings;

	public DefinedMaven(SettingsUrl settings) {
		this.settings = settings;
	}

	public Tree getTree(String group, String idText, String version, Object packaging) {
		// validator empty entries
		if (group.equals("") || idText.equals("")) {
			return null;
		} else {
			// reset tree
			definedMavenTree.removeAllItems();
			this.groupText = group;
			this.idText = idText;
			this.versionText = version;
			this.packagingText = packaging.toString();
			callAetherLib();
			return definedMavenTree;
		}
	}

	private void callAetherLib() {
		RemoteRepository central = new RemoteRepository.Builder("central", "default", settings.getExternalAetherUrl())
				.build();
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession(repoSystem, settings.getLocalAetherUrl());

		// Artifact artifact = new DefaultArtifact("groupId:artifactId:(0,]");
		Artifact artifact;
		if (!versionText.equals("")) {
			artifact = new DefaultArtifact(groupText + ":" + idText + ":" + "[" + versionText + "]");
		} else {
			artifact = new DefaultArtifact(groupText + ":" + idText + ":" + "(0,]");
		}
		VersionRangeRequest request = new VersionRangeRequest(artifact, Arrays.asList(central), null);
		try {
			VersionRangeResult versionResult = repoSystem.resolveVersionRange(session, request);
			if (versionResult.getVersions().isEmpty()) {
				definedMavenTree = null;
			} else {
				String[] pom = artifact.getGroupId().split("\\.");
				definedMavenTree.addItem(pom[0]);
				for (int i = 1; i < pom.length; i++) {
					definedMavenTree.addItem(pom[i]);
					definedMavenTree.setParent(pom[i], pom[i - 1]);
				}

				if (versionResult.getVersions().size() > 1) {
					for (Version v : versionResult.getVersions()) {
						addArtefactToTree(artifact, v, pom[pom.length - 1]);
					}
				} else {
					addArtefactToTree(artifact, versionResult.getHighestVersion(), pom[pom.length - 1]);
				}
			}

		} catch (VersionRangeResolutionException e) {
			e.printStackTrace();
		}
	}

	private void addArtefactToTree(Artifact artifact, Version version, String parent) {
		definedMavenTree.addItem(artifact.getArtifactId());
		definedMavenTree.setParent(artifact.getArtifactId(), parent);
		definedMavenTree.addItem(version.toString());
		definedMavenTree.setParent(version.toString(), artifact.getArtifactId());

		// konečný artefact je komplet url link např. pro wget - UPRAVIT DLE
		// POTŘEBY
		/*
		 * String artifactText = settings.getExternalAetherUrl() + "/" +
		 * groupText + "/" + idText + "/" + version + "." + packagingText;
		 */
		String artifactText = groupText + ":" + idText + ":" + version + ":" + packagingText;

		definedMavenTree.addItem(artifactText);
		definedMavenTree.setParent(artifactText, version.toString());
		definedMavenTree.setItemCaption(artifactText,
				artifact.getArtifactId() + "-" + version.toString() + "." + packagingText);
		definedMavenTree.setChildrenAllowed(artifactText, false);
		if (packagingText.equals("jar") || packagingText.equals("war")) {
			definedMavenTree.setItemIcon(artifactText, FontAwesome.GIFT);
		} else if (packagingText.equals("xml") || packagingText.equals("pom")) {
			definedMavenTree.setItemIcon(artifactText, FontAwesome.CODE);
		} else {
			definedMavenTree.setItemIcon(artifactText, FontAwesome.FILE);
		}
	}

	public static boolean resolveArtefact(String artifactText, SettingsUrl settings) {
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession(repoSystem, settings.getLocalAetherUrl());

		ArtifactRequest artifactRequest = new ArtifactRequest();
		Artifact artifact = new DefaultArtifact(artifactText.split(":")[0] + ":" + artifactText.split(":")[1] + ":" +
				artifactText.split(":")[3] + ":" + artifactText.split(":")[2]);

		artifactRequest.setArtifact(artifact);

		artifactRequest.addRepository(newCentralRepository(settings.getExternalAetherUrl()));
		ArtifactResult artifactResult;
		try {
			artifactResult = repoSystem.resolveArtifact(session, artifactRequest);
			artifactResult.getArtifact();
		} catch (ArtifactResolutionException e) {
			return false;
		}
		return true;
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

	public static RemoteRepository newCentralRepository(String centralAetherUrl) {
		return new RemoteRepository.Builder("central", "default", centralAetherUrl).build();
	}
}
