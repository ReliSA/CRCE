package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import com.fasterxml.jackson.databind.deser.impl.NoClassDefFoundDeserializer;
import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class CentralMavenLocatorTest {

    @Test
    public void testLocateArtifact() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.7.Final";
        String jarDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.jar";
        String pomDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.pom";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Returned artifact is null!", artifact);
        assertEquals("Wrong group id!", groupId, artifact.getGroupId());
        assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
        assertEquals("Wrong version!", version, artifact.getVersion());
        assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
        assertEquals("Wrong jar download link!", jarDownload, artifact.getJarDownloadLink());
        assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
        assertEquals("Wrong pom download link!", pomDownload, artifact.getPomDownloadLink());
    }

    @Test
    public void testLocateArtifacts() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String jarDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.jar";
        String pomDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.pom";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId);
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());

            String jd = String.format(jarDownloadTmplt, artifact.getVersion(), artifact.getVersion()),
                   pd = String.format(pomDownloadTmplt, artifact.getVersion(), artifact.getVersion());

            assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
            assertEquals("Wrong jar download link!", jd, artifact.getJarDownloadLink());
            assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
            assertEquals("Wrong pom download link!", pd, artifact.getPomDownloadLink());
        }
    }

    @Test
    public void testLocateArtifactByGroupId() {
        String groupId = "org.hibernate";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, null);
        assertNotNull("Null returned!", artifacts);
        assertFalse("No artifacts found for group id "+groupId, artifacts.isEmpty());
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertNotNull("Null artifact id!", artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());
        }
    }

    @Test
    @Ignore
    public void testLocateArtifactByIncludedPackage() {
        String packageName = "org.hibernate.dialect.function";
        // update this number as needed...
        int expCount = 880;

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(packageName);
        assertNotNull("Null returned!", artifacts);
        assertFalse("No artifacts containing package " + packageName + " found!", artifacts.isEmpty());
        assertEquals("Wrong number of artifacts located!", expCount, artifacts.size());
    }

    @Test
    @Ignore
    public void testLocatArtifactByIncludedPackageVersionFilter() {
        String packageName = "org.hibernate.dialect.function";
        int expCount = 880;

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(packageName);
        artifacts = locator.filter(artifacts, VersionFilter.HIGHEST_ONLY);

        assertNotNull("Null returned!",artifacts);
        assertFalse("No artifacts containing package "+packageName+" found!", artifacts.isEmpty());
        assertTrue("Wrong number of artifacts ("+artifacts.size()+"!", artifacts.size() < expCount);
    }

    @Test
    public void testGroupIdFilter() {
        Collection<FoundArtifact> foundArtifacts = new ArrayList<>();
        foundArtifacts.add(new SimpleFoundArtifact("asdf","","","",""));
        foundArtifacts.add(new SimpleFoundArtifact("test.group","","","",""));
        foundArtifacts.add(new SimpleFoundArtifact("groupId","","","",""));
        FoundArtifact fa = new SimpleFoundArtifact("test.group.id.artifact", "", "", "", "");
        foundArtifacts.add(fa);
        String groupIdFilter = "test.group.id";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        foundArtifacts = locator.filter(foundArtifacts, groupIdFilter);

        assertEquals("Only one item expected to pass!", 1, foundArtifacts.size());
        assertTrue("Artifact with correct groupId should be included!", foundArtifacts.contains(fa));
    }

    @Test
    public void testLocateArtifactByIncludedPackageBad() {
        String badPackageName = "asdasdasdagasd";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(badPackageName);
        assertNotNull("Null returned!", artifacts);
        assertTrue("Artifacts containing package "+badPackageName+" found!", artifacts.isEmpty());
    }

    @Test
    public void testFilterVersion() {
        String packageName = "org.hibernate.dialect.MimerSQLDialect";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(packageName);
        assertFalse("No artifacts found!", artifacts.isEmpty());
        int count = artifacts.size();

        Collection<FoundArtifact> highest = locator.filter(artifacts, VersionFilter.HIGHEST_ONLY);
        int hSize = highest.size();
        Collection<FoundArtifact> lowest = locator.filter(artifacts, VersionFilter.LOWEST_ONLY);
        int lSize = lowest.size();

        assertTrue("Highest versions count "+(hSize)+" should be lower than total count "+(count)+"!", hSize < count );
        assertTrue("Lowest versions count "+(lSize)+" should be lower than total count "+(count)+"!", lSize < count );
        assertEquals("Lowest version count sohuld be the same as the highest version count!", lSize, hSize);
    }

    @Test
    public void testLocateArtifacts2() {
        String fc = "org.specs.runner.Junit";
        int expCount = 54;

        CentralMavenRestLocator cmrl = new CentralMavenRestLocator();
        Collection<FoundArtifact> foundArtifacts = cmrl.locate(fc);

        assertNotNull("Null returned!", foundArtifacts);
        assertEquals("Wrong number of found artifacts!", expCount, foundArtifacts.size());
    }

    @Test
    public void testLocateArtifactByIncludedPackageHighestGMatch() {
        String fc = "org.hibernate.dialect.MimerSQLDialect";
        String expectedGid = "org.hibernate";
        int expCount = 174;

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> foundArtifacts = locator.locate(fc, true);

        assertNotNull("Null returned!", foundArtifacts);
        assertEquals("Wrong number of artifacts returned!", expCount, foundArtifacts.size());

        for (FoundArtifact fa : foundArtifacts) {
            assertEquals("Only artifacts with one groupId expected!", expectedGid, fa.getGroupId());
        }
    }

    @Test
    public void testLocateArtifactByIncludedPackageHighestGMatchFilterVersion() {
        String fc = "org.hibernate.dialect.MimerSQLDialect";
        String expectedGid = "org.hibernate";
        String expectedAid = "hibernate-core";
        String expectedAid2 = "hibernate";
        String expectedVersion = "5.2.10.Final";
        String expectedVersion2 = "3.2.7.ga";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> foundArtifacts = locator.locate(fc, true);
        foundArtifacts = locator.filter(foundArtifacts, VersionFilter.HIGHEST_ONLY);

        assertEquals("Only 2 artifacts expected!",2 , foundArtifacts.size());
        Iterator<FoundArtifact> faIt = foundArtifacts.iterator();
        FoundArtifact fa = faIt.next();
        assertEquals("Wrong groupId!", expectedGid, fa.getGroupId());
        assertTrue("Wrong aId "+fa.getArtifactId()+"!", fa.getArtifactId().equals(expectedAid) || fa.getArtifactId().equals(expectedAid2));
        assertTrue("Wrong version "+fa.getVersion()+"!", fa.getVersion().equals(expectedVersion) || fa.getVersion().equals(expectedVersion2));

        fa = faIt.next();
        assertEquals("Wrong groupId!", expectedGid, fa.getGroupId());
        assertTrue("Wrong aId "+fa.getArtifactId()+"!", fa.getArtifactId().equals(expectedAid) || fa.getArtifactId().equals(expectedAid2));
        assertTrue("Wrong version "+fa.getVersion()+"!", fa.getVersion().equals(expectedVersion) || fa.getVersion().equals(expectedVersion2));
    }

    @Test
    public void testLocateArtifactByIncludedPackageManualGroupIdFilterVersion() {
        String fc = "org.hibernate.dialect.MimerSQLDialect";
        String groupIdFilter = "org.ow2.easybeans";
        String expectedAId = "easybeans-uberjar-hibernate";
        String expectedVersionH = "1.1.1";
        String expectedVersionL = "1.0.0";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> beforeFilter = locator.locate(fc, groupIdFilter, false);
        assertNotNull("Null returned for search!", beforeFilter);

        Collection<FoundArtifact> hv = locator.filter(beforeFilter, VersionFilter.HIGHEST_ONLY);
        Collection<FoundArtifact> lv = locator.filter(beforeFilter, VersionFilter.LOWEST_ONLY);

        assertEquals("Only one artifact expected for highest version!", 1, hv.size());
        Iterator<FoundArtifact> faIt = hv.iterator();
        FoundArtifact fa = faIt.next();
        assertEquals("Wrong groupId!", groupIdFilter, fa.getGroupId());
        assertEquals("Wrong artifactId!", expectedAId, fa.getArtifactId());
        assertEquals("Wrong highest version!", expectedVersionH, fa.getVersion());

        faIt = lv.iterator();
        fa = faIt.next();
        assertEquals("Wrong groupId!", groupIdFilter, fa.getGroupId());
        assertEquals("Wrong artifactId!", expectedAId, fa.getArtifactId());
        assertEquals("Wrong lowest version!", expectedVersionL, fa.getVersion());
    }

    @Test
    public void testLocateArtifactByIncludedPackageWrongManualGid() {
        String fc = "org.hibernate.dialect.MimerSQLDialect";
        String groupIdFilter = "asdf.asdf.asdf";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> artifacts = locator.locate(fc, groupIdFilter, false);

        assertNotNull("Null returned!", artifacts);
        assertTrue("No artifacts expected for manual groupId filter "+groupIdFilter+"!", artifacts.isEmpty());
    }

    @Test
    public void testLocateArtifactByIncludedPackageWrongAutoGid() {
        String fc = "org.hibernate.dialect.MimerSQLDialect";
        String groupIdFilter = "asdf";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> artifacts = locator.locate(fc, groupIdFilter, false);

        assertNotNull("Null returned!", artifacts);
        assertTrue("No artifacts expected for manual groupId filter "+groupIdFilter+"!", artifacts.isEmpty());
    }

    @Test
    public void testLocateArtifactVersionFilter() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String lowestVersion = "3.3.0.CR1";
        String highestVersion = "5.2.10.Final";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();
        Collection<FoundArtifact> foundArtifacts = locator.locate(groupId, artifactId);
        assertNotNull("Null returned!", foundArtifacts);
        Collection<FoundArtifact> lowestVersions = locator.filter(foundArtifacts, VersionFilter.LOWEST_ONLY);
        Collection<FoundArtifact> highestVersions = locator.filter(foundArtifacts, VersionFilter.HIGHEST_ONLY);

        assertEquals("Only 1 artifact in lowestVersions expected!", 1, lowestVersions.size());
        FoundArtifact fa = lowestVersions.iterator().next();
        assertEquals("Wrong groupId of lowest version!", groupId, fa.getGroupId());
        assertEquals("Wrong artifactId of lowest version!", artifactId, fa.getArtifactId());
        assertEquals("Wrong version of lowest version!", lowestVersion, fa.getVersion());

        assertEquals("Only 1 artifact in highestVersions expected!", 1, highestVersions.size());
        fa = highestVersions.iterator().next();
        assertEquals("Wrong groupId of highest version!", groupId, fa.getGroupId());
        assertEquals("Wrong artifactId of highest version!", artifactId, fa.getArtifactId());
        assertEquals("Wrong version of highest version!", highestVersion, fa.getVersion());
    }

}
