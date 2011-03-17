package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CapabilityTest {
    
    private ResourceCreator rc;
    
    @Before
    public void before(){
        rc = new ResourceCreatorImpl();
    }
    
    @Test
    public void testChain() throws Exception {
        Capability c = rc.createCapability("a").setProperty("p1", "1").setProperty("p2", 2);
        
        assert "a".equals(c.getName());
        assert "1".equals(c.getProperty("p1").getConvertedValue());
        assert 2  == (Long) c.getProperty("p2").getConvertedValue();
    }

    @Test
    public void unsetProperty() {
        Capability c = rc.createCapability("a").setProperty("p1", "1").setProperty("p2", 2);
        
        assert c.getProperty("p1") != null;
        assert c.getProperty("p2") != null;
        
        c.unsetProperty("p1");
        
        assert c.getProperty("p1") == null;
        assert c.getProperty("p2") != null;
        
        c.unsetProperty("p2");
        
        assert c.getProperty("p1") == null;
        assert c.getProperty("p2") == null;
        

    }
}
