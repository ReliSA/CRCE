package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RequirementTest {

    Requirement r1;
    Requirement r2;
    Requirement r3;
    
    @Before
    public void init() {
        r1 = new RequirementImpl("req");
        r2 = new RequirementImpl("req");
        r3 = new RequirementImpl("req3");
    }
    
    @Test
    public void equalsName() {
        assert r1.equals(r2);
        assert !r1.equals(r3);
        assert !r2.equals(r3);
    }
    
    @Test
    public void equalsFilter() {
        r1.setFilter("(prop=value)");
        assert !r1.equals(r2);

        r2.setFilter("(prop=value)");
        assert r1.equals(r2);
    }
    
    @Test
    public void equalsExtend() {
        r1.setExtend(true);
        assert !r1.equals(r2);

        r2.setExtend(true);
        assert r1.equals(r2);
    }

    @Test
    public void equalsMultiple() {
        r1.setMultiple(true);
        assert !r1.equals(r2);

        r2.setMultiple(true);
        assert r1.equals(r2);
    }
    
    @Test
    public void equalsOptional() {
        r1.setOptional(true);
        assert !r1.equals(r2);

        r2.setOptional(true);
        assert r1.equals(r2);
    }

    @Test
    public void equals() {
        r1.setOptional(true);
        r1.setMultiple(true);
        r1.setExtend(true);
        r1.setFilter("(&(a=1)(b=2))");
        assert !r1.equals(r2);

        r2.setOptional(true);
        r2.setMultiple(true);
        r2.setExtend(true);
        r2.setFilter("(&(a=1)(b=2))");
        assert r1.equals(r2);
    }
}
