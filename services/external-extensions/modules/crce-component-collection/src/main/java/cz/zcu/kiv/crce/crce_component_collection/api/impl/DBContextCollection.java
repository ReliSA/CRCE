package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.LoggerFactory;

/**
 * Setting the context for connection to the MongoDB database.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class DBContextCollection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public DBContextCollection(){
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("crce");
        collection = database.getCollection("collection");
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

}
