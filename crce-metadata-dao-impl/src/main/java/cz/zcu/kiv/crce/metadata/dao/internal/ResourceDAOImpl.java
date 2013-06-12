package cz.zcu.kiv.crce.metadata.dao.internal;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;

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
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.impl.GenericAttributeType;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import tables.Cap_directive;
import tables.Req_attribute;
import tables.Req_directive;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Jan Dyrczyk (dyrczyk@students.zcu.cz)
 * @author Pavel Cihlář
 */
public class ResourceDAOImpl implements ResourceDAO {

    //private volatile MetadataService metadataService;
    Map<URI, Map<URI, Resource>> repositories = new HashMap<>();
    private volatile ResourceFactory resourceFactory;

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice

        if (uri != null) {
            //Resource resource = loadResource(uri);
            String resourceID = uri.toString();

            Resource resource = resourceFactory.createResource(resourceID);

            String conf = "data/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(conf);
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            SqlSessionFactory factory = builder.build(inputStream);

            // Load capability
            //Capability cap = null; 
            Capability cap = resourceFactory.createCapability(resourceID);

            try (SqlSession session = factory.openSession()) {
                List<String> selectedCapability = session.selectList("org.apache.ibatis.Mapper.getCapability", resourceID);
                session.close();
                //String capInternal_id = selectedCapability.get(0);
                //String cap_id = selectedCapability.get(1); //TODO unused
                //String capabilityNamespace = selectedCapability.get(2); //TODO unused
                String capability_id = selectedCapability.get(3);
                //TODO save cap_id and namespace

                SqlSession session2 = factory.openSession();
                List<String> selectedCapDirective = session2.selectList("org.apache.ibatis.Mapper.getCapabilityDirective", capability_id);
                session2.close();
                String capDirName = selectedCapDirective.get(1);
                String capDirValue = selectedCapDirective.get(2);

                cap.setDirective(capDirName, capDirValue);

                SqlSession session3 = factory.openSession();
                List<String> selectedCapAttribute = session3.selectList("org.apache.ibatis.Mapper.getCapabilityAttribute", capability_id);
                session3.close();
                String capAttribute_type = selectedCapAttribute.get(1);
                String capAttribute_name = selectedCapAttribute.get(2);
                String capAttribute_value = selectedCapAttribute.get(3);
                //String capAttribute_operator = selectedCapAttribute.get(4); // TODO unfinished
                //cap_att.setName();  // TODO check QNAME
                GenericAttributeType attributeType = new GenericAttributeType(capAttribute_name, capAttribute_type);
                cap.setAttribute(attributeType, capAttribute_value);

                resource.addCapability(cap);
            }

            // Load requirements
            //Requirement req = null;
            Requirement req = resourceFactory.createRequirement(resourceID);
            List<String> selectedRequirement;
            try (SqlSession session = factory.openSession()) {
                selectedRequirement = session.selectList("org.apache.ibatis.Mapper.getRequirement", resourceID);
                //String req_id = selectedRequirement.get(1); //TODO unused
                //String requirementNamespace = selectedRequirement.get(2); //TODO unused
                String requirement_id = selectedRequirement.get(3);
                // TODO save namespace and req_id

                SqlSession session2 = factory.openSession();
                List<String> selectedreqDirective = session2.selectList("org.apache.ibatis.Mapper.getRequirementDirective", requirement_id);
                session2.close();
                String reqDirName = selectedreqDirective.get(1);
                String reqDirValue = selectedreqDirective.get(2);

                req.setDirective(reqDirName, reqDirValue);

                SqlSession session3 = factory.openSession();
                List<String> selectedreqAttribute = session3.selectList("org.apache.ibatis.Mapper.RequirementAttribute", requirement_id);
                session3.close();
                String reqAttribute_type = selectedreqAttribute.get(1);
                String reqAttribute_name = selectedreqAttribute.get(2);
                String reqAttribute_value = selectedreqAttribute.get(3);
                //String reqAttribute_operator = selectedreqAttribute.get(4);  //not finished          
                GenericAttributeType attributeType = new GenericAttributeType(reqAttribute_name, reqAttribute_type);
                req.addAttribute(attributeType, reqAttribute_value); //TODO check setAttribute
                //req_att.setName();  // TODO check QNAME
                resource.addRequirement(req);
            }

            return resource;
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

        try (SqlSession session = factory.openSession()) {
            session.insert("org.apache.ibatis.Mapper.insertResource", resource.getId());
            session.commit();
            session.close();
        }

        // get internnal_id - autogenerated
        SqlSession session = factory.openSession();
        int internal_id = session.selectOne("org.mybatis.spring.sample.mapper.UserMapper.getResourceID", resource.getId());
        session.close();

        // CAPABILITY
        for (Capability c : resource.getCapabilities()) {
            // save to capability
            tables.Capability tab_capability = new tables.Capability();
            tab_capability.setNamespace(c.getNamespace());
            tab_capability.setId(c.getId());
            tab_capability.setResource_id(internal_id);
            //capability_id autoincrement
            // resource.getId(); // not used because String is slow - internal_id is int

            session = factory.openSession();
            session.insert("org.apache.ibatis.Mapper.insertCapability", tab_capability);
            session.commit();
            session.close();

            // get cap_InternalID - autogenerated
            session = factory.openSession();
            int cap_InternalID = session.selectOne("org.mybatis.spring.sample.mapper.UserMapper.getCapabilityID", c.getId());
            session.close();

            // cap_attribute
            for (Attribute<?> a : c.getAttributes()) {
                // save to cap_attribute, where the hell is attribute name ?!?
                tables.Cap_attribute tab_capAttribute = new tables.Cap_attribute();
                tab_capAttribute.setType(a.getAttributeType().toString());
                tab_capAttribute.setValue(a.getValue().toString());
                tab_capAttribute.setOperator(a.getOperator().toString());
                tab_capAttribute.setName(a.getStringValue());
                tab_capAttribute.setCapability_id(cap_InternalID);

                session = factory.openSession();
                session.insert("org.apache.ibatis.Mapper.insertCap_attribute", tab_capAttribute);
                session.commit();
                session.close();
            }

            // cap_directive
            Map<String, String> d = c.getDirectives();
            Set<String> k = d.keySet();
            Collection<String> v = d.values();

            String[] names = k.toArray(new String[k.size()]);
            String[] values = v.toArray(new String[v.size()]);

            for (int i = 0; i < k.size(); i++) {
                Cap_directive tab_capDirective = new Cap_directive();
                tab_capDirective.setName(names[i]);
                tab_capDirective.setValue(values[i]);
                tab_capDirective.setCapability_id(cap_InternalID);

                session = factory.openSession();
                session.insert("org.apache.ibatis.Mapper.insertCap_directive", tab_capDirective);
                session.commit();
                session.close();
            }
        }

        // REQUIREMENT
        for (Requirement r : resource.getRequirements()) {
            r.getId();
            r.getNamespace();
            tables.Requirement tab_requirement = new tables.Requirement();
            tab_requirement.setId(r.getId());
            tab_requirement.setNamespace(r.getNamespace());
            tab_requirement.setResource_id(internal_id);

            session = factory.openSession();
            session.insert("org.apache.ibatis.Mapper.insertRequirement", tab_requirement);
            session.commit();
            session.close();

            // get cap_InternalID - autogenerated
            session = factory.openSession();
            int req_Internal_id = session.selectOne("org.mybatis.spring.sample.mapper.UserMapper.getRequirementID", r.getId());
            session.close();

            // req_attribute
            for (Attribute<?> a : r.getAttributes()) {
                // save to req_attribute, where the hell is attribute name ?!?
                Req_attribute tab_reqAttribute = new Req_attribute();
                tab_reqAttribute.setName(a.getStringValue());
                tab_reqAttribute.setType(a.getAttributeType().toString());
                tab_reqAttribute.setValue(a.getValue().toString());
                tab_reqAttribute.setOperator(a.getOperator().toString());
                tab_reqAttribute.setRequirement_id(req_Internal_id);

                session = factory.openSession();
                session.insert("org.apache.ibatis.Mapper.insertReq_attribute", tab_reqAttribute);
                session.commit();
                session.close();
            }

            // req_directive 
            Map<String, String> d = r.getDirectives();
            Set<String> k = d.keySet();
            Collection<String> v = d.values();

            String[] names = k.toArray(new String[k.size()]);
            String[] values = v.toArray(new String[v.size()]);

            for (int i = 0; i < k.size(); i++) {
                Req_directive tab_reqDirective = new Req_directive();
                tab_reqDirective.setName(names[i]);
                tab_reqDirective.setValue(values[i]);
                tab_reqDirective.setRequirement_id(req_Internal_id);

                session = factory.openSession();
                session.insert("org.apache.ibatis.Mapper.insertReq_directive", tab_reqDirective);
                session.commit();
                session.close();
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
