package cz.zcu.kiv.crce.results.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.results.Result;
import cz.zcu.kiv.crce.results.ResultsStore;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResultsStoreImpl implements ResultsStore {

    private volatile BundleContext m_context;
    private File m_baseDir;

    @Override
    public Result storeResult(Resource resource, URI resultsFile, Plugin provider) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result associateCapability(Result result, Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result associateRequirements(Result result, Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Result> getResults(Resource resource, Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Result> getResults(Resource resource, Plugin plugin, boolean allVersions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Result> getResults(Resource resource, Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Result> getResults(Resource resource, Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result getProvider(Resource resource, Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
