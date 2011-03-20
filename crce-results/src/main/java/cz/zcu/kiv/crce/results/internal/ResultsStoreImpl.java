package cz.zcu.kiv.crce.results.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.results.Result;
import cz.zcu.kiv.crce.results.ResultsStore;
import java.io.File;
import java.net.URI;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResultsStoreImpl implements ResultsStore {

    private volatile BundleContext m_context;
    private File m_baseDir;

    @Override
    public Result getResult(Resource resource, Capability capability) {
        /*
         * 
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result storeResult(Plugin provider, Resource resource, URI result, Capability... capability) {
        /*
         * 
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Capability[] getCapabilities(Result plugin, Resource resource) {
        /*
         * resource -> Capability[] {all}
         * Capability[] {all} ~ plugin.getCapabilityName(resource) -> Capability[] {provided by plugin}
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result getProvider(Resource resource, Capability capability) {
        /* 
         * capability -> name
         * pluginManager.get(Result, name) -> provider
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result storeResult(Plugin provider, Resource resource, URI result, Requirement... requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result getResult(Resource resource, Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result associateResult(Result result, Capability... capabilities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result associateResult(Result result, Requirement... requirements) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
