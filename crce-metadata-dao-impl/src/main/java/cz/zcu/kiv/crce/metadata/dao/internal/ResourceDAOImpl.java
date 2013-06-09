package cz.zcu.kiv.crce.metadata.dao.internal;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
//import cz.zcu.kiv.crce.metadata.Property;
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
import java.util.Collection;
import java.util.Set;
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

        // TODO: check internal_id, autoincrement ??
        
        String conf = "data/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(conf);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);

        SqlSession session = factory.openSession();
        try {
            session.insert("org.apache.ibatis.Mapper.insertResource", resource.getId());
            session.commit();
        } finally {
            session.close();
        }

        // CAPABILITY
        for (Capability c : resource.getCapabilities()) {
            // save to capability
            c.getId();
            c.getNamespace();
            resource.getId();

            // cap_attribute
            for (Attribute<?> a : c.getAttributes()) {
                // save to cap_attribute, where the hell is attribute name ?!?
                a.getAttributeType();
                // a.getValue();
                a.getStringValue();
                a.getOperator();
            }

            // cap_directive
            Map<String, String> d = c.getDirectives();
            Set<String> k = d.keySet();
            Collection<String> v = d.values();

            String[] names = k.toArray(new String[k.size()]);
            String[] values = v.toArray(new String[v.size()]);

            for (int i = 0; i < k.size(); i++) {
                // save names[i] and values[i] to cap_directive
            }
        }

        // REQUIREMENT
        for (Requirement r : resource.getRequirements()) {
            r.getId();
            r.getNamespace();
            resource.getId();

            // req_attribute
            for (Attribute<?> a : r.getAttributes()) {
                // save to req_attribute, where the hell is attribute name ?!?
                a.getAttributeType();
                // a.getValue();
                a.getStringValue();
                a.getOperator();
            }

            // req_directive 
            Map<String, String> d = r.getDirectives();
            Set<String> k = d.keySet();
            Collection<String> v = d.values();

            String[] names = k.toArray(new String[k.size()]);
            String[] values = v.toArray(new String[v.size()]);

            for (int i = 0; i < k.size(); i++) {
                // save names[i] and values[i] to req_directive 
            }
        }
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
