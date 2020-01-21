package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.restimpl.JavaTypeWrapper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaTypeWrapperTest {

    @Test
    public void testSameTypes() {
        JavaTypeWrapper prim1 = new JavaTypeWrapper("B");
        JavaTypeWrapper prim2 = new JavaTypeWrapper("B");

        JavaTypeWrapper box1 = new JavaTypeWrapper("java/lang/Byte");
        JavaTypeWrapper box2 = new JavaTypeWrapper("java/lang/Byte");

        JavaTypeWrapper other = new JavaTypeWrapper("other");


        assertTrue("Primitive types (B) should be same!", prim1.equals(prim2));
        assertTrue("Box types (java/lang/Byte) should be same!", box1.equals(box2));
        assertTrue("Primitive and box types (B, java/lang/Byte) should be same!", prim1.equals(box2));
        assertFalse("Primitive type and other type should not be same!", prim1.equals(other));
    }

    @Test
    public void testFitsInto() {
        JavaTypeWrapper t1 = new JavaTypeWrapper("B");
        JavaTypeWrapper t2 = new JavaTypeWrapper("B");

        assertTrue("Same types should fit into each other!", t1.fitsInto(t2));

        t2 = new JavaTypeWrapper("J");
        assertTrue("Byte should fit into long!", t1.fitsInto(t2));
        assertFalse("Long should not fit into byte!", t2.fitsInto(t1));
    }

    @Test
    public void testFitsIntoDouble() {
        JavaTypeWrapper doubleType = new JavaTypeWrapper("java/lang/Double");
        JavaTypeWrapper other = new JavaTypeWrapper("F");

        assertTrue("Float should fit into double!", other.fitsInto(doubleType));

        other = new JavaTypeWrapper("J");
        assertFalse("Long should not fit into souble!", other.fitsInto(doubleType));

        other = new JavaTypeWrapper("other");
        assertFalse("Other should not fit into double!", other.fitsInto(doubleType));
    }

    @Test
    public void testNumber() {
        JavaTypeWrapper numberWrapper = new JavaTypeWrapper("java/lang/Number");
        JavaTypeWrapper integerWrapper = new JavaTypeWrapper("java/lang/Integer");

        assertTrue("Integer should extend number!", numberWrapper.isExtendedBy(integerWrapper));
        assertFalse("Number should not extend integer!", integerWrapper.isExtendedBy(numberWrapper));
    }
}
