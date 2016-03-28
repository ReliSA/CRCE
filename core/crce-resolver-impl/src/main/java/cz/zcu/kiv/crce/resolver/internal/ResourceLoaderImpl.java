package cz.zcu.kiv.crce.resolver.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.dao.filter.CapabilityFilter;
import cz.zcu.kiv.crce.metadata.dao.filter.Operator;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = ResourceLoader.class)
@ParametersAreNonnullByDefault
public class ResourceLoaderImpl implements ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLoaderImpl.class);

    @ServiceDependency private volatile MetadataDao metadataDao;
    @ServiceDependency private volatile MetadataFactory metadataFactory; // NOPMD will be used

    @Override
    @Nonnull
    public List<Resource> getResources(Repository repository, Requirement requirement) throws IOException {
        return getResources(repository, Collections.singleton(requirement));
    }

    @Nonnull
    @Override
    public List<Resource> getResources(Repository repository, Set<Requirement> requirements) throws IOException {
        List<Resource> resources = Collections.emptyList();
        try {
            ResourceFilter filter = buildFilter(requirements);
            resources = metadataDao.loadResources(repository, filter);
        } catch (IOException e) {
            logger.error("Could not load resources for requirements ({})", requirements.toString());
            logger.error("Stacktrace: ", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getResources(requirement={}) returns {}", requirements.toString(), resources.size());
        }
        return resources;
    }

    private ResourceFilter buildFilter(Set<Requirement> requirements) {
        ResourceFilter filter = new ResourceFilter();

        for (Requirement req : requirements) {
            CapabilityFilter cap = new CapabilityFilter(req.getNamespace());

            String operator = req.getDirective("operator");
            if (operator == null || operator.equals("and")) {
                cap.setOperator(Operator.AND);
            } else if (operator.equals("or")) {
                cap.setOperator(Operator.OR);
            }

            cap.addAttributes(req.getAttributes());
            filter.addCapabilityFilter(cap);
        }

        return filter;
    }
}
