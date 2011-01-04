package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.MetadataImpl;
import org.apache.felix.bundlerepository.Property;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.CapabilityImpl;
import org.apache.felix.bundlerepository.impl.ResourceImpl;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kalwi
 */
public class MetadataTest {

    private ResourceImpl staticResource;
    private ResourceImpl metaResource;
    private Metadata meta;
    
    @Before
    public void setUp() {
        staticResource = new ResourceImpl();
        metaResource = new ResourceImpl();
        meta = new MetadataImpl(null, staticResource, metaResource);
    }
    
    @After
    public void tearDown() {
        staticResource = null;
        metaResource = null;
    }
    
    @Test
    public void readOnlySymbolicName() {
        staticResource.put(Resource.SYMBOLIC_NAME, "sname");
        
        try {
            meta.setSymbolicName("test");
        } catch (ReadOnlyException ex) {
            return;
        }
        
        fail("Symbolic name should be read-only");
    }

    @Test
    public void readOnlyVersion() {
        staticResource.put(Resource.VERSION, "1.0.0");
        
        try {
            meta.setVersion("2.0.0");
        } catch (ReadOnlyException ex) {
            return;
        }
        
        fail("Version should be read-only");
    }
    
    @Test
    public void cloneResource() {
        ResourceImpl res = new ResourceImpl();
        res.put(Resource.SYMBOLIC_NAME, "sname");
        res.put(Resource.VERSION, "1.2.4");
        
        res.addCategory("cat");
        
        CapabilityImpl cap = new CapabilityImpl("cap");
        cap.addProperty("name1", "value");
        cap.addProperty("name2", Property.LONG, "6");
        cap.addProperty("name3", Property.DOUBLE, "6.5");
        res.addCapability(cap);
        
        
        Resource clone = ((MetadataImpl) meta).cloneResource(res);
        
        assert ((ResourceImpl) clone).equals(res);

        boolean flag = false;
        for (String cat : clone.getCategories()) {
            if ("cat".equals(cat)) {
                flag = true;
            }
        }
        assert flag : "Error by copying categories";
        
    }
}
