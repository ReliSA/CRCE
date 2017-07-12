package cz.zcu.kiv.crce.metadata.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.*;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.internal.AttributeImpl;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AttributeTest {

    private final AttributeType<String> ATTR_P1 = new SimpleAttributeType<>("p1", String.class);
    private final AttributeType<Long> ATTR_P1_LONG = new SimpleAttributeType<>("p1", Long.class);
    private final AttributeType<Double> ATTR_P1_DOUBLE = new SimpleAttributeType<>("p1", Double.class);
    private final AttributeType<String> ATTR_P2 = new SimpleAttributeType<>("p2", String.class);
    
    @Test
    public void equalsNames() throws Exception {
        assert (new AttributeImpl<>(ATTR_P1, "1")).equals(
                new AttributeImpl<>(ATTR_P1, "1"));
        
        assert !(new AttributeImpl<>(ATTR_P1, "1")).equals(
                 new AttributeImpl<>(ATTR_P2, "1"));
    }

    @Test
    public void equalsTypes() throws Exception {
        
        assert !(new AttributeImpl<>(ATTR_P1, "1")).equals(
                 new AttributeImpl<>(ATTR_P1_LONG, 1L));
        
        assert !(new AttributeImpl<>(ATTR_P1_LONG, 1L)).equals(
                 new AttributeImpl<>(ATTR_P1_DOUBLE, 1.));
        
        assert !(new AttributeImpl<>(ATTR_P1, "1")).equals(
                 new AttributeImpl<>(ATTR_P1_DOUBLE, 1.));
    }
    
    @Test
    public void equalsValues() throws Exception {
        assert !(new AttributeImpl<>(ATTR_P1, "1")).equals(
                 new AttributeImpl<>(ATTR_P1, "2"));
        
        assert (new AttributeImpl<>(ATTR_P1_LONG, 1L)).equals(
                new AttributeImpl<>(ATTR_P1_LONG, 1L));
        
        assert !(new AttributeImpl<>(ATTR_P1_LONG, 1L)).equals(
                 new AttributeImpl<>(ATTR_P1_LONG, 2L));
    }
    
    @Test
    public void hashSet() throws Exception {
        Set<Attribute> set1 = new HashSet<>();
        Set<Attribute> set2 = new HashSet<>();
        
        assert set1.equals(set2);
        
        set1.add(new AttributeImpl<>(ATTR_P1, "1"));
        assert !set1.equals(set2);

        set2.add(new AttributeImpl<>(ATTR_P1, "1"));
        assert set1.equals(set2);

        set2.remove(new AttributeImpl<>(ATTR_P1, "1"));
        assert !set1.equals(set2);

        set2.add(new AttributeImpl<>(ATTR_P1_LONG, 1L));
        assert !set1.equals(set2);
    }

    @Test
    public void hashMap() throws Exception {
        Map<AttributeType, Attribute> map1 = new HashMap<>();
        Map<AttributeType, Attribute> map2 = new HashMap<>();
        
        assert map1.equals(map2);
        
        map1.put(ATTR_P1, new AttributeImpl<>(ATTR_P1, "v1"));
        assert !map1.equals(map2);

        map2.put(ATTR_P1, new AttributeImpl<>(ATTR_P1, "v1"));
        assert map1.equals(map2);

        map1.put(ATTR_P1, new AttributeImpl<>(ATTR_P1, "v2"));
        assert !map1.equals(map2);
    }
}
