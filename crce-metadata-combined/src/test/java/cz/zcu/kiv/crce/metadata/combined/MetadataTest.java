package cz.zcu.kiv.crce.metadata.combined;

import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.internal.FileIndexingResourceDAO;
import cz.zcu.kiv.crce.metadata.metafile.internal.MetafileResourceDAO;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.combined.internal.CombinedResourceDAO;
import java.io.File;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kalwi
 */
public class MetadataTest {

    private ResourceDAO creator;
    
    private CombinedResource resource;
    private Resource staticResource;
    private Resource writableResource;
    
    private File dir;
    
    @Before
    public void setUp() {
        dir = Util.createTempDir();
        File bundle = Util.prepareFile(dir, "bundle.jar");
        
//        creator = new CombinedResourceDAO(new FileIndexingResourceDAO(), new MetafileResourceDAO()); - WAS REMOVED
        try {
            resource = (CombinedResource) creator.getResource(bundle.toURI());
        } catch (IOException ex) {
            fail("Can not create resource: " + ex.getMessage());
        }
        
        
        staticResource = resource.getStaticResource();
        writableResource = resource.getWritableResource();
    }
    
    @After
    public void tearDown() {
        Util.deleteDir(dir);
        staticResource = null;
        writableResource = null;
    }
    
    @Test
    public void readOnlySymbolicName() throws Exception {
        staticResource.setSymbolicName("sname");
        
        assert !"sname".equals(resource.getSymbolicName()) : "Symbolic is not read-only";
        
    }

    @Test
    public void readOnlyVersion() throws Exception {
        staticResource.setVersion("1.2.3");
        
        assert !"1.2.3".equals(staticResource.getVersion().toString()) : "Version is not read-only";
        
    }
    
//    @Test
//    public void cloneResource() throws Exception {
//        
////        res.put(Resource.SYMBOLIC_NAME, "sname");
////        res.put(Resource.VERSION, "1.2.4");
//        
//        resource.addCategory("cat");
//        
//        
//        
//        Capability cap = resource.createCapability("cap");
//        
//        cap.setProperty("name1", "value");
//        cap.setProperty("name2", "6", Type.LONG);
//        cap.setProperty("name3", "6.5", Type.DOUBLE);
//        
//        
//        File dir2 = Util.createTempDir();
//        File file2 = new File(dir2, "bundle2.jar");
//        
//        creator.copy(resource, file2.toURI());
//        
//        Resource resource2 = creator.getResource(file2.toURI());
//        
//        assert resource2.equals(resource) : "Moved resource not equal to source";
//
////        boolean flag = false;
////        for (String cat : clone.getCategories()) {
////            if ("cat".equals(cat)) {
////                flag = true;
////            }
////        }
////        assert flag : "Error by copying categories";
//        
//        // TODO test capabilities and requirements
//        
//    }
//
}
