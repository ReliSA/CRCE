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

import cz.zcu.kiv.crce.crce_integration_tests.rest.Options.Felix;
import cz.zcu.kiv.crce.crce_integration_tests.rest.Options.Osgi;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
//import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.IOException;

import org.apache.felix.dm.Component;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * This class serves as a minimal example of our integration tests. Also, if this test fails, something is likely
 * wrong with the environment
 */
@RunWith(JUnit4TestRunner.class)
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
    private String systemPackages =
            "com.sun.*,javax.xml.*,com.sun.org.apache.xerces.internal.*,"
            + "javax.accessibility,javax.annotation,javax.inject,javax.jmdns,javax.jms,javax.mail,"
            + "javax.mail.internet,javax.management,javax.management.modelmbean,javax.management.remote,"
            + "javax.microedition.io,javax.naming,javax.script,javax.security.auth.x500,javax.servlet,"
            + "javax.servlet.http,javax.servlet.jsp,javax.sql,"
            + "org.w3c.dom,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers,"
            + "org.w3c.dom.xpath,sun.io,org.w3c.dom.ls,"
            + "com.sun.java_cup.internal,com.sun.xml.internal.bind.v2";
    
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
                junitBundles(),
                felix(),
                // DS support
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager"),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),
                mavenBundle("org.apache.felix", "org.osgi.service.obr"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.metadata"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.storage"),
                mavenBundle("org.apache.felix", "org.apache.felix.shell"),
                mavenBundle("org.osgi", "org.osgi.compendium"),
                mavenBundle("org.slf4j", "slf4j-api"),
                mavenBundle("ch.qos.logback", "logback-core"),
                mavenBundle("ch.qos.logback", "logback-classic"),
                mavenBundle("com.sun.jersey", "jersey-core", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-server", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-servlet", "1.17"),
                //                mavenBundle("org.codehaus.groovy", "groovy-all"),
                //                mavenBundle("org.codehaus.janino", "commons-compiler"),

                //mavenBundle().groupId("log4j").artifactId("log4j").version("1.2.16"),

                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("2.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-metafile").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-api").version("2.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-indexer").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-assignment").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-rest").version("1.0.0-SNAPSHOT").classifier("classes"));
    }
    
    @Override
    protected void before() throws IOException {
        // configure the services you need; you cannot use the injected members yet
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[] {
                // create Dependency Manager components that should be started before the
                // test starts.
                createComponent()
                    .setImplementation(this)
                    .add(createServiceDependency()
                        .setService(PackageAdmin.class)
                        .setRequired(true))
                    .add(createServiceDependency()
                        .setService(ResourceFactory.class)
                //        .setRequired(true)
                )
                    .add(createServiceDependency()
                        .setService(ResourceDAO.class)
                //        .setRequired(true)
                )
        };
    }           

    // You can inject services as usual.
    private volatile PackageAdmin m_packageAdmin;

    @Test
    public void exampleTest() {
        assertEquals("Hey, who stole my package!",
                0,
                m_packageAdmin.getExportedPackage("org.osgi.framework").getExportingBundle().getBundleId());
    }
    
    private volatile ResourceFactory resourceFactory;       /* injected by dependency manager */
    private volatile ResourceDAO resourceDAO;     /* injected by dependency manager */
    
    @Test
    public void testResourceDAOImp() throws IOException {

        Resource r = resourceFactory.createResource();
        
        Capability cap = resourceFactory.createCapability("nameSpace", "1");
        r.addCapability(cap);
        
        Requirement req = resourceFactory.createRequirement("nameSpace", "1");
        r.addRequirement(req);
     
        //ResourceDAO impl = new ResourceDAOImpl();
        resourceDAO.saveResource(r);
        Resource r2 = resourceDAO.loadResource(null);
        
        assertTrue(r.equals(r2));
        
    }
}