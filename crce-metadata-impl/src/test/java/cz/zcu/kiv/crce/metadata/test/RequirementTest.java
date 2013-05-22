package cz.zcu.kiv.crce.metadata.test;

import static org.junit.Assert.*;

import org.junit.*;

import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementTest {

    private Requirement r1;
    private Requirement r2;
    private Requirement r3;
    private ResourceFactory rc;

    @Before
    public void before() {
        rc = new ResourceFactoryImpl();
        r1 = new RequirementImpl("req");
        r2 = new RequirementImpl("req");
        r3 = new RequirementImpl("req3");
    }

    @Test
    public void equalsName() {
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertNotEquals(r2, r3);
    }

    @Test
    public void testEqualAttributes() {
        r1.setAttribute("atr1", String.class, "value", Operator.EQUAL);
        assert !r1.equals(r2);

        r2.setAttribute("atr1", String.class, "value", Operator.EQUAL);
        assert r1.equals(r2);
    }

    @Test
    public void testAttributesWithOperator() {
        r1.setAttribute("atr1", String.class, "value", Operator.GREATER);
        assertNotEquals(r1, r2);

        r2.setAttribute("atr1", String.class, "value", Operator.GREATER);
        assertEquals(r1, r2);

        r1.setAttribute("atr1", String.class, "value", Operator.LESS);
        r1.equals(r2);
        System.out.println("eq: " + r1.equals(r2));
        assertNotEquals(r1, r2);
    }

    @Test
    public void testDirectives() {
        r1.setDirective("optional", "true");

        assertNotEquals(r1, r2);

        r2.setDirective("optional", "true");
        assertEquals(r1, r2);

        r1.setDirective("multiple", "false");
        assertNotEquals(r1, r2);

        assertEquals("true", r1.getDirective("optional"));
        assertEquals("false", r1.getDirective("multiple"));

        assertNull(r1.getDirective("unknown"));
    }

//    @Test
//    public void equals() {
//        r1.setOptional(true);
//        r1.setMultiple(true);
//        r1.setExtend(true);
//        r1.setFilter("(&(a=1)(b=2))");
//        assert !r1.equals(r2);
//
//        r2.setOptional(true);
//        assert !r1.equals(r2);
//
//        r2.setMultiple(true);
//        assert !r1.equals(r2);
//
//        r2.setExtend(true);
//        assert !r1.equals(r2);
//
//        r2.setFilter("(&(a=1)(b=2))");
//        assert r1.equals(r2);
//    }
//
//    @Test
//    public void hashSet() throws Exception {
//        Requirement r1 = rc.createRequirement("a");
//        Requirement r2 = rc.createRequirement("a");
//
//        Set<Requirement> set = new HashSet<Requirement>();
//
//        set.add(r1);
//
//        assert set.contains(r1);
//        assert set.contains(r2);
//
//        r1.setFilter("(a=b)");
//        assert set.contains(r1);
//        assert !set.contains(r2);
//
//        r2.setFilter("(a=b)");
//        assert set.contains(r1);
//        assert set.contains(r2);
//
//    }
}
