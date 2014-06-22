package cz.zcu.kiv.crce.repository.internal;

import java.io.File;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface MetadataIndexerCallback {

    void index(File file);
}
