package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class PropertyTest {

    @Test
    public void equalsNames() throws Exception {
        assert (new PropertyImpl("a", Type.STRING, "1")).equals(
                new PropertyImpl("a", Type.STRING, "1"));
        
        assert !(new PropertyImpl("a", Type.STRING, "1")).equals(
                 new PropertyImpl("b", Type.STRING, "1"));
    }

    @Test
    public void equalsTypes() throws Exception {
        assert !(new PropertyImpl("a", Type.STRING, "1")).equals(
                 new PropertyImpl("a", Type.DOUBLE, "1"));
        
        assert !(new PropertyImpl("a", Type.LONG, "1")).equals(
                 new PropertyImpl("a", Type.DOUBLE, "1"));
        
        assert !(new PropertyImpl("a", Type.STRING, "1")).equals(
                 new PropertyImpl("a", Type.VERSION, "1"));
    }
    
    @Test
    public void equalsValues() throws Exception {
        assert !(new PropertyImpl("a", Type.STRING, "1")).equals(
                 new PropertyImpl("a", Type.STRING, "2"));
        
        assert (new PropertyImpl("a", Type.LONG, "1")).equals(
                new PropertyImpl("a", Type.LONG, "1"));
        
        assert !(new PropertyImpl("a", Type.LONG, "1")).equals(
                 new PropertyImpl("a", Type.LONG, "2"));
    }
    
    @Test
    public void hashSet() throws Exception {
        Set<Property> set1 = new HashSet<Property>();
        Set<Property> set2 = new HashSet<Property>();
        
        assert set1.equals(set2);
        
        set1.add(new PropertyImpl("p1", Type.STRING, "v1"));
        assert !set1.equals(set2);

        set2.add(new PropertyImpl("p1", Type.STRING, "v1"));
        assert set1.equals(set2);

        set2.remove(new PropertyImpl("p1", Type.STRING, "v1"));
        assert !set1.equals(set2);
    }

    @Test
    public void hashMap() throws Exception {
        Map<String, Property> map1 = new HashMap<String, Property>();
        Map<String, Property> map2 = new HashMap<String, Property>();
        
        assert map1.equals(map2);
        
        map1.put("p1", new PropertyImpl("p1", Type.STRING, "v1"));
        assert !map1.equals(map2);

        map2.put("p1", new PropertyImpl("p1", Type.STRING, "v1"));
        assert map1.equals(map2);

        map1.put("p1", new PropertyImpl("p1", Type.STRING, "v2"));
        assert !map1.equals(map2);
    }
    
    
}
