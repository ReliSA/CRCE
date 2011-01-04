package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.DataModelHelperExtImpl;
import org.apache.felix.bundlerepository.Resource;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kalwi
 */
public class DataModelHelperExtTest {

    private DataModelHelperExt helper;

    private String RES_CONTENT =
            "<capability>"
                + "<p n='cname' v='cvalue'/>"
            + "</capability>"
            + "<require name='rname' filter='(rname=rvalue)' extend='false' multiple='false' optional='false'>"
                + "requirement"
            + "</require>";

    private String RES = "<resource symbolicname='sname' version='1.2.3'>"
                        + RES_CONTENT
                        + "</resource>";
    
    private String OBR_STD = "<obr>"
                    + RES_CONTENT
                    + "</obr>";

    private String OBR_EXT = "<obr>"
                    + RES
                    + "</obr>";
    
    @Before
    public void setUp() {
        helper = new DataModelHelperExtImpl();
    }

    @After
    public void tearDown() {
        helper = null;
    }
    
    /**
     * "Standard" OBR format (with capabilities and requirements directly inside <obr> element).
     * @throws Exception 
     */
    @Test
    public void parseObrStd() throws Exception {
        System.out.println(OBR_STD);
        Resource resource = helper.readMetadata(OBR_STD);
        
        assert resource.getSymbolicName() == null : "Expected sym. name: null, found: " + resource.getSymbolicName();
        assert "0.0.0".equals(resource.getVersion().toString()) : "Expected version: 0.0.0, found: " + resource.getVersion();
        
    }

    /**
     * Extended OBR format (with <resource> element inside <obr> element)
     * @throws Exception 
     */
    @Test
    public void parseObrExt() throws Exception {
        System.out.println(OBR_EXT);
        Resource resource = helper.readMetadata(OBR_EXT);
        
        assert "sname".equals(resource.getSymbolicName()) : "Expected sym. name: sname, found: " + resource.getSymbolicName();
        assert "1.2.3".equals(resource.getVersion().toString()) : "Expected sym. name: rname, found: " + resource.getVersion();
        
        
    }
    
}
