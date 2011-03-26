package cz.zcu.kiv.crce.repository;

import java.io.InputStream;
import java.util.List;
import org.apache.ace.obr.storage.BundleStore;

/**
 *
 * @author kalwi
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
