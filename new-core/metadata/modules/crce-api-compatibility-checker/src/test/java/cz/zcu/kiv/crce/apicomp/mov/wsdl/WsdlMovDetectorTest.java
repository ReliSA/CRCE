package cz.zcu.kiv.crce.apicomp.mov.wsdl;

import cz.zcu.kiv.crce.apicomp.impl.mov.common.ApiDescription;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.impl.mov.wsdl.WsdlMovDetector;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WsdlMovDetectorTest {

    @Test
    public void testDetectMov_same() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertFalse("Hosts should be the same!", diffs.hostDiff);
        assertFalse("Paths to endpoints should be the same!", diffs.pathDiff);
        assertFalse("Operations should be the same!", diffs.operationDiff);
    }

    @Test
    public void testDetectMov_shuffledOp() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1_shuffledOperations();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertFalse("Hosts should be the same!", diffs.hostDiff);
        assertFalse("Paths to endpoints should be the same!", diffs.pathDiff);
        assertFalse("Operations should be the same!", diffs.operationDiff);
    }

    @Test
    public void testDetectMov_differentHost() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1_diffHost();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertTrue("Hosts should not be the same!", diffs.hostDiff);
        assertFalse("Paths to endpoints should be the same!", diffs.pathDiff);
        assertFalse("Operations should be the same!", diffs.operationDiff);
    }

    @Test
    public void testDetectMov_differentEndpoints() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1_diffEndpoint();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertFalse("Hosts should be the same!", diffs.hostDiff);
        assertTrue("Paths to endpoints should not be the same!", diffs.pathDiff);
        assertFalse("Operations should be the same!", diffs.operationDiff);
    }

    @Test
    public void testDetectMov_differentOperations() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1_diffOperations();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertFalse("Hosts should be the same!", diffs.hostDiff);
        assertFalse("Paths to endpoints should be the same!", diffs.pathDiff);
        assertTrue("Operations should not be the same!", diffs.operationDiff);
    }

    @Test
    public void testDetectMov_differentHostAndEndpoints() {
        ApiDescription api1 = createApi1(),
                api2 = createApi1_diffHostAndEndpoints();

        WsdlMovDetector detector = new WsdlMovDetector(api1, api2);
        MovDetectionResult diffs = detector.detectMov();

        assertTrue("Hosts should not be the same!", diffs.hostDiff);
        assertTrue("Paths to endpoints should not be the same!", diffs.pathDiff);
        assertFalse("Operations should be the same!", diffs.operationDiff);
    }

    /**
     * host -> e1 -> o1-1
     *               o1-2
     *               o1-3
     *      -> e2 -> o2-1
     *               o2-2
     *               o2-3
     *               o2-4
     *
     * @return
     */
    private ApiDescription createApi1() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "operation1-1",
                "operation1-2",
                "operation1-3"
        );

        List<String> endpoint2operations = Arrays.asList(
                "operation2-1",
                "operation2-2",
                "operation2-3",
                "operation2-4"
        );

        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint1", endpoint1operations);
        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint2", endpoint2operations);

        return apiDescription;
    }

    /**
     * Same as API 1 but operation names are shuffled
     * @return
     */
    private ApiDescription createApi1_shuffledOperations() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "operation1-3",
                "operation1-1",
                "operation1-2"
        );

        List<String> endpoint2operations = Arrays.asList(
                "operation2-3",
                "operation2-2",
                "operation2-4",
                "operation2-1"
                );

        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint2", endpoint2operations);
        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint1", endpoint1operations);

        return apiDescription;
    }

    /**
     * Same as API 1 but host is different
     * @return
     */
    private ApiDescription createApi1_diffHost() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "operation1-1",
                "operation1-2",
                "operation1-3"
        );

        List<String> endpoint2operations = Arrays.asList(
                "operation2-1",
                "operation2-2",
                "operation2-3",
                "operation2-4"
        );

        apiDescription.addOperations("http://different-host-1.com", "/path/to/endpoint1", endpoint1operations);
        apiDescription.addOperations("http://different-host-1.com", "/path/to/endpoint2", endpoint2operations);

        return apiDescription;
    }

    /**
     * Same as API 1 but path to endpoints are different
     * @return
     */
    private ApiDescription createApi1_diffEndpoint() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "operation1-1",
                "operation1-2",
                "operation1-3"
        );

        List<String> endpoint2operations = Arrays.asList(
                "operation2-1",
                "operation2-2",
                "operation2-3",
                "operation2-4"
        );

        apiDescription.addOperations("http://host-1.com", "/this/way/to/endpoint1", endpoint1operations);
        apiDescription.addOperations("http://host-1.com", "/this/way/to/endpoint2", endpoint2operations);

        return apiDescription;
    }

    /**
     * Same as API 1 but operation names are different.
     * @return
     */
    private ApiDescription createApi1_diffOperations() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "function1-1",
                "function1-2",
                "function1-3"
        );

        List<String> endpoint2operations = Arrays.asList(
                "function2-1",
                "function2-2",
                "function2-3",
                "function2-4"
        );

        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint1", endpoint1operations);
        apiDescription.addOperations("http://host-1.com", "/path/to/endpoint2", endpoint2operations);

        return apiDescription;
    }

    /**
     * Api 1 but hostname and enpoint names are different
     * @return
     */
    private ApiDescription createApi1_diffHostAndEndpoints() {
        ApiDescription apiDescription = new ApiDescription();

        List<String> endpoint1operations = Arrays.asList(
                "operation1-1",
                "operation1-2",
                "operation1-3"
        );

        List<String> endpoint2operations = Arrays.asList(
                "operation2-1",
                "operation2-2",
                "operation2-3",
                "operation2-4"
        );

        apiDescription.addOperations("http://different-host-1.com", "/this/way/to/endpoint1", endpoint1operations);
        apiDescription.addOperations("http://different-host-1.com", "/this/way/to/endpoint2", endpoint2operations);

        return apiDescription;
    }
}
