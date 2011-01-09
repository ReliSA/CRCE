package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.internal.wrapper.ConvertedResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class StaticResourceCreator implements ResourceCreator {

    private static int BUFFER_SIZE = 8 * 1024;
    DataModelHelperExt m_dataModelHelper;

    public StaticResourceCreator() {
        m_dataModelHelper = Activator.getHelper();
    }

    @Override
    public void save(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        InputStream input = resource.getUri().toURL().openStream();
        OutputStream output = uri.toURL().openConnection().getOutputStream();
        
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int count = input.read(buffer); count != -1; count = input.read(buffer)) {
                output.write(buffer, 0, count);
            }
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                // nothing
            }
            input.close();
        }

    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        org.apache.felix.bundlerepository.Resource resource = m_dataModelHelper.createResource(uri.toURL());
        return resource == null ? null : new ConvertedResource(resource);
    }
}
