package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.plugin.stub.AbstractResourceIndexer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class OsgiManifestBundleIndexer extends AbstractResourceIndexer {

    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */


    @Override
    public Resource index(InputStream input) {
        return index(input, null);
    }

    @Override
    public Resource index(InputStream input, Resource resource) {
        File tmpFile;
        OutputStream output;

        // XXX copying file is very ineffective, needs another implementation
        try {
            tmpFile = File.createTempFile("jar", "tmp");
            output = new FileOutputStream(tmpFile);

            byte[] buf = new byte[1024];
            int len;

            while ((len = input.read(buf)) > 0) {
                output.write(buf, 0, len);
            }
            input.close();
            output.close();

            return index(tmpFile.toURI().toURL(), resource);
        } catch (Exception ex) {
            // TODO could be logged
            return resource == null ? m_resourceCreator.createResource() : resource;
        }

    }

    @Override
    public Resource index(URL artifact) {
        return index(artifact, null);
    }

    @Override
    public Resource index(URL artifact, Resource resource) {
        Resource res = (resource == null ? m_resourceCreator.createResource() : resource);

        org.apache.felix.bundlerepository.Resource fres;
        try {
            fres = DataModelHelperExt.instance().createResource(artifact);
        } catch (IOException ex) {
            return res;
        }
        for (org.apache.felix.bundlerepository.Capability fcap : fres.getCapabilities()) {
            Capability cap = res.createCapability(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                cap.setProperty(fprop.getName(), fprop.getValue(), Type.getValue(fprop.getType()));
            }
        }
        for (org.apache.felix.bundlerepository.Requirement freq : fres.getRequirements()) {
            Requirement req = res.createRequirement(freq.getName());
            req.setComment(freq.getComment());
            req.setExtend(freq.isExtend());
            req.setFilter(freq.getFilter());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
        }

        for (String category : fres.getCategories()) {
            res.addCategory(category);
        }

        // TODO properties, if necessary
        res.addCategory("osgi");
        return res;
    }
}
