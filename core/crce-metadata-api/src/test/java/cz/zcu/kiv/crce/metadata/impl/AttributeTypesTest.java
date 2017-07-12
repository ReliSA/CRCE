package cz.zcu.kiv.crce.metadata.impl;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

import cz.zcu.kiv.crce.metadata.AttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AttributeTypesTest {

    private static final AttributeType<Long> SIMPLE_LONG_A = new SimpleAttributeType<>("long.a", Long.class);
    private static final AttributeType<Long> SIMPLE_LONG_A_1 = new SimpleAttributeType<>("long.a", Long.class);
    private static final AttributeType<Double> SIMPLE_LONG_A_DBL = new SimpleAttributeType<>("long.a", Double.class);
    private static final AttributeType<Long> SIMPLE_LONG_B = new SimpleAttributeType<>("long.b", Long.class);

    private static final AttributeType<Object> GENERIC_LONG_A = new GenericAttributeType("long.a", "Long");
    private static final AttributeType<Object> GENERIC_LONG_A_1 = new GenericAttributeType("long.a", "Long");
    private static final AttributeType<Object> GENERIC_LONG_A_DBL = new GenericAttributeType("long.a", "Double");
    private static final AttributeType<Object> GENERIC_LONG_B = new GenericAttributeType("long.b", "Long");

    private static final AttributeType<List<String>> LIST_A = new ListAttributeType("list.a");
    private static final AttributeType<List<String>> LIST_A_1 = new ListAttributeType("list.a");
    private static final AttributeType<List<String>> LIST_LONG_A = new ListAttributeType("long.a");
    private static final AttributeType<List<String>> LIST_B = new ListAttributeType("list.b");

    @Test
    public void testSimpleAttributeTypesByName() {
        assertEquals(SIMPLE_LONG_A, SIMPLE_LONG_A_1);
        assertNotEquals(SIMPLE_LONG_A, SIMPLE_LONG_B);
    }

    @Test
    public void testSimpleAttributeTypesByType() {
        assertNotEquals(SIMPLE_LONG_A, SIMPLE_LONG_A_DBL);
    }

    @Test
    public void testGenericAttributeTypesByName() {
        assertEquals(GENERIC_LONG_A, GENERIC_LONG_A_1);
        assertNotEquals(GENERIC_LONG_A, GENERIC_LONG_B);
    }

    @Test
    public void testGenericAttributeTypesByType() {
        assertNotEquals(GENERIC_LONG_A, GENERIC_LONG_A_DBL);
    }

    @Test
    public void testListAttributeTypesByName() {
        assertEquals(LIST_A, LIST_A_1);
        assertNotEquals(LIST_A, LIST_B);
    }

    @Test
    public void testAttributeTypesTogether() {
        assertEquals(SIMPLE_LONG_A, GENERIC_LONG_A);
        assertNotEquals(SIMPLE_LONG_A, GENERIC_LONG_A_DBL);
        assertNotEquals(SIMPLE_LONG_A, LIST_LONG_A);
        assertNotEquals(GENERIC_LONG_A, LIST_LONG_A);
    }
}
