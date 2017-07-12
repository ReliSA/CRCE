package cz.zcu.kiv.crce.compatibility.dao.internal;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

/**
 * Context class responsible for connection management to MongoDB.
 * <p/>
 * Probably should be relocated to a public place in case more modules started using the database.
 * <p/>
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class DbContext {

    /**
     * Name of the database used by CRCE.
     */
    public static final String DEFAULT_DB_NAME = "crce";

    private static MongoClient client;

    /**
     * Singleton instance of Mongo connection driver. Client holds its own connection pool and therefore
     * should be used as singleton within the application
     */
    public static MongoClient getConnection() throws UnknownHostException {
        if (client == null) {
            client = new MongoClient("localhost", 27017);
        }

        return client;
    }

    /**
     * Close current connection. Probably used only on bundle shutdown.
     */
    public static void stop() {
        if (client != null) {
            client.close();
        }
    }
}
