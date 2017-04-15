package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Mock resolver is used for testing. No artifacts are being resolved from repos here.
 *
 * Created by Zdenek Vales on 11.4.2017.
 */
public class MavenMockResolver implements MavenResolver {

    public static final String TEST_ARTIFACT_NAME = "test-jar.jar";

    @Override
    public File resolve(FoundArtifact artifact) {
        return new File(getClass().getResource(TEST_ARTIFACT_NAME).getFile());
    }

    @Override
    public Collection<File> resolveArtifacts(Collection<FoundArtifact> artifacts) {
        Random r = new Random();
        int artCount = r.nextInt(10)+1;
        List<File> files = new ArrayList<>(artCount);
        for(int i = 0; i < artCount; i++){
            files.add(new File(getClass().getResource(TEST_ARTIFACT_NAME).getFile()));
        }
        return files;
    }
}
