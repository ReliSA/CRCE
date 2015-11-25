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
 *//*
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
package cz.zcu.kiv.crce.it.dao;

import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.felix.dm.Component;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.it.Configuration;
import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.it.Options.Crce;
import cz.zcu.kiv.crce.it.Options.Felix;
import cz.zcu.kiv.crce.it.Options.Osgi;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * This class serves as a minimal example of our integration tests. Also, if this test fails, something is likely wrong with the environment
 */
@RunWith(PaxExam.class)
public class ResourceDAOIT extends IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(ResourceDAOIT.class);

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
            + "javax.net,javax.net.ssl,javax.transaction.xa,"
            + "org.apache.commons.io";

    // This allows delegation of XPathFactory implementation to MyBatis.
    private final String bootDelegationPackages = "sun.*,com.sun.*";

    /**
     * Configuration of the OSGi runtime.
     *
     * @return the configuration
     */
    @org.ops4j.pax.exam.Configuration
    public Option[] configuration() {

        return options(
                systemPackage(systemPackages),
                bootDelegationPackage(bootDelegationPackages),
                junitBundles(),
                Options.logging(),
                Felix.dependencyManager(),

                Osgi.compendium(),
                Felix.configAdmin(),

                Crce.pluginApi(),

                Crce.metadata(),
                Crce.metadataService(),
                Crce.metadataJson(),
                Crce.metadataDao()
        );
    }

    @Override
    protected void before() throws IOException {
        // configure the services you need; you cannot use the injected members yet
        Configuration.metadataDao(this);
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[]{
            // create Dependency Manager components that should be started before the
            // test starts.
            createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceDAO.class).setRequired(true))
                .add(createServiceDependency().setService(RepositoryDAO.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataJsonMapper.class).setRequired(true))
            };
    }

    // You can inject services as usual.
    private volatile MetadataFactory metadataFactory;  /* injected by dependency manager */
    private volatile ResourceDAO resourceDAO;          /* injected by dependency manager */
    private volatile RepositoryDAO repositoryDAO;      /* injected by dependency manager */
    private volatile MetadataService metadataService;  /* injected by dependency manager */
    private volatile MetadataJsonMapper metadataJsonMapper;


    @Test
    public void testResourceDAOImp() throws IOException, URISyntaxException {

        Repository repository = metadataFactory.createRepository(new URI("file://a/b"));

        repositoryDAO.saveRepository(repository);

        Resource expected = metadataJsonMapper.deserialize(FileUtils.readFileToString(new File("src/test/resources/dao/Resource1.json")));
        assertNotNull(expected);

        //test boolean loading
        assertTrue(metadataService.getIdentity(expected).getAttribute(new SimpleAttributeType<Boolean>("confirmed", Boolean.class)).getValue());

        metadataService.getIdentity(expected).setAttribute("repository-id", String.class, repository.getId());

        URI uri = metadataService.getUri(expected);

        resourceDAO.saveResource(expected);
        Resource actual = resourceDAO.loadResource(uri);

        assertNotNull(actual);

        assertEquals(expected, actual);

        assertTrue(expected.equalsTo(actual, EqualityLevel.KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.SHALLOW_NO_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.SHALLOW_WITH_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.DEEP_NO_KEY));
        assertTrue(expected.equalsTo(actual, EqualityLevel.DEEP_WITH_KEY));

        //test boolean storage
        assertTrue(metadataService.getIdentity(actual).getAttribute(new SimpleAttributeType<Boolean>("confirmed", Boolean.class)).getValue());
    }
}
