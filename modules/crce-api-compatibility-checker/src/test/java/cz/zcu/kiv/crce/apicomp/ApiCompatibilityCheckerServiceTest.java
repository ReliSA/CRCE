package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.ApiCompatibilityCheckerServiceImpl;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.impl.webservice.jsonwsp.JsonWspCompatibilityChecker;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApiCompatibilityCheckerServiceTest {

    @Test
    public void testPickCheck_JsonWsp() {
        ApiCompatibilityCheckerService service = new ApiCompatibilityCheckerServiceImpl();

        ApiCompatibilityChecker checker = service.pickChecker(createJsonWspAPIMetadata());
        assertNotNull("Null returned!", checker);
        assertEquals("Wrong checker returned!", JsonWspCompatibilityChecker.class, checker.getClass());
    }

    private Resource createJsonWspAPIMetadata() {
        String wsId = "jsonWsp";

        Capability identity = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__CRCE_IDENTITY, "identity");
        identity.setAttribute(NsCrceIdentity.ATTRIBUTE__CATEGORIES, Collections.singletonList(JsonWspCompatibilityChecker.CATEGORY));

        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, wsId);

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        r.addRootCapability(identity);
        return r;
    }
}
