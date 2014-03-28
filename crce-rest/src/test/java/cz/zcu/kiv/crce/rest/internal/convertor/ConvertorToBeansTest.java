package cz.zcu.kiv.crce.rest.internal.convertor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.mapping.JaxbMapping;

/**
 * Test {@link JaxbMapping}.
 *
 * @author Jan Reznicek
 */
public class ConvertorToBeansTest {

    private static final String TEST_ID_1 = "testid-1.0.0";
    private static final String EXPECTED_UNKNOWN_STATUS = "unknown";
    private static final String CRCE_ID_CAP = "crce.identity";
    private static final String CRCE_STATUS = "status";

    public Capability getCapability(List<Capability> caps, String namespace) {
        for (Capability cap : caps) {
            if (namespace.equals(cap.getNamespace())) {
                return cap;
            }
        }

        return null;
    }

    public Attribute getAttribute(List<Attribute> list, String attrName) {
        for (Attribute atr : list) {

            if (attrName.equals(atr.getName())) {
                return atr;
            }
        }
        return null;
    }

    /**
     * Test {@link ConvertorToBeans#getResourceWithUnknownStatus(String)}.
     */
    @Test
    public void testGetResourceWithUnknownStatus() {

        JaxbMapping conv = new JaxbMapping();

        Resource res = conv.getResourceWithUnknownStatus(TEST_ID_1);
        assertTrue("Wrong id", TEST_ID_1.equals(res.getId()));

        Capability crceIdentityCap = getCapability(res.getCapabilities(), CRCE_ID_CAP);
        assertNotNull("Capabily " + CRCE_ID_CAP + " is missing.", crceIdentityCap);

        Attribute crceStatusAtr = getAttribute(crceIdentityCap.getAttributes(), CRCE_STATUS);
        assertTrue("Wrong status (" + crceStatusAtr.getValue() + "), expected status is: " + EXPECTED_UNKNOWN_STATUS, EXPECTED_UNKNOWN_STATUS.equals(crceStatusAtr.getValue()));
    }

}
