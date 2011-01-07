package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.junit.*;

/**
 *
 * @author kalwi
 */
public class PropertyTest {
    
    private Property prop;
    
    @Before
    public void setUp() {
        prop = new PropertyImpl("p");
    }
    
    @Test
    public void a() throws Exception {
        Set set = new HashSet();
        set.add("a");
        set.add("b");
        set.add("c");
        
        prop.setValue(set);
        
        StringTokenizer st = new StringTokenizer(prop.getValue(), ",");
    }
}
