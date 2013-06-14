package cz.zcu.kiv.crce.metadata.dao.internal;

import cz.zcu.kiv.crce.metadata.dao.internal.tables.Cap_directive;
import cz.zcu.kiv.crce.metadata.dao.internal.tables.Req_attribute;
import cz.zcu.kiv.crce.metadata.dao.internal.tables.Req_directive;
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
import java.util.Collection;
import java.util.Set;
import org.apache.ibatis.session.SqlSession;

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
            
            SqlSession session = SQLSessionHandler.getSession();
            
            //Resource resource = loadResource(uri);
            String resourceID = uri.toString();

            Resource resource = resourceFactory.createResource(resourceID);

            // Load capability
            //Capability cap = null; 
            Capability cap = resourceFactory.createCapability(resourceID);

            cz.zcu.kiv.crce.metadata.dao.internal.tables.Capability capability = session.selectOne("org.apache.ibatis.Mapper.getCapability", resourceID);
            //String capInternal_id = selectedCapability.get(0);
            //String cap_id = selectedCapability.get(1); //TODO unused
            //String capabilityNamespace = selectedCapability.get(2); //TODO unused
            String capability_id = capability.getId();
            //TODO save cap_id and namespace

            Cap_directive capabilityDirective = session.selectOne("org.apache.ibatis.Mapper.getCapabilityDirective", capability_id);
            String capDirName = capabilityDirective.getName();
            String capDirValue = capabilityDirective.getValue();
            cap.setDirective(capDirName, capDirValue);
                       
            cz.zcu.kiv.crce.metadata.dao.internal.tables.Cap_attribute capabilityAttribute = session.selectOne("org.apache.ibatis.Mapper.getCapabilityAttribute", capability_id);
            String capAttribute_type = capabilityAttribute.getType();
            String capAttribute_name = capabilityAttribute.getName();
            String capAttribute_value = capabilityAttribute.getValue();
            //String capAttribute_operator = selectedCapAttribute.get(4); // TODO unfinished
            //cap_att.setName();  // TODO check QNAME
            GenericAttributeType attributeType = new GenericAttributeType(capAttribute_name, capAttribute_type);
            cap.setAttribute(attributeType, capAttribute_value);

            resource.addCapability(cap);    
            
            
            // Load requirements
            //Requirement req = null;
            Requirement req = resourceFactory.createRequirement(resourceID);
            cz.zcu.kiv.crce.metadata.dao.internal.tables.Requirement requirement = session.selectOne("org.apache.ibatis.Mapper.getRequirement", resourceID);
            //String req_id = selectedRequirement.get(1); //TODO unused
            //String requirementNamespace = selectedRequirement.get(2); //TODO unused
            String requirement_id = requirement.getId();
            // TODO save namespace and req_id

            Req_directive regDirective = session.selectOne("org.apache.ibatis.Mapper.getRequirementDirective", requirement_id);
            String reqDirName = regDirective.getName();
            String reqDirValue = regDirective.getValue();

            req.setDirective(reqDirName, reqDirValue);
            
            Req_attribute requirementAttribute = session.selectOne("org.apache.ibatis.Mapper.RequirementAttribute", requirement_id);
            String reqAttribute_type = requirementAttribute.getType();
            String reqAttribute_name = requirementAttribute.getName();
            String reqAttribute_value = requirementAttribute.getValue();
            //String reqAttribute_operator = selectedreqAttribute.get(4);  //not finished          
            attributeType = new GenericAttributeType(reqAttribute_name, reqAttribute_type);
            req.addAttribute(attributeType, reqAttribute_value); //TODO check setAttribute
            //req_att.setName();  // TODO check QNAME
            resource.addRequirement(req);
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

        SqlSession session = SQLSessionHandler.getSession();
        
        session.insert("org.apache.ibatis.Mapper.insertResource", resource.getId());
        session.commit();

        
        // get internnal_id - autogenerated
        int internal_id = session.selectOne("org.apache.ibatis.Mapper.getResourceID", resource.getId());

        // CAPABILITY
        for (Capability c : resource.getCapabilities()) {
            // save to capability
            cz.zcu.kiv.crce.metadata.dao.internal.tables.Capability tab_capability = new cz.zcu.kiv.crce.metadata.dao.internal.tables.Capability();
            tab_capability.setNamespace(c.getNamespace());
            tab_capability.setId(c.getId());
            tab_capability.setResource_id(internal_id);
            //capability_id autoincrement
            // resource.getId(); // not used because String is slow - internal_id is int

            session.insert("org.apache.ibatis.Mapper.insertCapability", tab_capability);
            session.commit();

            // get cap_InternalID - autogenerated
            int cap_InternalID = session.selectOne("org.apache.ibatis.Mapper.getCapabilityID", c.getId());

            // cap_attribute
            for (Attribute<?> a : c.getAttributes()) {
                // save to cap_attribute, where the hell is attribute name ?!?
                cz.zcu.kiv.crce.metadata.dao.internal.tables.Cap_attribute tab_capAttribute = new cz.zcu.kiv.crce.metadata.dao.internal.tables.Cap_attribute();
                tab_capAttribute.setType(a.getAttributeType().toString());
                tab_capAttribute.setValue(a.getValue().toString());
                tab_capAttribute.setOperator(a.getOperator().toString());
                tab_capAttribute.setName(a.getStringValue());
                tab_capAttribute.setCapability_id(cap_InternalID);

                session.insert("org.apache.ibatis.Mapper.insertCap_attribute", tab_capAttribute);
                session.commit();
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

                session.insert("org.apache.ibatis.Mapper.insertCap_directive", tab_capDirective);
                session.commit();
            }
        }

        // REQUIREMENT
        for (Requirement r : resource.getRequirements()) {
            r.getId();
            r.getNamespace();
            cz.zcu.kiv.crce.metadata.dao.internal.tables.Requirement tab_requirement = new cz.zcu.kiv.crce.metadata.dao.internal.tables.Requirement();
            tab_requirement.setId(r.getId());
            tab_requirement.setNamespace(r.getNamespace());
            tab_requirement.setResource_id(internal_id);

            session.insert("org.apache.ibatis.Mapper.insertRequirement", tab_requirement);
            session.commit();

            // get cap_InternalID - autogenerated
            int req_Internal_id = session.selectOne("org.apache.ibatis.Mapper.getRequirementID", r.getId());

            // req_attribute
            for (Attribute<?> a : r.getAttributes()) {
                // save to req_attribute, where the hell is attribute name ?!?
                Req_attribute tab_reqAttribute = new Req_attribute();
                tab_reqAttribute.setName(a.getStringValue());
                tab_reqAttribute.setType(a.getAttributeType().toString());
                tab_reqAttribute.setValue(a.getValue().toString());
                tab_reqAttribute.setOperator(a.getOperator().toString());
                tab_reqAttribute.setRequirement_id(req_Internal_id);

                session.insert("org.apache.ibatis.Mapper.insertReq_attribute", tab_reqAttribute);
                session.commit();
                
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

                session.insert("org.apache.ibatis.Mapper.insertReq_directive", tab_reqDirective);
                session.commit();
            }
        }
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {

        SqlSession session = SQLSessionHandler.getSession();
        
        Resource resource = loadResource(uri);

        if (resource != null) {
            String resourceID = resource.getId();
            session.insert("org.apache.ibatis.Mapper.deleteResource", resourceID);
            session.commit();
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
