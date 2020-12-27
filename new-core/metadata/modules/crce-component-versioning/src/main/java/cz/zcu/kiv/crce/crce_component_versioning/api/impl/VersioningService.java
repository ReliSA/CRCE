package cz.zcu.kiv.crce.crce_component_versioning.api.impl;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import cz.zcu.kiv.crce.crce_component_versioning.api.VersioningServiceApi;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentDetailBean;
import org.bson.Document;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class VersioningService implements VersioningServiceApi {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public VersioningService() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("crce");
        collection = database.getCollection("versioning");
    }

    @Override
    public List<ComponentBean> getCompositeComponentAll() {
        List<ComponentBean> compositeBeans = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                ComponentBean cb = new ComponentBean(document.get("_id").toString(), document.get("name").toString(),
                        document.get("version").toString(), true);
                compositeBeans.add(cb);
            }
        }
        return compositeBeans;
    }

    @Override
    public ComponentDetailBean getCompositeComponentDetail(String id) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", id);
        Document doc = collection.find(searchQuery).first();
        if(doc != null){
            ComponentDetailBean componentDetailBean = new ComponentDetailBean(doc.get("_id").toString(),
                    doc.get("name").toString(), doc.get("version").toString(), (List) doc.get("child"));

            return componentDetailBean;
        }
        else{
            return null;
        }
    }

    @Override
    public boolean setCompositeComponent(String name, String version, List<String> listId) {
        Document doc = new Document("_id", new ObjectId().toString())
                .append("name", name)
                .append("version", version)
                .append("child", listId);

        try{
            collection.insertOne(doc);
            return true;
        }
        catch(MongoException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeCompositeComponent(String id) {
        try{
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", id);
            Document doc = collection.find(searchQuery).first();
            if(doc != null){
                collection.deleteOne(doc);
                return true;
            }
            else{
                return false;
            }
        }
        catch (MongoException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCompositeComponent(String id, String name, String version, List<String> listId) {
        try{
            Bson filter = new Document("_id", id);
            Bson newValue = new Document("name", name)
                    .append("version", version)
                    .append("child", listId);
            Bson updateOperationDocument = new Document("$set", newValue);
            collection.updateOne(filter, updateOperationDocument);
            return true;
        }

        catch (MongoException e){
            e.printStackTrace();
            return false;
        }
    }
}
