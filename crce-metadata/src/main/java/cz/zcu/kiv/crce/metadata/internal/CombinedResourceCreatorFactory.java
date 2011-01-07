package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.ResourceCreatorFactory;

/**
 *
 * @author kalwi
 */
public class CombinedResourceCreatorFactory implements ResourceCreatorFactory {

    private DataModelHelperExt m_dataModelHelperExt;

    private ResourceCreator m_staticCreator = new StaticResourceCreator();
    private ResourceCreator m_writableCreator = new MetafileResourceCreator();
    
    public CombinedResourceCreatorFactory() {
        m_dataModelHelperExt = Activator.getHelper();
    }

    @Override
    public ResourceCreator getResourceCreator() {
        
        ResourceCreator combinedCreator = new CombinedResourceCreator(m_staticCreator, m_writableCreator);
        
        return combinedCreator;
        
    }

        
//        if (!resourceFile.exists()) {
//            throw new FileNotFoundException("File not found: " + resourceFile.getAbsolutePath());
//        }
//
//        File obrFile = new File(resourceFile.getParentFile(), resourceFile.getName() + MetafileResourceCreator.METAFILE_EXTENSION);
//
//        
//        ResourceImpl metaResource = null;   // resource from metafile
//        try {
//            metaResource = (ResourceImpl) m_dataModelHelperExt.readMetadata(new FileReader(obrFile));
//        } catch (Exception e) {
//            // just not a metafile or metafile does not exist, create resource manually
//        }
//        if (metaResource == null) {
//            metaResource = new ResourceImpl();
//        }
//
//        
//        Resource staticResource = null;     // resource from artifact
//        try {
//            staticResource = m_dataModelHelperExt.createResource(resourceFile.toURI().toURL());
//        } catch (IOException e) {
//            // TODO
//        }
//
//        if (staticResource == null) { /* not a bundle */
//            staticResource = new ResourceImpl();
//            if (metaResource.getSymbolicName() == null) {
//                metaResource.put(Resource.SYMBOLIC_NAME, resourceFile.getName());
//            }
//        }
//
//        assert staticResource.getSymbolicName() != null || metaResource.getSymbolicName() != null : "Static as well as meta sym. name is null";
//        
//        ResourceCreator metadata = new CombinedResourceCreator(obrFile, staticResource, metaResource);
//
//        return metadata;

}
