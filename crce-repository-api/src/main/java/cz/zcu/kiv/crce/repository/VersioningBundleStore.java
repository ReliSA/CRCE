package cz.zcu.kiv.crce.repository;

import java.io.InputStream;
import java.util.List;
import org.apache.ace.obr.storage.BundleStore;

/**
 * This interface is not a direct part of Repository API, but defines methods
 * which where proposed to integrate ACE OBR with other tools.
 * 
 * It could be implemented by CRCE and it is included here for not to be forgotten.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface VersioningBundleStore extends BundleStore {

    List<String> listBundles();

    List<String> listVersions(String symbolicName);

    InputStream get(String symbolicName, String version);

    InputStream getLatest(String symbolicName);

    InputStream getLatest(String symbolicName, String versionRange);

    String put(InputStream resource);

    boolean remove(String symbolicName, String version);
}
