package cz.zcu.kiv.crce.metadata.metafile.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.stub.AbstractResourceDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author kalwi
 */
public class MetafileResourceDAO extends AbstractResourceDAO {

    public static final String METAFILE_EXTENSION = ".meta";
    
    private volatile ResourceCreator m_resourceCreator;
    
    private DataModelHelperExt m_dataModelHelper;

    public MetafileResourceDAO() {
        m_dataModelHelper = Activator.getHelper();
        
        System.out.println("metafile resource dao constructor");
    }

    @Override
    public void save(Resource resource) throws IOException {
        // TODO check for file protocol

        File file = new File(resource.getUri());

        if (!file.getName().toLowerCase().endsWith(METAFILE_EXTENSION)) {
            file = new File(file.getPath() + METAFILE_EXTENSION);
        }
        
        Writer writer;
        try {
            writer = new FileWriter(file);
        } catch (Exception e) {
            // TODO
            return;
        }
        m_dataModelHelper.writeMetadata(resource, writer);
        
        
        // TODO - vyjimky
        writer.flush();
        writer.close();
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        // TODO check for file protocol

        URI mfUri;
        if (uri.toString().toLowerCase().endsWith(METAFILE_EXTENSION)) {
            mfUri = uri;
        } else {
            mfUri = metafileUri(uri);
        }

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(mfUri.toURL().openStream());
        } catch (MalformedURLException e) {
            throw new IOException("Malformed URL in URI: " + mfUri.toString(), e);
        } catch (FileNotFoundException e) {
            Resource resource = m_resourceCreator.createResource();
            resource.setUri(uri);
            return resource;
        }

        try {
            return m_dataModelHelper.readMetadata(reader);
        } catch (IOException e) {
            throw new IOException("Can not read XML data", e);
        } catch (Exception e) {
            throw new IOException("Can not parse XML data", e);
        }
    }

    private URI metafileUri(URI uri) throws IllegalStateException {
        try {
            return new URI(uri.toString() + METAFILE_EXTENSION);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unexpected URI syntax: " + uri.toString() + METAFILE_EXTENSION, e);
        }
    }
}
