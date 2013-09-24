package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import java.util.HashSet;
import java.util.Set;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementTest {

    private Requirement r1;
    private Requirement r2;
    private Requirement r3;
    private ResourceCreator rc;
    
    @Before
    public void before(){
        rc = new ResourceCreatorImpl();
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
        assert !r1.equals(r2);
        
        r2.setMultiple(true);
        assert !r1.equals(r2);
        
        r2.setExtend(true);
        assert !r1.equals(r2);

        r2.setFilter("(&(a=1)(b=2))");
        assert r1.equals(r2);
    }
    
    @Test
    public void hashSet() throws Exception {
        Requirement r1 = rc.createRequirement("a");
        Requirement r2 = rc.createRequirement("a");
        
        Set<Requirement> set = new HashSet<Requirement>();
        
        set.add(r1);
        
        assert set.contains(r1);
        assert set.contains(r2);
        
        r1.setFilter("(a=b)");
        assert set.contains(r1);
        assert !set.contains(r2);

        r2.setFilter("(a=b)");
        assert set.contains(r1);
        assert set.contains(r2);
        
    }
    
}
