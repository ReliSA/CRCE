package cz.zcu.kiv.crce.apicomp.webservice.common.xsd;

import cz.zcu.kiv.crce.apicomp.impl.webservice.common.xsd.XsdTypeComparator;
import cz.zcu.kiv.crce.compatibility.Difference;
import org.junit.Test;

import static org.junit.Assert.*;

public class XsdTypeComparatorTest {

    @Test
    public void testIsXsdType() {
        String type = XsdTypeComparator.XSD_PREFIX+"int";
        assertTrue("Type should be XSD type!", XsdTypeComparator.isXsdDataType(type));
    }

    @Test
    public void testCompareXsdTypes_same() {
        String type1 = XsdTypeComparator.XSD_PREFIX+"int";

        assertEquals("Wrong result for same types!", Difference.NON, XsdTypeComparator.compareTypes(type1, type1));
    }

    @Test
    public void testCompareXsdTypes_GEN() {
        String type1 = XsdTypeComparator.XSD_PREFIX+"int";
        String type2 = XsdTypeComparator.XSD_PREFIX+"long";

        assertEquals("Wrong result for generalized type!", Difference.GEN, XsdTypeComparator.compareTypes(type1, type2));
    }

    @Test
    public void testCompareNonXsdTypes() {
        String type1 = "type1";
        String type2 = "type2";

        assertNull("Wrong result for non-xsd types!", XsdTypeComparator.compareTypes(type1, type2));
    }
}
