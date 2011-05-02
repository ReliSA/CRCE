package cz.zcu.kiv.crce.results.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.results.Result;
import cz.zcu.kiv.crce.results.ResultsStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.Dictionary;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResultsStoreImpl implements ResultsStore, ManagedService {

    public static final String RESULT_EXTENSION = ".result";
    
    private volatile BundleContext m_context;
    private volatile DataModelHelperExt m_helper;
    
    private File m_baseDir;

    @Override
    public Result storeResult(Resource resource, URI resultsFile, Plugin provider) throws IOException {
        File dir = new File(m_baseDir,
                resource.getSymbolicName() + File.separator
                + resource.getVersion() + File.separator
                + provider.getPluginId() + provider.getPluginVersion());

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Directory for result can not be created: " + dir);
        }
        
        File file = File.createTempFile("result", "", dir);
        
        InputStream input = resultsFile.toURL().openStream();
        OutputStream output = new FileOutputStream(file);
        
        try {
            IOUtils.copyLarge(input, output);
        } finally {
            output.flush();
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
        }
        
        Result result = new ResultImpl(file, resource, provider);
        
        Writer writer = new FileWriter(getResultMetafile(file));
        
        try {
            m_helper.writeMetadata(resource, writer);
        } finally {
            try {
                writer.flush();
            } finally {
                writer.close();
            }
        }
        
        return result;
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

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        if (properties == null) {
            return;
        }
        
        String path = (String) properties.get(Activator.STORE_URI);
        
        m_baseDir = new File(path);
        
        if (!m_baseDir.exists() && !m_baseDir.mkdirs()) {
            throw new ConfigurationException(path, "Results store directory does not exists and can not be created: " + m_baseDir.getPath());
        }
        
    }
    
    private File getResultMetafile(File resultFile) throws IOException {
        return new File(resultFile.getCanonicalPath() + RESULT_EXTENSION);
    }

}
