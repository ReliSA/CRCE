/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cz.zcu.kiv.crce.crce_integration_tests.rest;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.felix.dm.Component;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * This class serves as a minimal example of our integration tests. Also, if this test fails, something is likely wrong with the environment
 */
@RunWith(PaxExam.class)
public class ExampleTest extends IntegrationTestBase {

    /*
    @Configuration
    public Option[] configuration() {
        return options(
            // you can add additional directives, e.g. systemProperty or VMOptions here
            junitBundles(),
            provision(
                Osgi.compendium(),
                Felix.dependencyManager()
                // add additional bundles here
            )
        );
    }
    */

    /**
     * Container includes and provides these packages when container is started.
     */
    private final String systemPackages
            = "com.sun.*,javax.xml.*,com.sun.org.apache.xerces.internal.*,"
            + "javax.accessibility,javax.annotation,javax.inject,javax.jmdns,javax.jms,javax.mail,"
            + "javax.mail.internet,javax.management,javax.management.modelmbean,javax.management.remote,"
            + "javax.microedition.io,javax.naming,javax.naming.spi,javax.script,javax.security.auth.x500,javax.servlet,"
            + "javax.servlet.http,javax.servlet.jsp,javax.sql,"
            + "org.w3c.dom,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers,"
            + "org.w3c.dom.xpath,sun.io,org.w3c.dom.ls,"
            + "com.sun.java_cup.internal,com.sun.xml.internal.bind.v2,"
            + "javax.net,javax.net.ssl,javax.transaction.xa";

    // This allows delegation of XPathFactory implementation to MyBatis.
    private final String bootDelegationPackages = "sun.*,com.sun.*";

    /**
     * Configuration of the OSGi runtime.
     *
     * @return the configuration
     */
    @Configuration
    public Option[] configuration() {

        System.out.println("Option config");
        return options(
                systemPackage(systemPackages),
                bootDelegationPackage(bootDelegationPackages),
                junitBundles(),
                // DS support
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager.runtime"),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),
                mavenBundle("org.apache.felix", "org.osgi.service.obr"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.metadata"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.storage"),
                mavenBundle("org.apache.felix", "org.apache.felix.shell"),
                mavenBundle("org.osgi", "org.osgi.compendium", "4.3.1"),
                mavenBundle("org.slf4j", "slf4j-api"),
                mavenBundle("ch.qos.logback", "logback-core"),
                mavenBundle("ch.qos.logback", "logback-classic"),
                mavenBundle("com.sun.jersey", "jersey-core", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-server", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-servlet", "1.17"),
                mavenBundle("com.h2database", "h2", "1.3.172"),
                mavenBundle("org.mybatis", "mybatis", "3.2.2"),

                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-impl").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-service-api").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-service-impl").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-api").versionAsInProject(),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-impl").versionAsInProject());
    }

    @Override
    protected void before() throws IOException {
        // configure the services you need; you cannot use the injected members yet
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[]{
            // create Dependency Manager components that should be started before the
            // test starts.
            createComponent()
            .setImplementation(this)
            .add(createServiceDependency()
            .setService(ResourceFactory.class).setRequired(true))
            .add(createServiceDependency()
            .setService(ResourceDAO.class).setRequired(true))
            .add(createServiceDependency()
            .setService(MetadataService.class).setRequired(true))
        };
    }

    // You can inject services as usual.
    private volatile ResourceFactory resourceFactory;  /* injected by dependency manager */
    private volatile ResourceDAO resourceDAO;          /* injected by dependency manager */
    private volatile MetadataService metadataService;  /* injected by dependency manager */


    @Test
    public void testResourceDAOImp() throws IOException, URISyntaxException {

        URI uri = new URI("file://a/b/c");

        Resource expected = resourceFactory.createResource();

        assertNotNull(expected);

        metadataService.setUri(expected, uri);

        Capability cap = resourceFactory.createCapability("nameSpace");
        metadataService.addRootCapability(expected, cap);

        Requirement req = resourceFactory.createRequirement("nameSpace");
        metadataService.addRequirement(expected, req);

        resourceDAO.saveResource(expected);
        Resource actual = resourceDAO.loadResource(uri);

        assertNotNull(actual);

        assertEquals(expected, actual);

        assertTrue(expected.equalsTo(actual, EqualityLevel.KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.SHALLOW_NO_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.SHALLOW_WITH_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.DEEP_NO_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.DEEP_WITH_KEY));
    }
}
