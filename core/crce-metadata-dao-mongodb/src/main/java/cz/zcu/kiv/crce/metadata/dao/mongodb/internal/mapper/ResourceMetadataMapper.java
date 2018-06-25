package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.mapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbProperty;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbRepository;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * This class provides methods for mapping of metadata entities to db representation and vice versa.
 *
 * Date: 22.9.16
 *
 * @author Jakub Danek
 */
public class ResourceMetadataMapper {

    private static final Logger logger = LoggerFactory.getLogger(ResourceMetadataMapper.class);

    /*
    ############################## DbResource to Resource ########################################
     */

    public static Resource map(DbResource src, MetadataFactory metadataFactory, MetadataService metadataService) {
        Resource dest = metadataFactory.createResource(src.getId());

        mapCapability(dest, src.getIdentity(), metadataFactory, metadataService);
        metadataService.setUri(dest, src.getUri());

        return dest;
    }

    public static Resource mapCaps(Resource dest, Collection<DbCapability> caps, MetadataFactory metadataFactory, MetadataService metadataService) {
        for (DbCapability cap : caps) {
            if(!Objects.equals(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY, cap.getNamespace())) {
                metadataService.addRootCapability(dest, mapCapability(dest, cap, metadataFactory, metadataService));
            }
        }

        return dest;
    }

    public static Resource mapReqs(Resource dest, Collection<DbRequirement> reqs, MetadataFactory metadataFactory) {
        for (DbRequirement req : reqs) {
            dest.addRequirement(mapRequirement(req, metadataFactory));
        }

        return dest;
    }

    public static Resource mapProps(Resource dest, Collection<DbProperty> props, MetadataFactory metadataFactory) {
        for (DbProperty req : props) {
            dest.addProperty(mapProperty(req, metadataFactory));
        }

        return dest;
    }

    private static Capability mapCapability(Resource res, DbCapability src, MetadataFactory metadataFactory, MetadataService metadataService) {
        Capability cap = metadataFactory.createCapability(src.getNamespace(), src.getId());
        if(Objects.equals(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY, src.getNamespace())) {
            metadataService.addRootCapability(res, cap);
        }

        for (DbAttribute dbAttribute : src.getAttributes()) {
            cap.setAttribute(mapAttribute(dbAttribute, metadataFactory));
        }

        for (DbDirective dbDirective : src.getDirectives()) {
            cap.setDirective(dbDirective.getName(), dbDirective.getValue());
        }

        for (DbProperty dbProperty : src.getProperties()) {
            cap.addProperty(mapProperty(dbProperty, metadataFactory));
        }

        for (DbRequirement dbRequirement : src.getRequirements()) {
            cap.addRequirement(mapRequirement(dbRequirement, metadataFactory));
        }

        for (DbCapability dbCapability : src.getChildren()) {
            cap.addChild(mapCapability(res, dbCapability, metadataFactory, metadataService));
        }

        return cap;
    }

    private static Requirement mapRequirement(DbRequirement src, MetadataFactory metadataFactory) {
        Requirement req = metadataFactory.createRequirement(src.getNamespace(), src.getId());

        for (DbAttribute dbAttribute : src.getAttributes()) {
            req.addAttribute(mapAttribute(dbAttribute, metadataFactory));
        }

        for (DbDirective dbDirective : src.getDirectives()) {
            req.setDirective(dbDirective.getName(), dbDirective.getValue());
        }

        for (DbRequirement dbRequirement : src.getChildren()) {
            req.addChild(mapRequirement(dbRequirement, metadataFactory));
        }

        return req;
    }

    private static Property mapProperty(DbProperty src, MetadataFactory metadataFactory) {
        Property prop = metadataFactory.createProperty(src.getNamespace(), src.getId());

        for (DbAttribute dbAttribute : src.getAttributes()) {
            prop.setAttribute(mapAttribute(dbAttribute, metadataFactory));
        }

        return prop;
    }

    private static <T> Attribute<T> mapAttribute(DbAttribute<T> src, MetadataFactory metadataFactory) {
        Attribute at = null;

        if(Objects.equals(Version.class.getName(), src.getType())) {
            Version v = new Version((String) src.getValue());
            at = metadataFactory.createAttribute(src.getName(), Version.class, v);
        } else if (Objects.equals(URI.class.getName(), src.getType())) {
            URI v = URI.create((String) src.getValue());
            at = metadataFactory.createAttribute(src.getName(), URI.class, v);
        }
        else {
            try {
                at = metadataFactory.createAttribute(src.getName(), (Class<T>) Class.forName(src.getType()), (T) src.getValue());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return at;
    }

    /*
    ############################## Resource to DbResource  ########################################
     */

    public static DbResource map(Resource src, MetadataService metadataService) {
        DbResource dest = new DbResource();
        dest.setId(src.getId());
        dest.setUri(metadataService.getUri(src).toString());
        Attribute<String> at = metadataService.getIdentity(src).getAttribute(new SimpleAttributeType<>("repository-id", String.class));
        if(at != null) {
            dest.setRepositoryUuid(at.getValue());
        }


        Set<DbCapability> caps = new HashSet<>();
        for (Capability capability : src.getRootCapabilities()) {
            if(Objects.equals(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY, capability.getNamespace())) {
                dest.setIdentity(map(capability, null));
            }
            caps.add(map(capability, src.getId()));
        }
        dest.setCapabilities(caps);

        Set<DbRequirement> reqs = new HashSet<>();
        for (Requirement requirement : src.getRequirements()) {
            reqs.add(map(requirement, src.getId()));
        }
        dest.setRequirements(reqs);

        Set<DbProperty> props = new HashSet<>();
        for (Property property : src.getProperties()) {
            props.add(map(property, src.getId()));
        }
        dest.setProperties(props);

        return dest;
    }

    private static DbCapability map(Capability src, String resourceId) {
        DbCapability dest = new DbCapability();

        dest.setId(src.getId());
        dest.setNamespace(src.getNamespace());
        dest.setResourceId(resourceId);

        Set<DbCapability> caps = new HashSet<>();
        for (Capability capability : src.getChildren()) {
            caps.add(map(capability, null));
        }
        dest.setChildren(caps);

        Set<DbRequirement> reqs = new HashSet<>();
        for (Requirement requirement : src.getRequirements()) {
            reqs.add(map(requirement, null));
        }
        dest.setRequirements(reqs);

        Set<DbProperty> props = new HashSet<>();
        for (Property property : src.getProperties()) {
            props.add(map(property, null));
        }
        dest.setProperties(props);

        Set<DbAttribute<?>> atts = new HashSet<>();
        for (Attribute<?> attribute : src.getAttributes()) {
            atts.add(map(attribute));
        }
        dest.setAttributes(atts);

        Set<DbDirective> dirs = new HashSet<>();
        for (Map.Entry<String, String> data : src.getDirectives().entrySet()) {
            dirs.add(map(data.getKey(), data.getValue()));
        }
        dest.setDirectives(dirs);


        return dest;
    }

    private static DbRequirement map(Requirement src, String resourceId) {
        DbRequirement dest = new DbRequirement();

        dest.setId(src.getId());
        dest.setNamespace(src.getNamespace());
        dest.setResourceId(resourceId);

        Set<DbRequirement> reqs = new HashSet<>();
        for (Requirement requirement : src.getChildren()) {
            reqs.add(map(requirement, null));
        }
        dest.setChildren(reqs);

        Set<DbAttribute> atts = new HashSet<>();
        for (Attribute<?> attribute : src.getAttributes()) {
            atts.add(map(attribute));
        }
        dest.setAttributes(atts);

        Set<DbDirective> dirs = new HashSet<>();
        for (Map.Entry<String, String> data : src.getDirectives().entrySet()) {
            dirs.add(map(data.getKey(), data.getValue()));
        }
        dest.setDirectives(dirs);

        return dest;
    }

    private static DbProperty map(Property src, String resourceId) {
        DbProperty dest = new DbProperty();

        dest.setId(src.getId());
        dest.setNamespace(src.getNamespace());
        dest.setResourceId(resourceId);

        Set<DbAttribute<?>> atts = new HashSet<>();
        for (Attribute<?> attribute : src.getAttributes()) {
            atts.add(map(attribute));
        }
        dest.setAttributes(atts);

        return dest;
    }

    private static <T> DbAttribute<T> map(Attribute<T> src) {
        DbAttribute<T> dest = new DbAttribute<>();

        dest.setName(src.getName());
        dest.setType(src.getType().getName());

        if(Objects.equals(Version.class, src.getType())) {
            dest.setValue(src.getStringValue());
        } else {
            dest.setValue(src.getValue());
        }



        return dest;
    }


    private static DbDirective map(String key, String value) {
        DbDirective directive = new DbDirective();
        directive.setName(key);
        directive.setValue(value);
        return directive;
    }


    /*
    ################################ DbRepository to Repository ###################################
     */

    public static Repository map(DbRepository src, MetadataFactory metadataFactory) {
        try {
            return metadataFactory.createRepository(new URI(src.getUri()), src.getId());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /*
     ################################ Repository to DbRepository ####################################
     */

    public static DbRepository map(Repository src) {
        DbRepository dest = new DbRepository();
        dest.setId(src.getId());
        dest.setUri(src.getUri().toString());
        return dest;
    }
}
