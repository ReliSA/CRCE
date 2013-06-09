package cz.zcu.kiv.crce.metadata.dao.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Jan Dyrczyk (dyrczyk@students.zcu.cz)
 * @author Pavel Cihlář
 */
public class ResourceDAOImpl implements ResourceDAO {

    //private volatile MetadataService metadataService;
    Map<URI, Map<URI, Resource>> repositories = new HashMap<>();

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null) {
                Resource resource = resources.get(uri);
                if (resource != null) {
                    return resource;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized List<Resource> loadResources(Repository repository) throws IOException {
        Map<URI, Resource> resources = repositories.get(repository.getURI());
        if (resources != null) {
            return Collections.unmodifiableList(new ArrayList<>(resources.values()));
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized void saveResource(Resource resource) throws IOException {

        String conf = "data/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(conf);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        
        // RESOURCE VALUES - int internal_ID, varchar id
        String resourceID = resource.getId();
        //String resourceVersion = resource.getVersion().toString(); // Missing in the database, not needed?

        SqlSession session = factory.openSession();
        try {
            session.insert("org.apache.ibatis.Mapper.insertResource", resourceID);
            session.commit();
        } finally {
            session.close();
        }

        // CAPABILITY
        for (Capability c : resource.getCapabilities()) {
            c.getName();
            for (Property p : c.getProperties()) {
                p.getName();
                p.getType();
                p.getValue();
            }
        }

        // REQUIREMENT
        for (Requirement r : resource.getRequirements()) {
            r.getName();
            r.getComment();
            r.getFilter();
        }
        
        /*
        
        // CAPABILITY VALUES - int internal_ID, varchar id, varchar namespace, int capability_id, int resource_id
        //CAP_ATTRIBUTE - int internal_ID, varchar name, varchar type, varchar value, varchar operator, int capability_id
        //CAP_DIRECTIVE - int internal_ID, varchar name, varchar value, int capability_id
        String[] capabilitiesNames = new String[resourceCapabilities.length];
        //Property[] capabilitiesProperty = new Property[resourceCapabilities.length];
        for (int i = 0; i < resourceCapabilities.length; i++) {
            capabilitiesNames[i] = resourceCapabilities[i].getName();
        }

        // REQUIREMENT VALUES - int internal_ID, varchar namespace, int requirement_id, int resource_id
        //REQ_ATTRIBUTE - int internal_ID, varchar name, varchar type, varchar value, varchar operator, int requirement_id
        //REQ_DIRECTIVE - int internal_ID, varchar name, varchar value, int requirement_id
        String[] requirementsNames = new String[resourceRequirements.length];
        for (int i = 0; i < resourceRequirements.length; i++) {
            requirementsNames[i] = resourceRequirements[i].getName();
            // getComment - not needed?
        }
        
        */
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {

        Resource resource = loadResource(uri);

        if (resource != null) {
            String resourceID = resource.getId();
            String conf = "data/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(conf);
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            SqlSessionFactory factory = builder.build(inputStream);

            SqlSession session = factory.openSession();
            try {
                session.insert("org.apache.ibatis.Mapper.deleteResource", resourceID);
                session.commit();
            } finally {
                session.close();
            }
        }
    }

    @Override
    public synchronized boolean existsResource(URI uri) throws IOException {    // TODO just simple select, check if return null
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null && resources.containsKey(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {  // not needed
        Map<URI, Resource> resources = repositories.get(repository.getURI());
        return resources.containsKey(uri);
    }

    @Override
    public String toString() {
        return "Persisted repositories and resources:\r\n" + repositories.toString();
    }
}
