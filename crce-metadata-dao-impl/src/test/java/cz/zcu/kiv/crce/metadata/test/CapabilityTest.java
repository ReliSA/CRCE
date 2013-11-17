package cz.zcu.kiv.crce.metadata.test;

import java.util.List;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilityTest {

    private ResourceFactory factory;

    private final AttributeType<String> ATTR_P1 = new SimpleAttributeType<>("p1", String.class);
    private final AttributeType<Long> ATTR_P2 = new SimpleAttributeType<>("p2", Long.class);
    private final AttributeType<String> ATTR_P3 = new SimpleAttributeType<>("p3", String.class);

    @Before
    public void before(){
        factory = new ResourceFactoryImpl();
    }

    @Test
    public void testSetAttribute() throws Exception {
        Capability c = factory.createCapability("a");
        c.setAttribute(ATTR_P1, "1");
        c.setAttribute(ATTR_P2, 2L);

        assertEquals("a", c.getNamespace());
        assertEquals("1", c.getAttributeValue(ATTR_P1));
        assertEquals(2L, (long) c.getAttributeValue(ATTR_P2));
    }

    @Test
    public void testUnsetAttribute() throws Exception {
        Capability c = factory.createCapability("a");
        c.setAttribute(ATTR_P1, "1");
        c.setAttribute(ATTR_P2, 2L);

        assertNotNull(c.getAttribute(ATTR_P1));
        assertNotNull(c.getAttribute(ATTR_P2));

        c.removeAttribute(ATTR_P1);

        assertNull(c.getAttribute(ATTR_P1));
        assertNotNull(c.getAttribute(ATTR_P2));

        Attribute<Long> attribute = c.getAttribute(ATTR_P2);

        assertNotNull(attribute);

        c.removeAttribute(attribute);

        assertNull(c.getAttribute(ATTR_P1));
        assertNull(c.getAttribute(ATTR_P2));
    }

    @Test
    public void testUniqueAttributes() throws Exception {
        Capability c1 = factory.createCapability("a");

        c1.setAttribute(ATTR_P1, "a");
        assertEquals(1, c1.getAttributes().size());

        c1.setAttribute(ATTR_P1, "a");
        assertEquals(1, c1.getAttributes().size());

        c1.setAttribute(ATTR_P1, "b");
        assertEquals(1, c1.getAttributes().size());

        c1.setAttribute(ATTR_P3, "a");
        assertEquals(2, c1.getAttributes().size());
    }

    @Test
    public void equals() throws Exception {
        Capability c1 = factory.createCapability("a");
        Capability c2 = factory.createCapability("a");

        assertTrue(c1.equalsTo(c2, EqualityLevel.SHALLOW_NO_KEY));

        c1.setAttribute(ATTR_P1, "v1");
        assertFalse(c1.equalsTo(c2, EqualityLevel.SHALLOW_NO_KEY));


        c2.setAttribute(ATTR_P1, "v1");
        assertTrue(c1.equalsTo(c2, EqualityLevel.SHALLOW_NO_KEY));
    }


    @Test
    public void testEqualNamespaces() {
        Capability c1 = factory.createCapability("cap");
        Capability c2 = factory.createCapability("cap");
        Capability c3 = factory.createCapability("cap3");

        assertTrue(c1.equalsTo(c2, EqualityLevel.SHALLOW_NO_KEY));
        assertFalse(c1.equalsTo(c3, EqualityLevel.SHALLOW_NO_KEY));
        assertFalse(c2.equalsTo(c3, EqualityLevel.SHALLOW_NO_KEY));
    }

    @Test
    public void testHashSetContains() throws Exception {
        Capability c1 = factory.createCapability("a");
        Capability c2 = factory.createCapability("a");

        assertNotNull(c1);
        assertNotNull(c2);

        assertNotEquals(c1.hashCode(), c2.hashCode());

        c1.setAttribute(ATTR_P1, "p1");

        assertNotEquals(c1.hashCode(), c2.hashCode());

        c2.setAttribute(ATTR_P1, "p1");

        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void testHierarchy() {
        Capability root = factory.createCapability("a");

        Capability child1 = factory.createCapability("a.b");
        child1.setAttribute("key", String.class, "child1");

        Capability child2 = factory.createCapability("a.b");
        child2.setAttribute("key", String.class, "child2");

        Capability child3 = factory.createCapability("a.c");

        root.addChild(child1);
        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child2);
        root.addChild(child3);
        root.addChild(child3);

        // checking of presence of added child is commented because of possible performance decrease

        // with the check enabled, this would not be necessary
        child1.setParent(root);
        child2.setParent(root);
        child3.setParent(root);

        List<Capability> children = root.getChildren();
        // with the check enabled, expected value would be 3
        assertEquals(6, children.size());

        assertTrue(children.contains(child1));
        assertTrue(children.contains(child2));
        assertTrue(children.contains(child3));

        for (Capability child : children) {
            assertEquals(root, child.getParent());
        }
    }
}
