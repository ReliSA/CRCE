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
    List<Resource> getResources(Repository repository, Requirement requirement) throws IOException;

    @Nonnull
    List<Resource> getResources(Repository repository, Set<Requirement> requirement) throws IOException;
}
