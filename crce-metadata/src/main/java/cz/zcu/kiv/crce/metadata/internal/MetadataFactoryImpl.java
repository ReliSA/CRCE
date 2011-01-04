package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Metadata;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.ResourceImpl;

/**
 *
 * @author kalwi
 */
public class MetadataFactoryImpl implements MetadataFactory {

    private DataModelHelperExt m_dataModelHelperExt;

    public MetadataFactoryImpl() {
        m_dataModelHelperExt = Activator.getHelper();
    }

    @Override
    public Metadata createMetadata(File obrFile) throws IOException {
//        if (!obrFile.exists()) {
//            if (!obrFile.createNewFile()) {
//                throw new IOException("Can not create file: " + obrFile.getAbsolutePath());
//            }
//        } else if (!obrFile.isFile() || !obrFile.canWrite()) {
//            throw new IOException("Can not open file for writing: " + obrFile.getAbsolutePath());
//        }

//        return new MetadataImpl(obrFile);
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Metadata createMetadataFor(File resourceFile) throws FileNotFoundException, IOException {
        if (!resourceFile.exists()) {
            throw new FileNotFoundException("File not found: " + resourceFile.getAbsolutePath());
        }

        File obrFile = new File(resourceFile.getParentFile(), resourceFile.getName() + Metadata.METAFILE_EXTENSION);

        
        ResourceImpl metaResource = null;   // resource from metafile
        try {
            metaResource = (ResourceImpl) m_dataModelHelperExt.readMetadata(new FileReader(obrFile));
        } catch (Exception e) {
            // just not a metafile or metafile does not exist, create resource manually
        }
        if (metaResource == null) {
            metaResource = new ResourceImpl();
        }

        
        Resource staticResource = null;     // resource from artifact
        try {
            staticResource = m_dataModelHelperExt.createResource(resourceFile.toURI().toURL());
        } catch (IOException e) {
            // TODO
        }

        if (staticResource == null) { /* not a bundle */
            staticResource = new ResourceImpl();
            if (metaResource.getSymbolicName() == null) {
                metaResource.put(Resource.SYMBOLIC_NAME, resourceFile.getName());
            }
        }

        assert staticResource.getSymbolicName() != null || metaResource.getSymbolicName() != null : "Static as well as meta sym. name is null";
        
        Metadata metadata = new MetadataImpl(obrFile, staticResource, metaResource);

        return metadata;

    }

}
