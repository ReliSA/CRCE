package cz.zcu.kiv.crce.resolver.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAOFilter;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = ResourceLoader.class)
public class ResourceLoaderImpl implements ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLoaderImpl.class);

    @ServiceDependency private volatile ResourceDAO resourceDAO;
    @ServiceDependency private volatile MetadataFactory metadataFactory; // NOPMD will be used

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> getResources(Repository repository, Requirement requirement) throws IOException {
        if (!singleNamespace(requirement, requirement.getNamespace())) {
            logger.warn("Filtering of store resources by multiple namespaces is not supported.");
        }

        List<Resource> resources = Collections.emptyList();
        try {
            ResourceDAOFilter filter = new ResourceDAOFilter(requirement.getNamespace());

            String operator = requirement.getDirective("operator");
            if (operator == null || operator.equals("and")) {
                filter.setOperator(ResourceDAOFilter.Operator.AND);
                filter.setAttributes(requirement.getAttributes());
            } else if (operator.equals("or")) {
                filter.setOperator(ResourceDAOFilter.Operator.OR);
                filter.setAttributes(requirement.getAttributes());
            } else if (operator.equals("not")) {
                List<Attribute<?>> attributes = new ArrayList<>(requirement.getAttributes().size());
                for (Attribute<?> attribute : requirement.getAttributes()) {
                    Attribute<Object> newAttribute = metadataFactory.createAttribute(
                            (AttributeType<Object>) attribute.getAttributeType(),
                            attribute.getValue(),
                            negateOperator(attribute.getOperator())
                    );
                    attributes.add(newAttribute);
                }
                filter.setAttributes(attributes);
            }

            resources = resourceDAO.loadResources(repository, filter);
        } catch (IOException e) {
            logger.error("Could not load resources for requirement ({})", requirement.getNamespace(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getResources(requirement={}) returns {}", requirement.getNamespace(), resources.size());
        }
        return resources;
    }

    private boolean singleNamespace(Requirement requirement, String namespace) {
        if (!requirement.getNamespace().equals(namespace)) {
            return false;
        }
        for (Requirement child : requirement.getChildren()) {
            if (!singleNamespace(child, namespace)) {
                return false;
            }
        }
        return true;
    }

    private Operator negateOperator(Operator operator) {
        switch (operator) {
            case APPROX:
                return Operator.NOT_EQUAL; // TODO this is not perfectly correct

            case EQUAL:
                return Operator.NOT_EQUAL;

            case GREATER:
                return Operator.LESS_EQUAL;

            case GREATER_EQUAL:
                return Operator.LESS;

            case LESS:
                return Operator.GREATER_EQUAL;

            case LESS_EQUAL:
                return Operator.GREATER;

            case NOT_EQUAL:
                return Operator.EQUAL;

            case PRESENT:
                throw new UnsupportedOperationException("Not possible to negate PRESENT operator yet");

            case SUBSET:
                throw new UnsupportedOperationException("Not possible to negate SUBSET operator yet");

            case SUPERSET:
                throw new UnsupportedOperationException("Not possible to negate SUPERSET operator yet");

            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
