package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.ResourceCreatorFactory;
import cz.zcu.kiv.crce.repository.Plugin;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;

/**
 *
 * @author kalwi
 */
public class ResourceBufferImpl implements ResourceBuffer {

    private int BUFFER_SIZE = 8 * 1024;
    private volatile ResourceCreatorFactory m_resourceCreatorFactory;   /* Injected by dependency manager */

    private volatile BundleContext m_context; /* Injected by dependency manager */

    File m_baseDir;
    private List<Resource> m_resources = new ArrayList<Resource>(); // TODO remove, make repository.xml instead

    private void setUpBaseDir() {
        m_baseDir = m_context.getDataFile("stack");
        if (!m_baseDir.exists()) {
            m_baseDir.mkdir();
        } else if (!m_baseDir.isDirectory()) {
            m_baseDir.delete();
            m_baseDir.mkdir();
        }

    }

    @Override
    public synchronized Resource put(String name, InputStream resource) throws IOException {
        if (name == null || resource == null || "".equals(name)) {
            return null;
        }
        if (m_baseDir == null) {
            setUpBaseDir();
        }
        FileOutputStream output = null;
        File file = null;
        Resource out = null;
        try {
            file = File.createTempFile("res", ".tmp", m_baseDir);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int count = resource.read(buffer); count != -1; count = resource.read(buffer)) {
                output.write(buffer, 0, count);
            }

            ResourceCreator creator = m_resourceCreatorFactory.getResourceCreator();

            Resource res = creator.getResource(file.toURI());

            res.createCapability("file").setProperty("name", name);

            res.setSymbolicName(name);

            creator.save(res);
            m_resources.add(res);

            out = res;
        } finally {
            if (output != null) {
                output.flush();
                output.close();
            }
        }

        return out;
    }

    @Override
    public synchronized void commit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void runTestsOnComponent(Object component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getStoredResources() {
        return m_resources.toArray(new Resource[0]);
    }

    @Override
    public void executeOnStored(Plugin[] plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
        // do nothing yet
    }
}
