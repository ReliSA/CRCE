package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResourceTest {

    private Resource resource;
    
    @Before
    public void before() throws Exception {
        resource = new ResourceImpl();
    }
    
    @Test
    public void setSymbolicName() throws Exception {
        resource.setSymbolicName("r1");
        assert "r1".equals(resource.getSymbolicName());
        assert "r1/0.0.0".equals(resource.getId()) : "ID expected: r1/0.0.0, was: " + resource.getId();
    }

    @Test
    public void setVersion() throws Exception {
        resource.setVersion("1.2.3");
        assert "1.2.3".equals(resource.getVersion().toString()) : "version expected: 1.2.3, was: " + resource.getVersion();
        assert "null/1.2.3".equals(resource.getId()) : "ID expected: null/1.2.3, was: " + resource.getId();
    }
    
    @Test
    public void changeSymbolicName() throws Exception {
        resource.setSymbolicName("r1");
        assert "r1".equals(resource.getSymbolicName());
        assert "r1/0.0.0".equals(resource.getId()) : "ID expected: r1/0.0.0, was: " + resource.getId();
        
        resource.setSymbolicName("r2");
        assert "r2".equals(resource.getSymbolicName());
        assert "r2/0.0.0".equals(resource.getId()) : "ID expected: r2/0.0.0, was: " + resource.getId();

        resource.setSymbolicName("r3", true);
        assert "r3".equals(resource.getSymbolicName());
        assert "r3/0.0.0".equals(resource.getId()) : "ID expected: r3/0.0.0, was: " + resource.getId();
        
        resource.setSymbolicName("r4");
        assert "r3".equals(resource.getSymbolicName());
        assert "r3/0.0.0".equals(resource.getId()) : "ID expected: r3/0.0.0, was: " + resource.getId();
    }
    
    @Test
    public void changeVersion() throws Exception {
        resource.setVersion("1.2.3");
        assert "1.2.3".equals(resource.getVersion().toString());
        assert "null/1.2.3".equals(resource.getId()) : "ID expected: " + "null/1.2.3, was: " + resource.getId();
        
        resource.setVersion("4.5.6");
        assert "4.5.6".equals(resource.getVersion().toString());
        assert "null/4.5.6".equals(resource.getId()) : "ID expected: " + "null/4.5.6, was: " + resource.getId();
        
        resource.setVersion("7.8.9", true);
        assert "7.8.9".equals(resource.getVersion().toString());
        assert "null/7.8.9".equals(resource.getId()) : "ID expected: " + "null/7.8.9, was: " + resource.getId();

        resource.setVersion("10.11.12");
        assert "7.8.9".equals(resource.getVersion().toString());
        assert "null/7.8.9".equals(resource.getId()) : "ID expected: " + "null/7.8.9, was: " + resource.getId();
    }
    
    @Test
    public void writable() throws Exception {
        resource.setVersion("1.0.0");
        resource.setSymbolicName("r1");
        assert "r1/1.0.0".equals(resource.getId()) : "ID expected: " + "r1/1.0.0, was: " + resource.getId();
        
        resource.unsetWritable();
        
        resource.setVersion("2.0.0");
        resource.setSymbolicName("r2");
        assert "r1/1.0.0".equals(resource.getId()) : "ID expected: " + "r1/1.0.0, was: " + resource.getId();
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        Resource resource2 = new ResourceImpl();
        
        resource.setSymbolicName("r");
        resource2.setSymbolicName("r");
        resource.setVersion("1.0.0");
        resource2.setVersion("1.0.0");
        
        assert resource.equals(resource2);
        assert resource2.equals(resource);
        assert resource.hashCode() == resource2.hashCode();
        
        resource.setVersion("1.2.3");
        
        assert !resource.equals(resource2);
        assert !resource2.equals(resource);
        assert resource.hashCode() != resource2.hashCode();
    }
    
}
