package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Metadata;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.bundlerepository.Capability;
import org.apache.felix.bundlerepository.Property;
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
        Resource staticResource = null;     // resource from artifact
        
        try {
            metaResource = (ResourceImpl) m_dataModelHelperExt.readMetadata(new FileReader(obrFile));
        } catch (Exception e) {
            throw new IOException("Can not parse OBR metafile: " + obrFile.getAbsolutePath(), e);
        }

        try {
            staticResource = m_dataModelHelperExt.createResource(resourceFile.toURI().toURL());
        } catch (Exception e) {
            // just not a bundle, create resource manually
        }

        if (staticResource == null) { /* not a bundle */
            staticResource = new ResourceImpl();
            metaResource.put(Resource.SYMBOLIC_NAME, resourceFile.getName());
            metaResource.put(Resource.VERSION, "0.0.0", Property.VERSION);
        } else { /* bundle */

        }

        Metadata metadata = new MetadataImpl(obrFile, staticResource, metaResource);

        return metadata;

    }

//    private void mergeCapabilities(Resource src, ResourceImpl dest, boolean overwrite) {
//        Map<String, Capability> capabilities = new HashMap<String, Capability>();
//        
//        for (Capability cap : src.getCapabilities()) {
//            capabilities.put(cap.getName(), cap);
//        }
//        
//        for (Capability cap : dest.getCapabilities()) {
//            Capability s = capabilities.get(cap);
//            if (s == null) {
//                dest.addCapability(cap);
//            }
//        }
//        
//    }
}
