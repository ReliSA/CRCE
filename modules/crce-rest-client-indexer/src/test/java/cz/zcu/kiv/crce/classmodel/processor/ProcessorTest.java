package cz.zcu.kiv.crce.classmodel.processor;

import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Processor;

public class ProcessorTest {

        private static Map<String, Endpoint> springWebClientEndpoints;
        private static Map<String, Endpoint> springWebClientExpectedEndpoints;

        private static Map<String, Endpoint> springResttemplateEndpoints;
        private static Map<String, Endpoint> springResttemplateExpectedEndpoints;

        private static Map<String, Endpoint> jaxRsEndpoints;
        private static Map<String, Endpoint> jaxRsExpectedEndpoints;

        private static ClassLoader classLoader;

        public static void initWebClient() {
                final Endpoint endpoint1 = new Endpoint("/123", Set.of(HttpMethod.GET),
                                new HashSet<>(),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointParameter(null, "java/lang/Integer", false,
                                                null)),
                                new HashSet<>(), new HashSet<>());
                Endpoint endpoint2 = new Endpoint("/prvni/uri/trida", Set.of(HttpMethod.PUT),
                                new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                Endpoint endpoint3 = new Endpoint("/employee/{id}/prvni/uri/tridaNONSTATICtest",
                                Set.of(HttpMethod.PUT, HttpMethod.DELETE),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointRequestBody("java/lang/String", false),
                                                new EndpointRequestBody(
                                                                "com/baeldung/reactive/model/Employee",
                                                                false)),
                                new HashSet<>(Set.of(new EndpointParameter(null,
                                                "java/lang/Integer", false, null))),
                                new HashSet<>(), new HashSet<>());
                final Endpoint endpoint4 = new Endpoint("/bla/uri/s/argumentem/{id}",
                                Set.of(HttpMethod.PUT), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>(Set.of(new EndpointParameter(null,
                                                "java/lang/Integer", false, null))),
                                new HashSet<>(), new HashSet<>());
                final Endpoint endpoint5 = new Endpoint("/prvni/uri/tridaNONSTATICtest",
                                Set.of(HttpMethod.PUT), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                final Endpoint endpoint7 = new Endpoint("/employee", Set.of(HttpMethod.POST),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                Set.of(new EndpointRequestBody(
                                                "com/baeldung/reactive/model/Employee", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                final Endpoint endpoint8 = new Endpoint("/nejaka/uri/s/argumentem/{id}",
                                Set.of(HttpMethod.POST), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/lang/String", false)),
                                new HashSet<>(Set.of(new EndpointParameter(null,
                                                "java/lang/Integer", false, null))),
                                new HashSet<>(), new HashSet<>());
                final Endpoint endpoint9 = new Endpoint("/test", HttpMethod.PUT)
                                .addExpectedResponse(
                                                new EndpointRequestBody("java/lang/String", false))
                                .addConsumes(new Header("Accept", "application/json"));
                final Endpoint endpoint10 = new Endpoint("/accept", HttpMethod.PUT)
                                .setConsumes(Set.of(new Header("Accept", "application/json")))
                                .addProduces(new Header("Content-Type", "application/json"))
                                .addExpectedResponse(
                                                new EndpointRequestBody("java/lang/String", false));

                springWebClientExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1,
                                endpoint2.getUrl(), endpoint2, endpoint3.getUrl(), endpoint3,
                                endpoint4.getUrl(), endpoint4, endpoint5.getUrl(), endpoint5,
                                endpoint9.getUrl(), endpoint9, endpoint7.getUrl(), endpoint7,
                                endpoint8.getUrl(), endpoint8, endpoint10.getUrl(), endpoint10

                );
        }

        public static void initResttemplate() {
                Endpoint endpoint1 = new Endpoint("http://localhost:8090", "/api/user/users",
                                Set.of(HttpMethod.GET), new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/util/List", true)),
                                new HashSet<>(),
                                Set.of(new Header("Content-Type", "application/json")),
                                Set.of(new Header("Accept", "application/json")));
                Endpoint endpoint2 = new Endpoint("http://localhost:8090", "/api/user/addUser",
                                Set.of(HttpMethod.POST),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                Endpoint endpoint3 = new Endpoint("http://localhost:8090", "/api/user/patchUser/",
                                Set.of(HttpMethod.PATCH),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                Set.of(new EndpointRequestBody("com/app/demo/model/User", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                Endpoint endpoint4 = new Endpoint("http://localhost:8090", "/api/user/deleteUser/",
                                Set.of(HttpMethod.DELETE), new HashSet<>(), new HashSet<>(),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                springResttemplateExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1,
                                endpoint2.getUrl(), endpoint2, endpoint3.getUrl(), endpoint3,
                                endpoint4.getUrl(), endpoint4

                );
        }

        public static void initJaxRs() {
                final String baseUrl = "http://localhost:8080/hrmanagerapi/webapi";
                final Endpoint endpoint1 = new Endpoint(baseUrl, "/emp", Set.of(HttpMethod.GET),
                                new HashSet<>(),
                                Set.of(new EndpointRequestBody("java/util/List", true)),
                                new HashSet<>(),
                                Set.of(new Header("Content-Type", "application/json")),
                                Set.of(new Header("Accept", "application/json")));
                final Endpoint endpoint2 = new Endpoint(baseUrl, "/emp/addemp",
                                Set.of(HttpMethod.POST),
                                Set.of(new EndpointRequestBody(
                                                "com/nagarro/hrmanager/model/Employee", false)),
                                new HashSet<>(), new HashSet<>(),
                                Set.of(new Header("Content-Type", "application/json")),
                                new HashSet<>());

                final Endpoint endpoint3 = new Endpoint(baseUrl, "/emp/update",
                                Set.of(HttpMethod.PUT),
                                Set.of(new EndpointRequestBody(
                                                "com/nagarro/hrmanager/model/Employee", false)),
                                new HashSet<>(), new HashSet<>(),
                                Set.of(new Header("Content-Type", "application/json")),
                                new HashSet<>());
                final Endpoint endpoint4 = new Endpoint(baseUrl, "/emp/delete/",
                                Set.of(HttpMethod.GET), new HashSet<>(),
                                Set.of(new EndpointRequestBody("javax/ws/rs/core/Response", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                final Endpoint endpoint5 = new Endpoint(baseUrl, "/emp/getemp/",
                                Set.of(HttpMethod.GET), new HashSet<>(),
                                Set.of(new EndpointRequestBody("javax/ws/rs/core/Response", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());
                final Endpoint endpoint6 = new Endpoint(baseUrl, "/user/getUser/",
                                Set.of(HttpMethod.GET), new HashSet<>(),
                                Set.of(new EndpointRequestBody("javax/ws/rs/core/Response", false)),
                                new HashSet<>(), new HashSet<>(), new HashSet<>());

                jaxRsExpectedEndpoints = Map.of(endpoint1.getUrl(), endpoint1, endpoint2.getUrl(),
                                endpoint2, endpoint3.getUrl(), endpoint3, endpoint4.getUrl(),
                                endpoint4, endpoint5.getUrl(), endpoint5, endpoint6.getUrl(),
                                endpoint6);
        }

        @BeforeClass
        public static void init() {
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
                        if (!springWebClientEndpoints.containsKey(endpoint.getPath())) {
                                fail("Missing endpoint " + endpoint);
                                return;
                        }
                        Endpoint found = springWebClientEndpoints.get(endpoint.getPath());
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
