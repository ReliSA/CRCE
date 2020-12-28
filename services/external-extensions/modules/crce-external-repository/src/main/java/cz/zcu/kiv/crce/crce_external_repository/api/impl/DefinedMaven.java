package cz.zcu.kiv.crce.crce_external_repository.api.impl;

import cz.zcu.kiv.crce.crce_external_repository.api.DefinedMavenApi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefinedMaven implements DefinedMavenApi {
	private ArtifactTree definedArtefact;
	private String groupText;
	private String idText;
	private String versionText;
	private String packagingText;

	private SettingsUrl settings;

	public DefinedMaven(SettingsUrl settings) {
		this.settings = settings;
	}

	@Override
	public ArtifactTree getArtifact(String group, String idText, String version, Object packaging) {
		// validator empty entries
		if (group.equals("") || idText.equals("")) {
			return null;
		} else {
			this.groupText = group;
			this.idText = idText;
			this.versionText = version;
			this.packagingText = packaging.toString();
			callAetherLib();
			/*
			 * System.out.println(new File(settings.getLocalAetherUrl()).mkdirs());
			 * List<String> pomoc = new ArrayList<String>(); pomoc.add("0.0.0");
			 * 
			 * definedArtefact = new ArtifactTree("cz.pokus", "pokusId", pomoc, "jar");
			 */
			return definedArtefact;
		}
	}

	private void callAetherLib() {
		RemoteRepository central = new RemoteRepository.Builder("central", "default", settings.getExternalAetherUrl())
				.build();
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession(repoSystem, settings.getLocalAetherUrl());
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
				definedArtefact = null;
			} else {
				List<String> versionTextList = new ArrayList<String>();
				if (versionResult.getVersions().size() > 1) {
					for (Version v : versionResult.getVersions()) {
						versionTextList.add(v.toString());
					}
				} else {
					versionTextList.add(versionResult.getHighestVersion().toString());
				}
				definedArtefact = new ArtifactTree(artifact.getGroupId(), artifact.getArtifactId(), versionTextList,
						packagingText);
			}

		} catch (VersionRangeResolutionException e) {
			e.printStackTrace();
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	public static boolean resolveArtifact(String artifactText, SettingsUrl settings) {
		RepositorySystem repoSystem = newRepositorySystem();
		RepositorySystemSession session = newSession(repoSystem, settings.getLocalAetherUrl());

		ArtifactRequest artifactRequest = new ArtifactRequest();
		Artifact artifact = new DefaultArtifact(artifactText.split(":")[0] + ":" + artifactText.split(":")[1] + ":"
				+ artifactText.split(":")[3] + ":" + artifactText.split(":")[2]);

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
		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler(){
            @Override
            public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
            {
                exception.printStackTrace();
            }
        });
		return locator.getService(RepositorySystem.class);
	}

	private static RepositorySystemSession newSession(RepositorySystem system, String localAetherUrl) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository(localAetherUrl);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
		return session;
	}

	private static RemoteRepository newCentralRepository(String centralAetherUrl) {
		return new RemoteRepository.Builder("central", "default", centralAetherUrl).build();
	}
}