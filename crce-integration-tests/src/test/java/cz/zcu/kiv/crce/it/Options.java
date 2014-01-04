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
package cz.zcu.kiv.crce.it;


import static org.ops4j.pax.exam.CoreOptions.composite;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.extra.VMOption;

/**
 * This class contains a set of Pax Exam options, intended for typo-free provisioning of bundles.
 */
public class Options {

    public static class Osgi {

        public static MavenArtifactProvisionOption compendium() {
            return maven("org.osgi.compendium");
        }

        private static MavenArtifactProvisionOption maven(String artifactId) {
            return Options.mavenBundle("org.osgi", artifactId);
        }
    }

    public static class Felix {

        public static MavenArtifactProvisionOption preferences() {
            return maven("org.apache.felix.prefs");
        }

        public static Option dependencyManager() {
            return composite(
                    maven("org.apache.felix.dependencymanager"),
                    maven("org.apache.felix.dependencymanager.runtime")
            );
        }

        public static MavenArtifactProvisionOption configAdmin() {
            return maven("org.apache.felix.configadmin");
        }

        public static MavenArtifactProvisionOption eventAdmin() {
            return maven("org.apache.felix.eventadmin");
        }

        public static MavenArtifactProvisionOption deploymentAdmin() {
            return maven("org.apache.felix.deploymentadmin");
        }

        public static MavenArtifactProvisionOption bundleRepository() {
            return maven("org.apache.felix.bundlerepository");
        }

        private static MavenArtifactProvisionOption maven(String artifactId) {
            return Options.mavenBundle("org.apache.felix", artifactId);
        }
    }

    public static class Knopflerfish {

        public static MavenArtifactProvisionOption useradmin() {
            return mavenBundle("org.knopflerfish.bundle", "useradmin");
        }

        public static MavenArtifactProvisionOption log() {
            return mavenBundle("org.knopflerfish", "log");
        }
    }

    public static MavenArtifactProvisionOption jetty() {
        return mavenBundle("org.ops4j.pax.web", "pax-web-jetty-bundle");
    }

    public static Option logging() {
        return composite(
                    mavenBundle("org.slf4j", "slf4j-api"),
                    mavenBundle("ch.qos.logback", "logback-core"),
                    mavenBundle("ch.qos.logback", "logback-classic")
        );
    }

    public static class Crce {

        public static Option enableDebugger() {
            return enableDebugger(true, 65506);
        }

        public static Option enableDebugger(boolean suspend, int port) {
            return new VMOption("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=" + (suspend ? "y" : "n") + ",address=" + port);
        }

        public static Option metadata() {
            return composite(
                    metadataApi(),
                    metadataImpl()
            );
        }

        public static Option metadataService() {
            return composite(
                    metadataServiceApi(),
                    metadataServiceImpl()
            );
        }

        public static Option metadataDao() {
            return composite(
                    metadataDaoApi(),
                    metadataDaoImpl(),
                    mavenBundle("com.h2database", "h2"),
                    mavenBundle("org.mybatis", "mybatis")
            );
        }

        public static Option repository() {
            return composite(
                    repositoryApi(),
                    repositoryImpl()
            );
        }

        public static MavenArtifactProvisionOption metadataServiceApi() {
            return maven("crce-metadata-service-api");
        }

        public static MavenArtifactProvisionOption metadataServiceImpl() {
            return maven("crce-metadata-service-impl");
        }

        public static MavenArtifactProvisionOption metadataDaoApi() {
            return maven("crce-metadata-dao-api");
        }

        public static MavenArtifactProvisionOption metadataDaoImpl() {
            return maven("crce-metadata-dao-impl");
        }

        public static MavenArtifactProvisionOption metadataApi() {
            return maven("crce-metadata-api");
        }

        public static MavenArtifactProvisionOption metadataImpl() {
            return maven("crce-metadata-impl");
        }

        public static MavenArtifactProvisionOption pluginApi() {
            return maven("crce-plugin-api");
        }

        public static MavenArtifactProvisionOption repositoryApi() {
            return maven("crce-repository-api");
        }

        public static MavenArtifactProvisionOption metadataIndexerApi() {
            return maven("crce-metadata-indexer-api");
        }

        public static MavenArtifactProvisionOption metadataIndexer() {
            return maven("crce-metadata-indexer");
        }

        public static MavenArtifactProvisionOption repositoryImpl() {
            return maven("crce-repository-impl");
        }

        public static MavenArtifactProvisionOption compatibilityApi() {
            return maven("crce-compatibility-api");
        }

        public static MavenArtifactProvisionOption compatibilityDaoApi() {
            return maven("crce-compatibility-dao-api");
        }

        public static MavenArtifactProvisionOption compatibilityImpl() {
            return maven("crce-compatibility-impl");
        }

        public static MavenArtifactProvisionOption compatibilityDaoMongo() {
            return maven("crce-compatibility-dao-mongodb");
        }

        public static MavenArtifactProvisionOption maven(String artifactId) {
            return Options.mavenBundle("cz.zcu.kiv.crce", artifactId);
        }
    }

    private static MavenArtifactProvisionOption mavenBundle(String groupId, String artifactId) {
        return CoreOptions.mavenBundle().groupId(groupId).artifactId(artifactId).versionAsInProject();
    }
}