package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ConfigurationDependency;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.LifecycleController;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.internal.mapper.SequenceMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Jan Dyrczyk (dyrczyk@students.zcu.cz)
 * @author Pavel Cihlář
 */
@Component(provides={ResourceDAO.class})
public class ResourceDAOImpl extends AbstractResourceDAO {

    private static final Logger logger = LoggerFactory.getLogger(ResourceDAOImpl.class);

    private static final String METADATA_MAPPER = "cz.zcu.kiv.crce.metadata.dao.internal.mapper.MetadataMapper.";

    //private volatile MetadataService metadataService;
    Map<URI, Map<URI, Resource>> repositories = new HashMap<>();

    @ServiceDependency private volatile ResourceFactory resourceFactory;
    @ServiceDependency private volatile MetadataService metadataService;

    @LifecycleController
    Runnable lifeCycleController;

    @Override
    void factoryPostConfiguration(Configuration configuration) {
        configuration.addMapper(SequenceMapper.class);
    }

    @Init
    void init() {
        logger.info("Starting CRCE Metadata DAO.");

        try (SqlSession session = getSession()) {
            session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createTables");
            session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createSequences");
            session.commit();
            // starts the component only if initialization is successful
            lifeCycleController.run();
        } catch (Exception e) {
            logger.error("Could not initialize DB.", e);
        }
    }

    @Override
    @ConfigurationDependency(pid = Activator.PID)
    public synchronized void updated(Dictionary<String, ?> dict) throws ConfigurationException {
        super.updated(dict);
    }

    @Start
    void start() {
        logger.info("CRCE Metadata DAO started.");
    }

    @Stop
    synchronized void stop() {
        logger.info("CRCE Metadata DAO finished.");
    }

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice
        logger.debug("loadResource(uri={})", uri);

        Resource result = null;

        try (SqlSession session = getSession()) {

            if (uri != null) {

                //Resource resource = loadResource(uri);
                String resourceID = uri.toString();

                Resource resource = resourceFactory.createResource(resourceID);

                // Load capability
                //Capability cap = null;
                /*
                Capability cap = resourceFactory.createCapability(resourceID);

                List<DbCapability> capabilityList = session.selectList(METADATA_MAPPER + "getCapability", resourceID);
                Iterator<DbCapability> itr0 = capabilityList.iterator();
                while (itr0.hasNext()) {
                    DbCapability capability = itr0.next();
                    //String capInternal_id = selectedCapability.get(0);
                    //String cap_id = selectedCapability.get(1); //TODO unused
                    //String capabilityNamespace = selectedCapability.get(2); //TODO unused
                    String capability_id = capability.getId();
                    //TODO save cap_id and namespace

                    List<DbDirective> capabilityDirective = session.selectList(METADATA_MAPPER + "getCapabilityDirective", capability_id);
                    Iterator<DbDirective> itr = capabilityDirective.iterator();
                    while (itr.hasNext()) {
                        DbDirective cDirective = itr.next();
                        String capDirName = cDirective.getName();
                        String capDirValue = cDirective.getValue();
                        cap.setDirective(capDirName, capDirValue);
                    }
                    List<cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapabilityAttribute> capabilityAttribute = session.selectList(METADATA_MAPPER + "getCapabilityAttribute", capability_id);
                    Iterator<cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapabilityAttribute> itr2 = capabilityAttribute.iterator();
                    while (itr.hasNext()) {
                        cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapabilityAttribute cAttribute = itr2.next();
                        String capAttribute_type = cAttribute.getType();
                        String capAttribute_name = cAttribute.getName();
                        String capAttribute_value = cAttribute.getValue();
                        //String capAttribute_operator = selectedCapAttribute.get(4); // TODO unfinished
                        //cap_att.setName();  // TODO check QNAME
                        GenericAttributeType attributeType = new GenericAttributeType(capAttribute_name, capAttribute_type);
                        cap.setAttribute(attributeType, capAttribute_value);
                    }
                    resource.addCapability(cap);
                }
                */

                // Load requirements
                //Requirement req = null;
                /*
                Requirement req = resourceFactory.createRequirement(resourceID);
                List<DbRequirement> requirementList = session.selectList(METADATA_MAPPER + "getRequirement", resourceID);
                Iterator<DbRequirement> itr3 = requirementList.iterator();
                while (itr3.hasNext()) {
                    DbRequirement requirement = itr3.next();
                    //String req_id = selectedRequirement.get(1); //TODO unused
                    //String requirementNamespace = selectedRequirement.get(2); //TODO unused
                    String requirement_id = requirement.getId();
                    // TODO save namespace and req_id

                    List<Req_directive> regDirectiveList = session.selectList(METADATA_MAPPER + "getRequirementDirective", requirement_id);
                    Iterator<Req_directive> itr4 = regDirectiveList.iterator();
                    while (itr4.hasNext()) {
                        Req_directive regDirective = itr4.next();
                        String reqDirName = regDirective.getName();
                        String reqDirValue = regDirective.getValue();

                        req.setDirective(reqDirName, reqDirValue);
                    }
                    List<Req_attribute> requirementAttributeList = session.selectList(METADATA_MAPPER + "RequirementAttribute", requirement_id);
                    Iterator<Req_attribute> itr5 = requirementAttributeList.iterator();
                    while (itr5.hasNext()) {
                        Req_attribute requirementAttribute = itr5.next();
                        String reqAttribute_type = requirementAttribute.getType();
                        String reqAttribute_name = requirementAttribute.getName();
                        String reqAttribute_value = requirementAttribute.getValue();
                        //String reqAttribute_operator = selectedreqAttribute.get(4);  //not finished
                        GenericAttributeType attributeType = new GenericAttributeType(reqAttribute_name, reqAttribute_type);
                        req.addAttribute(attributeType, reqAttribute_value); //TODO check setAttribute
                        //req_att.setName();  // TODO check QNAME
                        resource.addRequirement(req);
                    }
                }
                */
                result = resource;
            }
        }

        logger.debug("loadResource(uri) returns {}", result);
        return result;
    }

    @Override
    public synchronized List<Resource> loadResources(Repository repository) throws IOException {
        logger.debug("loadResources(repository={})", repository);

        List<Resource> result;
        Map<URI, Resource> resources = repositories.get(repository.getURI());
        if (resources != null) {
            result = Collections.unmodifiableList(new ArrayList<>(resources.values()));
        } else {
            result = Collections.emptyList();
        }

        if (logger.isTraceEnabled()) {
            logger.trace("loadResources(repository={}) returns {}", repository, result);
        } else {
            logger.debug("loadResources(repository) returns {}", result.size());
        }
        return result;
    }

    @Override
    public synchronized void saveResource(Resource resource) throws IOException {
        logger.debug("saveResource(resource={})", resource);

        try (SqlSession session = getSession()) {
            SequenceMapper seqMapper = session.getMapper(SequenceMapper.class);

            DbResource dbResource = MetadataMapping.mapResource2DbResource(resource, metadataService);

            long resourceId = seqMapper.nextVal("resource_seq");
            dbResource.setResourceId(resourceId);

            session.insert(METADATA_MAPPER + "insertResource", dbResource);

            // capabilities
            for (Capability capability : resource.getRootCapabilities()) {
                saveCapabilityRecursive(capability, resourceId, null, session, seqMapper);
            }

            // requirements
            for (Requirement requirement : resource.getRequirements()) {
                saveRequirementRecursive(requirement, resourceId, null, session, seqMapper);
            }

            session.commit();
        }

        logger.debug("saveResource(resource) returns", resource);
    }

    private void saveCapabilityRecursive(Capability capability, long resourceId, Long parentId, SqlSession session, SequenceMapper seqMapper) {

        DbCapability dbCapability = MetadataMapping.mapCapability2DbCapability(capability, metadataService);
        dbCapability.setResourceId(resourceId);

        long capabilityId = seqMapper.nextVal("capability_seq");
        dbCapability.setCapabilityId(capabilityId);
        dbCapability.setParentCapabilityId(parentId != null ? parentId : capabilityId);

        session.insert(METADATA_MAPPER + "insertCapability", dbCapability);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(capability.getAttributes(), capabilityId);
        if (!dbAttributes.isEmpty()) {
            session.insert(METADATA_MAPPER + "insertCapabilityAttributes", dbAttributes);
        }

        List<DbDirective> dbDirectives = MetadataMapping.mapDirectives2DbDirectives(capability.getDirectives(), capabilityId);
        if (!dbDirectives.isEmpty()) {
            session.insert(METADATA_MAPPER + "insertCapabilityDirectives", dbDirectives);
        }

        for (Capability child : capability.getChildren()) {
            saveCapabilityRecursive(child, resourceId, capabilityId, session, seqMapper);
        }
    }

    private void saveRequirementRecursive(Requirement requirement, long resourceId, Long parentId, SqlSession session, SequenceMapper seqMapper) {

        DbRequirement dbRequirement = MetadataMapping.mapRequirement2DbRequirement(requirement, metadataService);
        dbRequirement.setResourceId(resourceId);

        long requirementId = seqMapper.nextVal("requirement_seq");
        dbRequirement.setRequirementId(requirementId);
        dbRequirement.setParentRequirementId(parentId != null ? parentId : requirementId);

        session.insert(METADATA_MAPPER + "insertRequirement", dbRequirement);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(requirement.getAttributes(), requirementId, true);
        if (!dbAttributes.isEmpty()) {
            session.insert(METADATA_MAPPER + "insertRequirementAttributes", dbAttributes);
        }

        List<DbDirective> dbDirectives = MetadataMapping.mapDirectives2DbDirectives(requirement.getDirectives(), requirementId);
        if (!dbDirectives.isEmpty()) {
            session.insert(METADATA_MAPPER + "insertRequirementDirectives", dbDirectives);
        }

        for (Requirement child : requirement.getChildren()) {
            saveRequirementRecursive(child, resourceId, requirementId, session, seqMapper);
        }
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {
        logger.debug("deleteResource(uri={})", uri);

        try (SqlSession session = getSession()) {
            Resource resource = loadResource(uri);

            if (resource != null) {
                String resourceID = resource.getId();
                session.insert(METADATA_MAPPER + "deleteResource", resourceID);
                session.commit();
            }
        }

        logger.debug("deleteResource(uri) returns");
    }

    @Override
    public synchronized boolean existsResource(URI uri) throws IOException {    // TODO just simple select, check if return null
        logger.debug("existsResource(uri={})", uri);

        boolean result = false;
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null && resources.containsKey(uri)) {
                result = true;
            }
        }

        logger.debug("existsResource(uri) returns {}", result);
        return result;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {  // not needed
        logger.debug("existsResource(uri={}, repository={})", uri, repository);

        Map<URI, Resource> resources = repositories.get(repository.getURI());
        boolean result = resources.containsKey(uri);

        logger.debug("existsResource(uri, repository) returns {}", result);
        return result;
    }

    @Override
    public String toString() {
        return "Persisted repositories and resources:\r\n" + repositories.toString();
    }
}
