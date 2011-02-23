package cz.zcu.kiv.crce.metadata.combined;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.metafile.internal.DataModelHelperExtImpl;
import org.junit.*;

/**
 *
 * @author kalwi
 */
public class DataModelHelperExtTest {

    private DataModelHelperExtImpl m_helper;
    private String RES_CONTENT =
            "<capability name='cap'>"
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
        m_helper = new DataModelHelperExtImpl();
    }

    @After
    public void tearDown() {
        m_helper = null;
    }

    /**
     * "Standard" OBR format (with capabilities and requirements directly inside <obr> element).
     * @throws Exception 
     */
    @Test
    public void parseObrStd() throws Exception {
        Resource resource = m_helper.readMetadata(OBR_STD);

        assert resource.getSymbolicName() == null : "Expected sym. name: null, found: " + resource.getSymbolicName();
        assert "0.0.0".equals(resource.getVersion().toString()) : "Expected version: 0.0.0, found: " + resource.getVersion();
    }

    /**
     * Extended OBR format (with <resource> element inside <obr> element)
     * @throws Exception 
     */
    @Test
    public void parseObrExt() throws Exception {
        Resource resource = m_helper.readMetadata(OBR_EXT);

        assert "sname".equals(resource.getSymbolicName()) : "Expected sym. name: sname, found: " + resource.getSymbolicName();
        assert "1.2.3".equals(resource.getVersion().toString()) : "Expected sym. name: rname, found: " + resource.getVersion();
    }

//    @Test
//    public void createOtherResource() throws Exception {
//        File file = new File("src/test/resources/other.txt");
//        org.apache.felix.bundlerepository.Resource r = m_helper.createResource(file.toURI().toURL());
//        assert r == null : "other.txt is not a bundle";
//    }
//    
//    @Test
//    public void createBundleResource() throws Exception {
//        File file = new File("src/test/resources/bundle.jar");
//        org.apache.felix.bundlerepository.Resource r = m_helper.createResource(file.toURI().toURL());
//        assert r != null : "bundle.jar is a bundle";
//    }
}
