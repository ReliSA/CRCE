package cz.zcu.kiv.crce.compatibility.dao.internal;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.metadata.dao.mongodb.DbContext;

/**
 * Factory for CompatibilityDao implementation using MongoDB.
 *
 * Ensures the dao object is singleton within the application.
 *
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityDaoMongoFactory {
    private static final Logger logger = LoggerFactory.getLogger(CompatibilityDaoMongoFactory.class);

    private CompatibilityDao dao;

    /**
     *
     * @return instance of CompatibilityDao MongoDB implementation.
     */
    public CompatibilityDao get() {
        System.out.println("MONGO GET");
        if(dao == null) {
            try {
                System.out.println("NEW MONGOIMPL");
                dao = new CompatibilityDaoMongoImpl(DbContext.getConnection());
            } catch (UnknownHostException e) {
                logger.error("Unable to open connection to the database!", e);
                return null;
            }
        }

        return dao;
    }


}
