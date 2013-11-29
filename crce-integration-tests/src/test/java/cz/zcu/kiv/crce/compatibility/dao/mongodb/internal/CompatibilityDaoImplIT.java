package cz.zcu.kiv.crce.compatibility.dao.mongodb.internal;

import static org.junit.Assert.*;
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
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationAdmin;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;

/**
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CompatibilityDaoImplIT {

    private static final String TEST_DB = "compatibilityDaoImplIT";

    @Inject
    private CompatibilityFactory m_factory;

    @Inject
    private CompatibilityDao m_dao;

    @Inject
    private ConfigurationAdmin m_config;

    private MongoClient client;
    private DB db;

    @Before
    public void before() throws Exception {
        client = new MongoClient("localhost");
        db = client.getDB(TEST_DB);
        configureDao();
        Thread.sleep(300); //wait for DAO configuration
    }

    /**
     * Change database the DAO uses to test db, which can be dropped after each test method.
     * @throws Exception
     */
    private void configureDao() throws Exception{
        org.osgi.service.cm.Configuration config = m_config.getConfiguration("cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao", null);
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
        client.close();
    }

    @Configuration
    public Option[] configure() throws Exception {
        return options(
                mavenBundle("cz.zcu.kiv.jacc","types-cmp", "1.0.1"),
                mavenBundle("cz.zcu.kiv.crce","crce-compatibility-api","1.0.0-SNAPSHOT"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager", "3.0.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.6.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository", "1.6.6"),
                mavenBundle("org.osgi", "org.osgi.compendium", "4.3.1"),
                mavenBundle("org.slf4j", "slf4j-api", "1.7.4"),
                mavenBundle("ch.qos.logback", "logback-core", "1.0.9"),
                mavenBundle("ch.qos.logback", "logback-classic", "1.0.9"),
                mavenBundle("cz.zcu.kiv.crce","crce-metadata-api","1.0.0-SNAPSHOT"),
                mavenBundle("cz.zcu.kiv.crce","crce-compatibility-impl","1.0.0-SNAPSHOT"),
                mavenBundle("cz.zcu.kiv.crce","crce-compatibility-dao-api","1.0.0-SNAPSHOT"),
                mavenBundle("cz.zcu.kiv.crce","crce-compatibility-dao-mongodb","1.0.0-SNAPSHOT"),
                mavenBundle("org.mongodb", "mongo-java-driver", "2.11.3"),

                junitBundles());
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
        Compatibility test = m_factory.createCompatibility(null, "cz.zcu.kiv.crce.compatibility.dao.test.save", new Version(1, 0, 0),
                                                            null, new Version(0, 1, 0), Difference.MUT, null);

        test = m_dao.saveCompatibility(test);
        Compatibility read = m_dao.readCompability(test.getId());

        assertNotNull("Save must have failed. Read returned null.", read);
        assertEquals("Expected the compatibilities to be equal.", test, read);

        //creating new passed, now try update

        test = m_factory.createCompatibility(test.getId(), "modified", test.getResourceVersion(),
                                                test.getBaseResourceName(), test.getBaseResourceVersion(), test.getDiffValue(), null);

        m_dao.saveCompatibility(test);
        read = m_dao.readCompability(read.getId());
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
            test = m_factory.createCompatibility(null, RESOURCE_NAME, RESOURCE_VERSION,
                    version, DIFFERENCES[i], null);
            test = m_dao.saveCompatibility(test);
            testData.add(test);
            i++;
        }

        //create one different resource and several different resource versions to check
        //the dao method doesnt return all it can find
        test = m_factory.createCompatibility(null, "cz.zcu.kiv.unwanted.resource.name", RESOURCE_VERSION,
                null, new Version(33,0,33), Difference.MUT, null);
        test = m_dao.saveCompatibility(test);
        testData.add(test);


        //these test data are also used for higher-version search
        test = m_factory.createCompatibility(null, RESOURCE_NAME, new Version(1,1,2),
                VERSIONS[1], DIFFERENCES[1], null);
        test = m_dao.saveCompatibility(test);
        testData.add(test);
        test = m_factory.createCompatibility(null, RESOURCE_NAME, new Version(1,2,0),
                VERSIONS[1], DIFFERENCES[1], null);
        test = m_dao.saveCompatibility(test);
        testData.add(test);
        test = m_factory.createCompatibility(null, RESOURCE_NAME, new Version(42,0,0),
                VERSIONS[1], Difference.MUT, null);
        test = m_dao.saveCompatibility(test);
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

        List<Compatibility> testee = m_dao.listOwnedCompatibilities(RESOURCE_NAME, RESOURCE_VERSION);

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

        List<Compatibility> higher = m_dao.findHigher(RESOURCE_NAME, VERSIONS[1], diffs);
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

        List<Compatibility> lower = m_dao.findLower(RESOURCE_NAME, RESOURCE_VERSION, diffs);
        assertEquals("Expected to find exactly 2 resources!", 2, lower.size());
        assertTrue(vers.contains(lower.get(0).getBaseResourceVersion()));
        assertTrue(vers.contains(lower.get(1).getBaseResourceVersion()));
    }

    @Test
    public void testNoDifferencesAllowed() throws Exception {
        createTestDataForListing();

        List<Compatibility> lower = m_dao.findLower(RESOURCE_NAME, RESOURCE_VERSION, null);
        List<Compatibility> higher = m_dao.findHigher(RESOURCE_NAME, VERSIONS[0], new ArrayList<Difference>());

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
        Compatibility test = m_factory.createCompatibility(null, "cz.zcu.kiv.crce.compatibility.dao.test.remove",new Version(1,0,0),
                null, new Version(0,1,0), Difference.MUT, null);

        test = m_dao.saveCompatibility(test);
        Compatibility read = m_dao.readCompability(test.getId());
        assertNotNull("Save must have failed. Read returned null.", read);

        m_dao.deleteCompatibility(read);
        test = m_dao.readCompability(test.getId());
        assertNull("Expected to find no results after removal.", test);

    }
}