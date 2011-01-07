package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.CombinedResourceCreator;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.*;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public class MetadataTest {

    private ResourceImpl staticResource;
    private ResourceImpl metaResource;
    private ResourceCreator meta;
    
    @Before
    public void setUp() {
//        staticResource = new ResourceImpl();
//        metaResource = new ResourceImpl();
//        meta = new CombinedResourceCreator(null, staticResource, metaResource);
    }
    
    @After
    public void tearDown() {
        staticResource = null;
        metaResource = null;
    }
    
//    @Test
//    public void readOnlySymbolicName() {
//        staticResource.put(Resource.SYMBOLIC_NAME, "sname");
//        
//        assert !meta.getResource().setSymbolicName("test") : "Symbolic name should be read-only";
//        
//    }
//
//    @Test
//    public void readOnlyVersion() {
//        staticResource.put(Resource.VERSION, "1.0.0");
//        
//        assert meta.getResource().setVersion(new Version("2.0.0")) : "Version should be read-only";
//        
//    }
//    
//    @Test
//    public void cloneResource() {
//        ResourceImpl res = new ResourceImpl();
//        res.put(Resource.SYMBOLIC_NAME, "sname");
//        res.put(Resource.VERSION, "1.2.4");
//        
//        res.addCategory("cat");
//        
//        CapabilityImpl cap = new CapabilityImpl("cap");
//        cap.addProperty("name1", "value");
//        cap.addProperty("name2", Property.LONG, "6");
//        cap.addProperty("name3", Property.DOUBLE, "6.5");
//        res.addCapability(cap);
//        
//        
//        Resource clone = ((CombinedResourceCreator) meta).cloneResourceDeep(res);
//        
//        assert ((ResourceImpl) clone).equals(res);
//
//        boolean flag = false;
//        for (String cat : clone.getCategories()) {
//            if ("cat".equals(cat)) {
//                flag = true;
//            }
//        }
//        assert flag : "Error by copying categories";
//        
//        // TODO test capabilities and requirements
//        
//    }
}
