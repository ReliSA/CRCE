package cz.zcu.kiv.crce.resolver;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface ResourceLoader {

    @Nonnull
    List<Resource> getResources(Repository repository, Requirement requirement, boolean withDetails) throws IOException;

    /**
     *
     * @param repository repository to search
     * @param requirement set of requirements
     * @return resources that fullfil all the given requirements
     * @throws IOException
     */
    @Nonnull
    List<Resource> getResources(Repository repository, Set<Requirement> requirement, boolean withDetails) throws IOException;

    /**
     *
     * @param repository repository to search
     * @param requirement set of requirements
     * @param op AND or OR the requirement constraints
     * @return resources that fulfill some or all of the given requirements, depending on the operator value
     * @throws IOException
     */
    @Nonnull
    List<Resource> getResources(Repository repository, Set<Requirement> requirement, Operator op, boolean withDetails) throws IOException;
}
