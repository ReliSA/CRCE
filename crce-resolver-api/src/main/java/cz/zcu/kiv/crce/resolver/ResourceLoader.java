package cz.zcu.kiv.crce.resolver;

import java.io.IOException;
import java.util.List;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResourceLoader {

    List<Resource> getResources(Repository repository, Requirement requirement) throws IOException;
}
