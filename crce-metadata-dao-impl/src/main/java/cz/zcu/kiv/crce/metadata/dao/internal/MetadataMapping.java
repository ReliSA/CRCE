package cz.zcu.kiv.crce.metadata.dao.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbAttributeType;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbOperator;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class MetadataMapping {

    public static DbResource mapResource2DbResource(Resource resource, MetadataService metadataService) {
        DbResource dbResource = new DbResource();

        URI uri = metadataService.getUri(resource);
        Repository repository = resource.getRepository();

        dbResource.setId(resource.getId());
        dbResource.setUri(uri.toString());
        dbResource.setRepository_uri(repository != null ? repository.getURI().toString() : "");

        return dbResource;
    }

    public static DbCapability mapCapability2DbCapability(Capability capability, MetadataService metadataService) {
        DbCapability dbCapability = new DbCapability();

        dbCapability.setId(capability.getId());
        dbCapability.setNamespace(capability.getNamespace());

        return dbCapability;
    }

    public static DbRequirement mapRequirement2DbRequirement(Requirement requirement, MetadataService metadataService) {
        DbRequirement dbRequirement = new DbRequirement();

        dbRequirement.setId(requirement.getId());
        dbRequirement.setNamespace(requirement.getNamespace());

        return dbRequirement;
    }

    public static List<DbAttribute> mapAttributes2DbAttributes(List<Attribute<?>> attributes, long entityId) {
        return mapAttributes2DbAttributes(attributes, entityId, false);
    }

    public static List<DbAttribute> mapAttributes2DbAttributes(List<Attribute<?>> attributes, long entityId, boolean multipleAttributes) {
        List<DbAttribute> result = new ArrayList<>(attributes.size());

        Map<String, Short> attributeIndexes = new HashMap<>();

        for (Attribute<?> attribute : attributes) {
            DbAttributeType dbAttributeType = DbAttributeType.fromClass(attribute.getAttributeType().getType());

            String name = attribute.getAttributeType().getName();

            Short attributeIndex = null;
            if (multipleAttributes) {
                attributeIndex = attributeIndexes.get(name);
                if (attributeIndex == null) {
                    attributeIndex = 0;
                } else {
                    attributeIndex++;
                }
                attributeIndexes.put(name, attributeIndex);
            }

            if (DbAttributeType.LIST.equals(dbAttributeType)) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) attribute.getValue();

                short operator = DbOperator.getDbValue(attribute.getOperator());

                short i = 0;
                for (String entry : list) {
                    DbAttribute dbAttribute = new DbAttribute();

                    dbAttribute.setEntityId(entityId);
                    dbAttribute.setName(name);
                    dbAttribute.setOperator(operator);
                    dbAttribute.setType(dbAttributeType.getDbValue());
                    dbAttribute.setStringValue(entry);
                    dbAttribute.setListIndex(i++);
                    dbAttribute.setAttributeIndex(attributeIndex);

                    result.add(dbAttribute);
                }
            } else {
                DbAttribute dbAttribute = new DbAttribute();

                dbAttribute.setEntityId(entityId);
                dbAttribute.setName(name);
                dbAttribute.setOperator(DbOperator.getDbValue(attribute.getOperator()));
                dbAttribute.setType(dbAttributeType.getDbValue());
                dbAttribute.setListIndex((short) 0);
                dbAttribute.setAttributeIndex(attributeIndex);

                switch (dbAttributeType) {
                    case STRING:
                        dbAttribute.setStringValue((String) attribute.getValue());
                        break;

                    case LONG:
                        dbAttribute.setLongValue((Long) attribute.getValue());
                        break;

                    case DOUBLE:
                        dbAttribute.setDoubleValue((Double) attribute.getValue());
                        break;

                    case VERSION:
                        Version version = (Version) attribute.getValue();
                        dbAttribute.setVersionMajorValue(version.getMajor());
                        dbAttribute.setVersionMinorValue(version.getMinor());
                        dbAttribute.setVersionMicroValue(version.getMicro());
                        dbAttribute.setStringValue(version.getQualifier());
                        break;

                    case URI:
                        dbAttribute.setStringValue(attribute.getValue().toString());
                        break;

                    default:
                        throw new IllegalArgumentException("Unexpected attribute type: " + dbAttributeType);
                }

                result.add(dbAttribute);
            }
        }

        return result;
    }

    public static List<DbDirective> mapDirectives2DbDirectives(Map<String, String> directives, long entityId) {
        List<DbDirective> result = new ArrayList<>(directives.size());

        for (Map.Entry<String, String> entry : directives.entrySet()) {
            DbDirective dbDirective = new DbDirective();

            dbDirective.setEntityId(entityId);
            dbDirective.setName(entry.getKey());
            dbDirective.setValue(entry.getValue());

            result.add(dbDirective);
        }

        return result;
    }
}
