package cz.zcu.kiv.crce.metadata.dao.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

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

    /**
     * Default connection string for mongo database.
     */
    private static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";

    /**
     * Name of environment variable to store connection string.
     */
    private static final String ENV_CONNECTION = "mongo_connection";

    /**
     * Field for cache client instance
     */
    private static MongoClient client;

    /**
     * Singleton instance of Mongo connection driver. Client holds its own connection pool and therefore
     * should be used as singleton within the application.
     * Method try to read environment property which contains connection string {@link DbContext#ENV_CONNECTION}
     * for database.
     * @see MongoClientURI
     * */
    public static MongoClient getConnection() throws UnknownHostException {
        if (client == null) {
            String connectionString = DEFAULT_CONNECTION;
            if(System.getenv().containsKey(ENV_CONNECTION)) {
                connectionString = System.getenv().get(ENV_CONNECTION);
            }
            MongoClientURI connectionParams = new MongoClientURI(connectionString);
            client = new MongoClient(connectionParams);
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
