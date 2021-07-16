package cz.zcu.kiv.crce.classmodel.processor;

import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Processor;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;

public class ProcessorTest {

        private static Map<String, Endpoint> springWebClientEndpoints;
        private static Map<String, Endpoint> springWebClientExpectedEndpoints;

        private static Map<String, Endpoint> springResttemplateEndpoints;
        private static Map<String, Endpoint> springResttemplateExpectedEndpoints;

        private static Map<String, Endpoint> jaxRsEndpoints;
        private static Map<String, Endpoint> jaxRsExpectedEndpoints;

        private static ClassLoader classLoader;

        public static void initWebClient() throws JsonParseException, JsonMappingException,
                        JsonProcessingException, IOException {
                Endpoint endpoint1 = new Endpoint("/123", null, Set.of(HttpMethod.GET),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false,
                                                ParameterCategory.QUERY)),
                                Set.of());
                endpoint1.addExpectedResponse(
                                new EndpointBody("com/baeldung/reactive/model/Employee", false));
                Endpoint endpoint2 = new Endpoint("/prvni/uri/trida", null, Set.of(HttpMethod.PUT),
                                Set.of(), Set.of());
                endpoint2.addExpectedResponse(new EndpointBody("java/lang/String", false));
                Endpoint endpoint3 = new Endpoint("/employee/{id}/prvni/uri/tridaNONSTATICtest",
                                null, Set.of(HttpMethod.PUT, HttpMethod.DELETE),
                                Set.of(new EndpointParameter(null,
                                                "com/baeldung/reactive/model/Employee", false,
                                                ParameterCategory.BODY),
                                                new EndpointParameter(null, "java/lang/Integer",
                                                                false, ParameterCategory.QUERY)),
                                Set.of());
                endpoint3.addExpectedResponse(new EndpointBody("java/lang/String", false));
                endpoint3.addExpectedResponse(
                                new EndpointBody("com/baeldung/reactive/model/Employee", false));

                Endpoint endpoint4 = new Endpoint("/bla/uri/s/argumentem/{id}", null,
                                Set.of(HttpMethod.PUT),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false,
                                                ParameterCategory.QUERY)),
                                Set.of());
                endpoint4.addExpectedResponse(new EndpointBody("java/lang/String", false));

                Endpoint endpoint5 = new Endpoint("/prvni/uri/tridaNONSTATICtest", null,
                                Set.of(HttpMethod.PUT), Set.of(), Set.of());
                endpoint5.addExpectedResponse(new EndpointBody("java/lang/String", false));


                Endpoint endpoint7 = new Endpoint("/employee", null, Set.of(HttpMethod.POST),
                                Set.of(new EndpointParameter(null,
                                                "com/baeldung/reactive/model/Employee", false,
                                                ParameterCategory.BODY)),
                                Set.of());
                endpoint7.addExpectedResponse(
                                new EndpointBody("com/baeldung/reactive/model/Employee", false));

                Endpoint endpoint8 = new Endpoint("/nejaka/uri/s/argumentem/{id}", null,
                                Set.of(HttpMethod.POST),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false,
                                                ParameterCategory.QUERY)),
                                Set.of());
                endpoint8.addExpectedResponse(new EndpointBody("java/lang/String", false));


                Endpoint endpoint9 = new Endpoint("test", null, Set.of(HttpMethod.DELETE),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false,
                                                ParameterCategory.QUERY)),
                                Set.of());
                endpoint9.addExpectedResponse(new EndpointBody("java/lang/String", false));

                Set<Header> endpoint10Headers = new HashSet<>();
                endpoint10Headers.add(new Header("Content-Type", "application/json"));
                Endpoint endpoint10 = new Endpoint("/accept", null, Set.of(HttpMethod.PUT),
                                Set.of(), endpoint10Headers);
                endpoint10.addExpectedResponse(new EndpointBody("java/lang/String", false));


                springWebClientExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1,
                                endpoint2.getUrl(), endpoint2, endpoint3.getUrl(), endpoint3,
                                endpoint4.getUrl(), endpoint4, endpoint5.getUrl(), endpoint5,
                                endpoint9.getUrl(), endpoint9, endpoint7.getUrl(), endpoint7,
                                endpoint8.getUrl(), endpoint8, endpoint10.getUrl(), endpoint10

                );
        }

        public static void initResttemplate() {

                final String baseURL = "http://localhost:8090";
                Set<Header> endpoint1Headers = new HashSet<>();
                endpoint1Headers.add(new Header("Content-Type", "application/json"));
                endpoint1Headers.add(new Header("Accept", "application/json"));
                Endpoint endpoint1 = new Endpoint(baseURL + "/api/user/users", null,
                                Set.of(HttpMethod.GET), Set.of(), endpoint1Headers);
                endpoint1.addExpectedResponse(new EndpointBody("com/app/demo/model/User", true));

                Endpoint endpoint2 = new Endpoint(baseURL + "/api/user/addUser", null,
                                Set.of(HttpMethod.POST),
                                Set.of(new EndpointParameter(null, "com/app/demo/model/User", false,
                                                ParameterCategory.BODY)),
                                Set.of());
                endpoint2.addExpectedResponse(new EndpointBody("com/app/demo/model/User", false));

                Endpoint endpoint3 = new Endpoint(baseURL + "/api/user/patchUser/", null,
                                Set.of(HttpMethod.PATCH),
                                Set.of(new EndpointParameter(null, "com/app/demo/model/User", false,
                                                ParameterCategory.BODY)),
                                Set.of());
                endpoint3.addExpectedResponse(new EndpointBody("com/app/demo/model/User", false));

                Endpoint endpoint4 = new Endpoint(baseURL, "/api/user/deleteUser/",
                                Set.of(HttpMethod.DELETE), Set.of(), Set.of());

                springResttemplateExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1,
                                endpoint2.getUrl(), endpoint2, endpoint3.getUrl(), endpoint3,
                                endpoint4.getUrl(), endpoint4

                );
        }

        public static void initJaxRs() {
                final String baseUrl = "http://localhost:8080/hrmanagerapi/webapi";
                Set<Header> test = new HashSet<>();
                test.add(new Header("Content-Type", "application/json"));
                test.add(new Header("Accept", "application/json"));
                Endpoint endpoint1 = new Endpoint(baseUrl, "/emp", Set.of(HttpMethod.GET), Set.of(),
                                test);
                endpoint1.addExpectedResponse(
                                new EndpointBody("com/nagarro/hrmanager/model/Employee", true));
                Endpoint endpoint2 = new Endpoint(baseUrl, "/emp/addemp", Set.of(HttpMethod.POST),
                                Set.of(new EndpointParameter(null,
                                                "com/nagarro/hrmanager/model/Employee", false,
                                                ParameterCategory.BODY)),
                                Set.of(new Header("Content-Type", "application/json")));
                Endpoint endpoint3 = new Endpoint(baseUrl, "/emp/update", Set.of(HttpMethod.PUT),
                                Set.of(new EndpointParameter(null,
                                                "com/nagarro/hrmanager/model/Employee", false,
                                                ParameterCategory.BODY)),
                                Set.of(new Header("Content-Type", "application/json")));
                Endpoint endpoint4 = new Endpoint(baseUrl, "/emp/delete/", Set.of(HttpMethod.GET),
                                Set.of(), Set.of());
                endpoint4.addExpectedResponse(new EndpointBody("javax/ws/rs/core/Response", false));
                Endpoint endpoint5 = new Endpoint(baseUrl, "/emp/getemp/", Set.of(HttpMethod.GET),
                                Set.of(), Set.of());
                endpoint5.addExpectedResponse(new EndpointBody("javax/ws/rs/core/Response", false));
                Endpoint endpoint6 = new Endpoint(baseUrl, "/user/getUser/", Set.of(HttpMethod.GET),
                                Set.of(), Set.of());
                endpoint6.addExpectedResponse(new EndpointBody("javax/ws/rs/core/Response", false));

                jaxRsExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1, endpoint2.getUrl(),
                                endpoint2, endpoint3.getUrl(), endpoint3, endpoint4.getUrl(),
                                endpoint4, endpoint5.getUrl(), endpoint5, endpoint6.getUrl(),
                                endpoint6);
        }

        @BeforeClass
        public static void init() throws JsonParseException, JsonMappingException,
                        JsonProcessingException, IOException {
                classLoader = ProcessorTest.class.getClassLoader();
                initWebClient();
                initResttemplate();
                initJaxRs();
        }

        @Test
        @Parameters(name = "Testing the compatiblity with Spring framework (WebClient class)")
        public void testWebClientSpring() {
                File file = new File(classLoader.getResource("spring_webclient_v2.jar").getFile());
                try {
                        springWebClientEndpoints = Processor.process(file);
                } catch (IOException e) {
                        fail(e.getMessage());
                }
                for (Endpoint endpoint : springWebClientExpectedEndpoints.values()) {
                        if (!springWebClientEndpoints.containsKey(endpoint.getUrl())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = springWebClientEndpoints.get(endpoint.getUrl());
                        if (!found.equals(endpoint)) {

                                fail("Expected " + endpoint + " but got " + found);
                        }
                }
        }

        @Test
        @Parameters(name = "Testing the compatiblity with Spring framework (RestTemplate class)")
        public void testResttemplateSpring() {
                File file = new File(classLoader.getResource("spring_resttemplate.jar").getFile());
                try {
                        springResttemplateEndpoints = Processor.process(file);
                } catch (IOException e) {
                        fail(e.getMessage());
                }
                for (Endpoint endpoint : springResttemplateExpectedEndpoints.values()) {
                        if (!springResttemplateEndpoints.containsKey(endpoint.getUrl())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = springResttemplateEndpoints.get(endpoint.getUrl());
                        if (!found.equals(endpoint)) {
                                fail("Expected " + endpoint + " but got " + found);
                        }
                }
        }

        @Test
        @Parameters(name = "Testing the compatiblity with JAX-RS framework")
        public void testJaxRs() {
                File file = new File(classLoader.getResource("jaxrs.jar").getFile());
                try {
                        jaxRsEndpoints = Processor.process(file);
                } catch (IOException e) {
                        fail(e.getMessage());
                }
                for (Endpoint endpoint : jaxRsExpectedEndpoints.values()) {
                        if (!jaxRsEndpoints.containsKey(endpoint.getUrl())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = jaxRsEndpoints.get(endpoint.getUrl());
                        if (!found.equals(endpoint)) {
                                fail("Expected " + endpoint + " but got " + found);
                        }
                }
        }
}
