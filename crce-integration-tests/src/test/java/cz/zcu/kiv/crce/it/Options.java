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


import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.ops4j.pax.exam.Option;
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
            return Options.maven("org.osgi", artifactId);
        }
    }

    public static class Felix {
        public static MavenArtifactProvisionOption preferences() {
            return maven("org.apache.felix.prefs");
        }

        public static MavenArtifactProvisionOption dependencyManager() {
            return maven("org.apache.felix.dependencymanager");
        }

        public static MavenArtifactProvisionOption dependencyManagerRuntime() {
            return maven("org.apache.felix.dependencymanager.runtime");
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
            return Options.maven("org.apache.felix", artifactId);
        }
    }

    public static class Knopflerfish {
        public static MavenArtifactProvisionOption useradmin() {
            return maven("org.knopflerfish.bundle", "useradmin");
        }
        public static MavenArtifactProvisionOption log() {
            return maven("org.knopflerfish", "log");
        }
    }

    public static MavenArtifactProvisionOption jetty() {
        return maven("org.ops4j.pax.web", "pax-web-jetty-bundle");
    }

    public static class Crce {
//        public static WrappedUrlProvisionOption util() {
            // we do this because we need access to some test classes that aren't exported
//            return wrappedBundle(mavenBundle("org.apache.ace", "org.apache.ace.util")).overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL);
//        }

        public static Option enableDebugger() {
            return enableDebugger(true, 65506);
        }

        public static Option enableDebugger(boolean suspend, int port) {
            return new VMOption("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=" + (suspend ? "y" : "n") + ",address=" + port);
        }

        public static MavenArtifactProvisionOption metadataApi() {
            return maven("crce-metadata-api");
        }

        public static MavenArtifactProvisionOption pluginApi() {
            return maven("crce-plugin-api");
        }

        public static MavenArtifactProvisionOption metadataServiceApi() {
            return maven("crce-metadata-service-api");
        }

        public static MavenArtifactProvisionOption metadataDaoApi() {
            return maven("crce-metadata-dao-api");
        }

        public static MavenArtifactProvisionOption metadataImpl() {
            return maven("crce-metadata-impl");
        }

        public static MavenArtifactProvisionOption metadataServiceImpl() {
            return maven("crce-metadata-service-impl");
        }

        public static MavenArtifactProvisionOption metadataDaoImpl() {
            return maven("crce-metadata-dao-impl");
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
            return Options.maven("cz.zcu.kiv.crce", artifactId);
        }
    }

    private static MavenArtifactProvisionOption maven(String groupId, String artifactId) {
        return mavenBundle().groupId(groupId).artifactId(artifactId).versionAsInProject();
    }
}