package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class RestApiCompatibilityCheckerTest {

    @Test
    public void testIsApiSupported() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        Capability supportedCapability = new CapabilityImpl("restimpl.identity", "");
        Capability notSupportedCapability = new CapabilityImpl("not.supported", "");

        assertFalse("Empty set should not be supported!",checker.isApiSupported(Collections.emptySet()));
        assertFalse("Null set should not be supported!",checker.isApiSupported(null));
        assertFalse("Compatibility set with wrong namespace should not be supported", checker.isApiSupported(Collections.singleton(notSupportedCapability)));
        assertTrue("Compatibility set with wrong namespace should be supported", checker.isApiSupported(Collections.singleton(supportedCapability)));
    }

    @Test(expected = RuntimeException.class)
    public void testCompareApis_api1notSupported() {
        Set<Capability> api1 = new HashSet<>();
        api1.add(new CapabilityImpl("namespace", "id"));
        api1.add(new CapabilityImpl("namespace2", "id2"));

        Set<Capability> api2 = new HashSet<>();
        api1.add(new CapabilityImpl("namespace", "id"));
        api1.add(new CapabilityImpl("namespace2", "id2"));

        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        checker.compareApis(api1, api2);
        fail("Not supported exception expceted!");
    }

    @Test
    public void testCompareApis() {
        // todo:
    }
}
