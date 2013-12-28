package cz.zcu.kiv.crce.rest.internal.convertor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import cz.zcu.kiv.crce.rest.internal.jaxb.Tattribute;
import cz.zcu.kiv.crce.rest.internal.jaxb.Tcapability;
import cz.zcu.kiv.crce.rest.internal.jaxb.Tresource;

/**
 *
 * Test {@link ConvertorToBeans}.
 * @author Jan Reznicek
 *
 */
public class ConvertorToBeansTest {

	private static final String TEST_ID_1 = "testid-1.0.0";
	private static final String EXPECTED_UNKNOWN_STATUS = "unknown";
	private static final String CRCE_ID_CAP = "crce.identity";
	private static final String CRCE_STATUS = "crce.status";






	public Tcapability getCapability(List<Tcapability> caps, String namespace) {
		for(Tcapability cap: caps) {
			if(namespace.equals(cap.getNamespace())) {
				return cap;
			}
		}

		return null;
	}

	public Tattribute getAttribute(List<Object> list, String attrName) {
		for(Object obj: list) {
			if(obj instanceof Tattribute) {
				Tattribute atr = (Tattribute) obj;

				if(attrName.equals(atr.getName())) {
					return atr;
				}

			}

		}

		return null;
	}

	/**
	 * Test {@link ConvertorToBeans#getResourceWithUnknownStatus(String)}.
	 */
	@Test
	public void testGetResourceWithUnknownStatus() {

		ConvertorToBeans conv = new ConvertorToBeans();

		Tresource res = conv.getResourceWithUnknownStatus(TEST_ID_1);

		assertTrue("Wrong id", TEST_ID_1.equals(res.getId()));

		Tcapability crceIdentityCap = getCapability(res.getCapability(), CRCE_ID_CAP);

		assertNotNull("Capabily " + CRCE_ID_CAP + " is missing.", crceIdentityCap);

		Tattribute crceStatusAtr = getAttribute(crceIdentityCap.getDirectiveOrAttributeOrCapability(), CRCE_STATUS);

		assertTrue("Wrong status (" + crceStatusAtr.getValue() + "), expected status is: " + EXPECTED_UNKNOWN_STATUS, EXPECTED_UNKNOWN_STATUS.equals(crceStatusAtr.getValue()));

	}

}


