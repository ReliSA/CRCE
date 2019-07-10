package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import cz.zcu.kiv.crce.crce_component_collection.api.CollectionServiceApi;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import com.mongodb.client.MongoCollection;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class CollectionService implements CollectionServiceApi {
    private DBContextCollection dbContextCollection;
    private MongoCollection<Document> collection;

    public CollectionService() {
        dbContextCollection = new DBContextCollection();
        collection = dbContextCollection.getCollection();
    }

    @Override
    public List<CollectionBean> getCollectionComponentAll() {
        List<CollectionBean> compositeBeans = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                CollectionBean cb = new CollectionBean(document.get("_id").toString(), document.get("name").toString(),
                        document.get("version").toString(), true);
                compositeBeans.add(cb);
            }
        }
        return compositeBeans;
    }

    @Override
    public CollectionDetailBean getCollectionComponentDetail(String id) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", id);
        Document doc = collection.find(searchQuery).first();
        if(doc != null){
            CollectionDetailBean componentDetailBean = new CollectionDetailBean(doc.get("_id").toString(),
                    doc.get("name").toString(), doc.get("version").toString(), (List) doc.get("specificArtifacts"),
                    (List)doc.get("parameters"), (List)doc.get("rangeArtifacts"));

            return componentDetailBean;
        }
        else{
            return null;
        }
    }

    @Override
    public boolean setCollectionComponent(String name, String version, List<String> specificArtifacts,
                                          List<String> parameters, List<String> rangeArtifacts) {
        Document doc = new Document("_id", new ObjectId().toString())
                .append("name", name)
                .append("version", version)
                .append("specificArtifacts", specificArtifacts)
                .append("parameters", parameters)
                .append("rangeArtifacts", rangeArtifacts);

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
    public boolean removeCollectionComponent(String id) {
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
    public boolean updateCollectionComponent(String id, String name, String version,  List<String> specificArtifacts,
                                             List<String> parameters, List<String> rangeArtifacts) {
        try{
            Bson filter = new Document("_id", id);
            Bson newValue = new Document("name", name)
                    .append("version", version)
                    .append("specificArtifacts", specificArtifacts)
                    .append("parameters", parameters)
                    .append("rangeArtifacts", rangeArtifacts);
            Bson updateOperationDocument = new Document("$set", newValue);
            collection.updateOne(filter, updateOperationDocument);
            return true;
        }

        catch (MongoException e){
            e.printStackTrace();
            return false;
        }
    }

    public void closeMongoClient(){
        dbContextCollection.getMongoClient().close();
    }
}
