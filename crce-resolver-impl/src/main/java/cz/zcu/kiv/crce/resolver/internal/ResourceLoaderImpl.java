package cz.zcu.kiv.crce.resolver.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
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
    public List<Resource> getResources(Repository repository, Requirement requirement) throws IOException {
        if (!singleNamespace(requirement, requirement.getNamespace())) {
            logger.warn("Filtering of store resources by multiple namespaces is not supported.");
        }

        List<Resource> resources = Collections.emptyList();
        try {
            ResourceDAOFilter filter = new ResourceDAOFilter(requirement.getNamespace());
            filter.setAttributes(requirement.getAttributes());

            String operator = requirement.getDirective("operator");
            if (operator == null || operator.equals("and")) {
                filter.setOperator(ResourceDAOFilter.Operator.AND);
            } else if (operator.equals("or")) {
                filter.setOperator(ResourceDAOFilter.Operator.OR);
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
}
