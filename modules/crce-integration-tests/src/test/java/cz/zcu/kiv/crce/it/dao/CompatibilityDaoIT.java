package cz.zcu.kiv.crce.it.dao;

import static org.ops4j.pax.exam.CoreOptions.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.cm.ConfigurationAdmin;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CompatibilityDaoIT extends IntegrationTestBase {

    private static final String TEST_DB = "compatibilityDaoImplIT";

    /**
     * Container includes and provides these packages when container is started.
     */
    private static final String systemPackages
            = "com.sun.*,javax.xml.*,com.sun.org.apache.xerces.internal.*,"
            + "javax.accessibility,javax.annotation,javax.inject,javax.jmdns,javax.jms,javax.mail,"
            + "javax.mail.internet,javax.management,javax.management.modelmbean,javax.management.remote,"
            + "javax.microedition.io,javax.naming,javax.naming.spi,javax.script,javax.security.auth.x500,javax.servlet,"
            + "javax.servlet.http,javax.servlet.jsp,javax.sql,"
            + "org.w3c.dom,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers,"
            + "org.w3c.dom.xpath,sun.io,org.w3c.dom.ls,"
            + "com.sun.java_cup.internal,com.sun.xml.internal.bind.v2,"
            + "javax.net,javax.net.ssl,javax.transaction.xa,com.jcraft.jsch,"
            + "org.apache.commons.collections.map,org.apache.commons.httpclient,"
            + "org.apache.commons.httpclient.auth,org.apache.commons.httpclient.methods,"
            + "org.apache.commons.httpclient.params,org.apache.commons.httpclient.util,"
            + "org.apache.jackrabbit.webdav,org.apache.jackrabbit.webdav.client.methods,"
            + "org.apache.jackrabbit.webdav.property,org.apache.jackrabbit.webdav.version,"
            + "org.apache.jackrabbit.webdav.xml,org.apache.tools.ant";

    @Inject
    private CompatibilityFactory compatibilityFactory;

    @Inject
    private CompatibilityDao compatibilityDao;

    @Inject
    private ConfigurationAdmin configurationAdmin;

    private MongoClient mongoClient;
    private DB db;

    @Before
    @Override
    public void before() throws Exception {
        mongoClient = new MongoClient("localhost");
        db = mongoClient.getDB(TEST_DB);
        configureDao();
        Thread.sleep(300); //wait for DAO configuration
    }

    /**
     * Change database the DAO uses to test db, which can be dropped after each test method.
     * @throws Exception
     */
    private void configureDao() throws Exception{
        org.osgi.service.cm.Configuration config = configurationAdmin.getConfiguration("cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao", null);
        Dictionary<String, Object> props = config.getProperties();
        if(props == null) {
            props = new Hashtable<>();
        }
        props.put("cz.zcu.kiv.crce.mongodb.dbname", TEST_DB);
        config.update(props);
    }

    @After
    public void after() throws Exception {
        db.dropDatabase();
        mongoClient.close();
    }

    @Configuration
    public Option[] configuration() throws Exception {
        return options(
                systemPackage(systemPackages),
                junitBundles(),
                Options.Felix.dependencyManager(),
                Options.logging(),
                Options.Osgi.compendium(),
                //Options.Osgi.core(),
                Options.Felix.configAdmin(),
                Options.Felix.bundleRepository(),

                mavenBundle("org.mongodb", "mongo-java-driver"),

                mavenBundle("commons-io", "commons-io"),
                mavenBundle("commons-net", "commons-net"),
                mavenBundle("org.apache.commons", "commons-vfs2"),
                mavenBundle("org.ow2.asm", "asm-all"),
                mavenBundle("org.apache.commons", "commons-lang3"),

                mavenBundle("cz.zcu.kiv.jacc", "types-cmp"),
                mavenBundle("cz.zcu.kiv.jacc", "javatypes"),
                mavenBundle("cz.zcu.kiv.jacc", "javatypes-cmp"),
                mavenBundle("cz.zcu.kiv.jacc", "javatypes-loader"),
                mavenBundle("cz.zcu.kiv.obcc", "bundle-loader"),
                mavenBundle("cz.zcu.kiv.obcc", "bundle-cmp"),
                mavenBundle("cz.zcu.kiv.obcc", "bundle-types"),

                Options.Crce.pluginApi(),
                Options.Crce.metadataApi(),
                Options.Crce.metadataServiceApi(),
                Options.Crce.metadataIndexer(),
                Options.Crce.metadataOsgiBundle(),
                Options.Crce.repositoryApi(),
                Options.Crce.compatibilityApi(),
                Options.Crce.compatibilityDaoApi(),
                Options.Crce.compatibilityDaoMongo(),
                Options.Crce.compatibilityImpl()
        );
    }

    /**
     * Test the driver was able to connect to the database.
     * @throws Exception
     */
    private void connectionCheck() throws Exception {
        DBCollection col = db.getCollection("connectionTest");
        try {
            col.insert(new BasicDBObject("pokus", 5));
        } catch (MongoException ex) {
            fail("Unable to connect to MongoDB. All ITs are going to fail.");
        }

    }

    /**
     * Test save of a new compatibility and its update.
     * @throws Exception
     */
    @Test
    public void compatibilitySaveTest() throws Exception {
        Diff root = compatibilityFactory.createEmptyDiff();
        root.setLevel(DifferenceLevel.PACKAGE);
        ;
        root.setNamespace("osgi.wiring.package");
        root.setName("cz.zcu.kiv");
        root.setRole(DifferenceRole.CAPABILITY);
        root.setValue(Difference.MUT);

        Diff child = compatibilityFactory.createEmptyDiff();
        root.setLevel(DifferenceLevel.TYPE);
        ;
        root.setName("cz.zcu.kiv.Clazz1");
        root.setValue(Difference.DEL);
        root.addChild(child);

        child = compatibilityFactory.createEmptyDiff();
        root.setLevel(DifferenceLevel.TYPE);
        ;
        root.setName("cz.zcu.kiv.Clazz2");
        root.setValue(Difference.INS);
        root.addChild(child);

        List<Diff> diffs = new ArrayList<>();
        diffs.add(root);


        Compatibility test = compatibilityFactory.createCompatibility(null, "cz.zcu.kiv.crce.compatibility.dao.test.save", new Version(1, 0, 0),
                null, new Version(0, 1, 0), Difference.MUT, diffs, Contract.SYNTAX);


        test = compatibilityDao.saveCompatibility(test);
        Compatibility read = compatibilityDao.readCompability(test.getId());

        assertNotNull("Save must have failed. Read returned null.", read);
        assertEquals("Expected the compatibilities to be equal.", test, read);

        //creating new passed, now try update

        test = compatibilityFactory.createCompatibility(test.getId(), "modified", test.getResourceVersion(),
                test.getBaseResourceName(), test.getBaseResourceVersion(), test.getDiffValue(), new ArrayList<Diff>(), Contract.SYNTAX);

        compatibilityDao.saveCompatibility(test);
        read = compatibilityDao.readCompability(read.getId());
        assertEquals("Expected the compatibilities to be equal.", test, read);
    }

    /*
            LISTING TESTS
     */

    /*
        static constants for LISTING tests
     */
    private static final String RESOURCE_NAME = "cz.zcu.kiv.crce.compatibility.dao.test.listing";
    private static final Version RESOURCE_VERSION = new Version(3,0,1);
    private static final Version VERSIONS[] = {new Version(1,0,0), new Version(1, 1,0), new Version(1,2,4), new Version(2,0,0)};
    private static final Difference DIFFERENCES[] = {Difference.GEN, Difference.NON, Difference.GEN, Difference.SPE};

    /**
     * Prepare test data for listing case
     * @return list of created compatibilities which can be then checked for equality
     */
    private List<Compatibility> createTestDataForListing() {
        List<Compatibility> testData = new ArrayList<>(4);

        Compatibility test;
        int i = 0;
        for(Version version : VERSIONS) {
            test = compatibilityFactory.createCompatibility(null, RESOURCE_NAME, RESOURCE_VERSION,
                    version, DIFFERENCES[i], new ArrayList<Diff>(), Contract.SYNTAX);
            test = compatibilityDao.saveCompatibility(test);
            testData.add(test);
            i++;
        }

        //create one different resource and several different resource versions to check
        //the dao method doesnt return all it can find
        test = compatibilityFactory.createCompatibility(null, "cz.zcu.kiv.unwanted.resource.name", RESOURCE_VERSION,
                null, new Version(33, 0, 33), Difference.MUT, new ArrayList<Diff>(), Contract.SYNTAX);
        test = compatibilityDao.saveCompatibility(test);
        testData.add(test);


        //these test data are also used for higher-version search
        test = compatibilityFactory.createCompatibility(null, RESOURCE_NAME, new Version(1,1,2),
                VERSIONS[1], DIFFERENCES[1], new ArrayList<Diff>(), Contract.SYNTAX);
        test = compatibilityDao.saveCompatibility(test);
        testData.add(test);
        test = compatibilityFactory.createCompatibility(null, RESOURCE_NAME, new Version(1,2,0),
                VERSIONS[1], DIFFERENCES[1], new ArrayList<Diff>(), Contract.SYNTAX);
        test = compatibilityDao.saveCompatibility(test);
        testData.add(test);
        test = compatibilityFactory.createCompatibility(null, RESOURCE_NAME, new Version(42,0,0),
                VERSIONS[1], Difference.MUT, new ArrayList<Diff>(), Contract.SYNTAX);
        test = compatibilityDao.saveCompatibility(test);
        testData.add(test);


        return testData;
    }

    /**
     * Test listing of compatibility data created for a resource
     * @throws Exception
     */
    @Test
    public void compatibilityListTest() throws Exception {
        List<Compatibility> testData = createTestDataForListing();

        List<Compatibility> testee = compatibilityDao.listOwnedCompatibilities(RESOURCE_NAME, RESOURCE_VERSION);

        assertEquals("Expected to get same amount of results as VERSIONS", VERSIONS.length, testee.size());
        for(Compatibility test : testee) {
            assertTrue("Expected testData to contain all of the testees.", testData.contains(test));
        }
    }

    /**
     * Test for higher versions of component. Created test data contain versions different by
     * major, minor and micro (only one at a time) to cover all cases.
     * @throws Exception
     */
    @Test
    public void findHigherCompatibilitiesTest() throws Exception {
        createTestDataForListing();

        List<Difference> diffs = new ArrayList<>(1);
        diffs.add(DIFFERENCES[1]);

        List<Compatibility> higher = compatibilityDao.findHigher(RESOURCE_NAME, VERSIONS[1], diffs);
        assertEquals("Expected to find exactly 3 resource!", 3, higher.size());
    }

    /**
     * More simple version of findHigherCompatibilitiesTest to check correct modification
     * of behaviour on operation change.
     * @throws Exception
     */
    @Test
    public void findLowerCompatibilitiesTest() throws Exception {
        createTestDataForListing();

        List<Difference> diffs = new ArrayList<>(1);
        diffs.add(DIFFERENCES[0]);

        List<Version> vers = new ArrayList<>(2);
        vers.add(VERSIONS[0]);
        vers.add(VERSIONS[2]);

        List<Compatibility> lower = compatibilityDao.findLower(RESOURCE_NAME, RESOURCE_VERSION, diffs);
        assertEquals("Expected to find exactly 2 resources!", 2, lower.size());
        assertTrue(vers.contains(lower.get(0).getBaseResourceVersion()));
        assertTrue(vers.contains(lower.get(1).getBaseResourceVersion()));
    }

    @Test
    public void testNoDifferencesAllowed() throws Exception {
        createTestDataForListing();

        List<Compatibility> lower = compatibilityDao.findLower(RESOURCE_NAME, RESOURCE_VERSION, null);
        List<Compatibility> higher = compatibilityDao.findHigher(RESOURCE_NAME, VERSIONS[0], new ArrayList<Difference>());

        assertEquals("Expected no lower versions with null differences to be found!", 0, lower.size());
        assertEquals("Expected no higher versions with no differences to be found!", 0, higher.size());
    }

    /*
        LISTING TESTS end
     */

    /**
     * Test removing of a Compatibility
     * @throws Exception
     */
    @Test
    public void compatibilityDeleteTest() throws Exception {
        Compatibility test = compatibilityFactory.createCompatibility(null, "cz.zcu.kiv.crce.compatibility.dao.test.remove",new Version(1,0,0),
                null, new Version(0, 1, 0), Difference.MUT, new ArrayList<Diff>(), Contract.SYNTAX);

        test = compatibilityDao.saveCompatibility(test);
        Compatibility read = compatibilityDao.readCompability(test.getId());
        assertNotNull("Save must have failed. Read returned null.", read);

        compatibilityDao.deleteCompatibility(read);
        test = compatibilityDao.readCompability(test.getId());
        assertNull("Expected to find no results after removal.", test);

    }
}