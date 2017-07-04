package cz.zcu.kiv.crce.metadata.dao.mongodb;

import java.util.Dictionary;

import org.mongojack.JacksonDBCollection;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Base class for MongoDB based DAOs in CRCE.
 *
 * Handles connection to db and provide basic method to open collections.
 *
 * Implements ManagedService - change of database on air is supported. This feature is however
 * meant primarily for use during integration tests. Use with caution.
 *
 * Database access is thread safe due to MongoDB inner implementation. Currently whole database (DB instance) is
 * locked - multiple read locks and single write lock with higher priority than the read locks.
 * Read more at http://docs.mongodb.org/ecosystem/drivers/java-concurrency/
 * and http://docs.mongodb.org/manual/faq/concurrency/
 *
 * Date: 22.9.16
 *
 * @author Jakub Danek
 */
public abstract class BaseMongoDao implements ManagedService {
    private static final Logger logger = LoggerFactory.getLogger(BaseMongoDao.class);

    /*
        OSGi stuff
    */
    @Override
    public void updated(Dictionary<String, ?> props) throws ConfigurationException {
        String name = DbContext.DEFAULT_DB_NAME;

        if ( props != null) {
            Object o = props.get("cz.zcu.kiv.crce.mongodb.dbname");
            if ( o != null )
                name = (String) o;
        }

        if ( !dbName.equals(name) ) {
            logger.info("Changing database to: {}", name);
            openDB(name);
            logger.info("Database has been changed.");
        }
    }

    /**
     * Name of currently used database.
     */
    private volatile String dbName = DbContext.DEFAULT_DB_NAME;

    /**
     * Currently used database reference.
     */
    private DB db;
    private Object dbLock;
    /**
     * MongoClient reference holding current connection.
     */
    private MongoClient client;

    /**
     * Creates new CompatibilityDao implementation for MongoDB
     * @param client MongoDB driver client with open connection.
     */
    protected BaseMongoDao(MongoClient client) {
        this.client = client;
        this.dbLock = new Object();
        openDB(dbName);
    }

    /**
     * Thread-safe method for switching database on-air.
     * @param name name of the new database
     */
    protected void openDB(String name) {
        synchronized (dbLock) {
            dbName = name;
            db = client.getDB(name);
        }
    }

    protected DBCollection getCollection(String name) {
        synchronized (dbLock) {
            return db.getCollection(name);
        }
    }

    protected  <T,K> JacksonDBCollection<T, K> getCollection(String name, Class<T> resourceType, Class<K> keyType) {
        return JacksonDBCollection.wrap(getCollection(name), resourceType, keyType);
    }
}
