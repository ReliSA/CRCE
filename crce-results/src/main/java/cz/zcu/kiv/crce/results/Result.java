package cz.zcu.kiv.crce.results;

import cz.zcu.kiv.crce.metadata.Requirement;
import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Result {

    Requirement[] getRequirements();

    URI getResult();
}
