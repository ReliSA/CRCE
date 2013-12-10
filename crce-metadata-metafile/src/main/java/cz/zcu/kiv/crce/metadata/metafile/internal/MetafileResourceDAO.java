package cz.zcu.kiv.crce.metadata.metafile.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.dao.AbstractResourceDAO;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;

/**
 * This implementation of ResourceDAO reads/writes metadata from/to a file,
 * whose name (URI path) is created by resource's URI path and '.meta' extension.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class MetafileResourceDAO extends AbstractResourceDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(MetafileResourceDAO.class);
    
    public static final String METAFILE_EXTENSION = ".meta";
    
    private volatile ResourceCreator m_resourceCreator;
    private volatile DataModelHelperExt m_dataModelHelper;

    @Override
    public void save(Resource resource) throws IOException {
        URI metadataUri = getMetafileUri(resource.getUri());

        Writer writer;
        if ("file".equals(metadataUri.getScheme())) {
            writer = new FileWriter(new File(metadataUri));
        } else {
            // TODO maybe another way to write to HTTP is required (see remove())
            try {
                writer = new OutputStreamWriter(metadataUri.toURL().openConnection().getOutputStream());
            } catch (Exception e) {
                throw new IOException("Can not open output stream for URI: " + metadataUri, e);
            }
        }
        m_dataModelHelper.writeMetadata(resource, writer);
        
        try {
            writer.flush();
        } finally {
            writer.close();
        }
    }

    @Override
    public Resource moveResource(Resource resource, URI uri) {
        Resource out = m_resourceCreator.createResource(resource);
        out.setUri(uri);
        return out;
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        URI metadataUri = getMetafileUri(uri);

        InputStreamReader reader = null;
        try {
        try {
            reader = new InputStreamReader(metadataUri.toURL().openStream());
        } catch (MalformedURLException e) {
            throw new IOException("Malformed URL in URI: " + metadataUri.toString(), e);
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
            logger.error("Can not parse XML data (probably corrupted content): {}", e.getMessage());
            return m_resourceCreator.createResource();
        }
        } finally {
            if (reader !=  null) {
                reader.close();
            }
        }
    }

    @Override
    public void remove(Resource resource) throws IOException {
        URI metadataUri = getMetafileUri(resource.getUri());
        String scheme = metadataUri.getScheme();
        if ("file".equals(scheme)) {
                File resourceFile = new File(metadataUri);
                if (resourceFile.exists() && !resourceFile.delete()) {
                    logger.warn("Can not delete metadata file {}, it will be deleted later.", metadataUri);
                    resourceFile.deleteOnExit();
                }
        } else if ("http".equals(scheme)) {
                HttpURLConnection httpCon = (HttpURLConnection) metadataUri.toURL().openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("DELETE");
                httpCon.connect();
        } else {
                throw new UnsupportedOperationException("Removing metadata for scheme of given URI is not supported: " + scheme);
        }
    }
    
    private URI getMetafileUri(URI uri) {
        URI metadataUri = uri;

        if (!uri.toString().toLowerCase().endsWith(METAFILE_EXTENSION)) {
            try {
                metadataUri = new URI(uri.toString() + METAFILE_EXTENSION);
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Invalid URI syntax: " + uri.toString() + METAFILE_EXTENSION, ex);
            }
        }
        
        return metadataUri;
    }
}
