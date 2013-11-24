package cz.zcu.kiv.crce.metadata.dao.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbProperty;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbAttributeType;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbOperator;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class MetadataMapping {

    static DbResource mapResource2DbResource(Resource resource, MetadataService metadataService) {
        DbResource dbResource = new DbResource();

        URI uri = metadataService.getUri(resource);
//        Repository repository = resource.getRepository();

        dbResource.setId(resource.getId());
        dbResource.setUri(uri.toString());
//        dbResource.setRepository_uri(repository != null ? repository.getURI().toString() : "");

        return dbResource;
    }

    static DbCapability mapCapability2DbCapability(Capability capability, MetadataService metadataService) {
        DbCapability dbCapability = new DbCapability();

        dbCapability.setId(capability.getId());
        dbCapability.setNamespace(capability.getNamespace());

        return dbCapability;
    }

    static DbRequirement mapRequirement2DbRequirement(Requirement requirement, MetadataService metadataService) {
        DbRequirement dbRequirement = new DbRequirement();

        dbRequirement.setId(requirement.getId());
        dbRequirement.setNamespace(requirement.getNamespace());

        return dbRequirement;
    }

    static DbProperty mapProperty2DbProperty(Property<?> property, long propertyId, long parentId, MetadataService metadataService) {
        DbProperty dbProperty = new DbProperty();

        dbProperty.setPropertyId(propertyId);
        dbProperty.setParentId(parentId);
        dbProperty.setId(property.getId());
        dbProperty.setNamespace(property.getNamespace());

        return dbProperty;
    }

    static List<DbAttribute> mapAttributes2DbAttributes(List<Attribute<?>> attributes, long entityId) {
        return mapAttributes2DbAttributes(attributes, entityId, false);
    }

    static List<DbAttribute> mapAttributes2DbAttributes(List<Attribute<?>> attributes, long entityId, boolean multipleAttributes) {
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

    static void mapDbAttributes2Requirement(List<DbAttribute> dbAttributes, final Requirement requirement) {
        mapDbAttributes2Entity(dbAttributes, new Entity() {

            @Override
            public <T> void setAttribute(AttributeType<T> type, T value, Operator operator) {
                requirement.addAttribute(type, value, operator);
            }
        }, true);
    }

    static void mapDbAttributes2Capability(List<DbAttribute> dbAttributes, final Capability capability) {
        mapDbAttributes2Entity(dbAttributes, new Entity() {

            @Override
            public <T> void setAttribute(AttributeType<T> type, T value, Operator operator) {
                capability.setAttribute(type, value, operator);
            }
        }, false);
    }

    static void mapDbAttributes2Property(List<DbAttribute> dbAttributes, final Property<?> property) {
        mapDbAttributes2Entity(dbAttributes, new Entity() {

            @Override
            public <T> void setAttribute(AttributeType<T> type, T value, Operator operator) {
                property.setAttribute(type, value, operator);
            }
        }, false);
    }

    static void mapDbDirectives2Capability(List<DbDirective> dbDirectives, Capability capability) {
        for (DbDirective dbDirective : dbDirectives) {
            capability.setDirective(dbDirective.getName(), dbDirective.getValue());
        }
    }

    static void mapDbDirectives2Requirement(List<DbDirective> dbDirectives, Requirement requirement) {
        for (DbDirective dbDirective : dbDirectives) {
            requirement.setDirective(dbDirective.getName(), dbDirective.getValue());
        }
    }

    private interface Entity {

        <T> void setAttribute(@Nonnull AttributeType<T> type, @Nonnull T value, @Nonnull Operator operator);
    }

    private static void mapDbAttributes2Entity(List<DbAttribute> dbAttributes, Entity entity, boolean multipleAttributes) {

        // temporary fields for processing list attributes in a loop
        List<String> list = null;
        String listName = null;
        Short operator = null;
        Short attributeIndex = null;

        for (DbAttribute dbAttribute : dbAttributes) {
            DbAttributeType dbAttributeType = DbAttributeType.fromDbValue(dbAttribute.getType());

            if (DbAttributeType.LIST.equals(dbAttributeType)) {
                if (!dbAttribute.getName().equals(listName)
                        && (!multipleAttributes || !Objects.equals(dbAttribute.getAttributeIndex(), attributeIndex))) { // a new list attribute
                    // save previous list, if any
                    if (listName != null && list != null) {
                        entity.setAttribute(new ListAttributeType(listName), list, DbOperator.getOperatorValue(operator));
                    }
                    //
                    list = new ArrayList<>();
                    list.add(dbAttribute.getStringValue());
                    listName = dbAttribute.getName();
                    operator = dbAttribute.getOperator();
                    if (multipleAttributes) {
                        attributeIndex = dbAttribute.getAttributeIndex();
                    }
                } else { // another element of the current list attribute
                    assert list != null;

                    list.add(dbAttribute.getStringValue());
                }
            } else {
                // save previous list attribute, if any
                if (listName != null && list != null) {
                    entity.setAttribute(new ListAttributeType(listName), list, DbOperator.getOperatorValue(operator));
                    list = null;
                    listName = null;
                    if (multipleAttributes) {
                        attributeIndex = null;
                    }
                }

                switch (dbAttributeType) {
                    case DOUBLE:
                        entity.setAttribute(new SimpleAttributeType<>(dbAttribute.getName(), Double.class),
                                dbAttribute.getDoubleValue(), DbOperator.getOperatorValue(dbAttribute.getOperator()));
                        break;

                    case LONG:
                        entity.setAttribute(new SimpleAttributeType<>(dbAttribute.getName(), Long.class),
                                dbAttribute.getLongValue(), DbOperator.getOperatorValue(dbAttribute.getOperator()));
                        break;

                    case STRING:
                        entity.setAttribute(new SimpleAttributeType<>(dbAttribute.getName(), String.class),
                                dbAttribute.getStringValue(), DbOperator.getOperatorValue(dbAttribute.getOperator()));
                        break;

                    case URI:
                        try {
                            entity.setAttribute(new SimpleAttributeType<>(dbAttribute.getName(), URI.class),
                                new URI(dbAttribute.getStringValue()), DbOperator.getOperatorValue(dbAttribute.getOperator()));
                        } catch (URISyntaxException e) {
                            throw new IllegalStateException("Invalid URI for attribute " + dbAttribute.getName(), e);
                        }
                        break;

                    case VERSION:
                        entity.setAttribute(new SimpleAttributeType<>(dbAttribute.getName(), Version.class),
                                new Version(
                                    dbAttribute.getVersionMajorValue(),
                                    dbAttribute.getVersionMinorValue(),
                                    dbAttribute.getVersionMicroValue(),
                                    dbAttribute.getStringValue()),
                                DbOperator.getOperatorValue(dbAttribute.getOperator()));
                        break;

                    default:
                        throw new IllegalArgumentException("Unexpected attribute type: " + dbAttributeType);
                }

            }
        }
        // save unsaved list attribute, if any
        if (listName != null && list != null) {
            entity.setAttribute(new ListAttributeType(listName), list, DbOperator.getOperatorValue(operator));
        }
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
