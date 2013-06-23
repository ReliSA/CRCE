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
package cz.zcu.kiv.crce.metadata.osgi.internal;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.felix.utils.manifest.Attribute;
import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Directive;
import org.apache.felix.utils.manifest.Parser;
import org.apache.felix.utils.version.VersionCleaner;
import org.apache.felix.utils.version.VersionRange;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * This is original class DataModelHelperImpl adopted from org.apache.felix.bundlerepository.
 *
 */
public class OsgiManifestBundleIndexer extends AbstractResourceIndexer {

    private static final Logger logger = LoggerFactory.getLogger(OsgiManifestBundleIndexer.class);

    public static final String BUNDLE_LICENSE = "Bundle-License";
    public static final String BUNDLE_SOURCE = "Bundle-Source";

    private volatile ResourceFactory resourceFactory;
    private volatile MetadataService metadataService;

    @Override
    @SuppressWarnings("unchecked")
    public List<String> index(final InputStream input, Resource resource) {

        try {
            URLStreamHandler handler = new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(null) {
                        @Override
                        public void connect() throws IOException {
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return input;
                        }
                    };

                }
            };
            fillResource(new URL("none", "none", 0, "none", handler), resource);
            //            fres = repositoryAdmin.getHelper().createResource(new URL("none", "none", 0, "none", handler));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unexpected MalformedURLException", e);
        } catch (ZipException e) {
            logger.warn("Zip file is corrupted: {}", resource.getId(), e);
            metadataService.addCategory(resource, "corrupted");
            return Collections.singletonList("corrupted");
        } catch (IOException ex) {
            logger.error("I/O error on indexing resource: {}", resource.getId(), ex);
            return Collections.emptyList();
        } catch (IllegalArgumentException e) {
            // not a bundle
            return Collections.emptyList();
        }

        Capability identity = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);

        if (identity.getAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME) == null) {
            return Collections.emptyList();
        }

        cz.zcu.kiv.crce.metadata.Attribute<String> pn = identity.getAttribute(NsOsgiIdentity.ATTRIBUTE__PRESENTATION_NAME);
        if (pn != null) {
            metadataService.setPresentationName(resource, pn.getValue());
        }

        metadataService.addCategory(resource, "osgi");

        return Collections.singletonList("osgi");
    }

    @Override
    public List<String> getProvidedCategories() {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, "osgi", "corrupted");
        return result;
    }

    public Resource fillResource(final URL bundleUrl, Resource resource) throws IOException {
        fillResource(new Headers() {
            private final Manifest manifest;
            private Properties localization;

            {
                // Do not use a JarInputStream so that we can read the manifest even if it's not
                // the first entry in the JAR.
                byte[] man = loadEntry(JarFile.MANIFEST_NAME);
                if (man == null) {
                    throw new IllegalArgumentException("The specified url is not a valid jar (can't read manifest): " + bundleUrl);
                }
                manifest = new Manifest(new ByteArrayInputStream(man));
            }

            public String getHeader(String name) throws IOException {
                String value = manifest.getMainAttributes().getValue(name);
                if (value != null && value.startsWith("%")) {
                    if (localization == null) {
//                        try
//                        {
                        localization = new Properties();
                        String path = manifest.getMainAttributes().getValue(Constants.BUNDLE_LOCALIZATION);
                        if (path == null) {
                            path = Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
                        }
                        path += ".properties";
                        byte[] loc = loadEntry(path);
                        if (loc != null) {
                            localization.load(new ByteArrayInputStream(loc));
                        }
//                        }
//                        catch (IOException e)
//                        {
//                            // TODO: ?
//                        }
                    }
                    value = value.substring(1);
                    value = localization.getProperty(value, value);
                }
                return value;
            }

            private byte[] loadEntry(String name) throws IOException {
                try (InputStream is = FileUtil.openURL(bundleUrl); ZipInputStream jis = new ZipInputStream(is)) {
                    for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
                        if (name.equalsIgnoreCase(e.getName())) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            int n;
                            while ((n = jis.read(buf, 0, buf.length)) > 0) {
                                baos.write(buf, 0, n);
                            }
                            return baos.toByteArray();
                        }
                    }
                }
                return null;
            }
        }, resource);
//        if (resource != null)
//        {
//            if ("file".equals(bundleUrl.getProtocol()))
//            {
//                try {
//                    File f = new File(bundleUrl.toURI());
//                    metadataService.setSize(resource, f.length());
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            try {
//                metadataService.setUri(resource, bundleUrl.toURI());
//    //            resource.put(Resource.URI, bundleUrl.toExternalForm(), null);
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return resource;
    }

    private Resource fillResource(Headers headers, Resource resource) throws IOException {
        String bsn = headers.getHeader(Constants.BUNDLE_SYMBOLICNAME);
        if (bsn == null) {
            return null;
        }
        populate(headers, resource);
        return resource;
    }

    private void populate(Headers headers, Resource resource) throws IOException {
        String bsn = getSymbolicName(headers);
        String v = getVersion(headers);
        Capability identity = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);

        identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__ID, bsn + "/" + v);
        identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, bsn);
        identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__VERSION, v);
        if (headers.getHeader(Constants.BUNDLE_NAME) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__PRESENTATION_NAME, headers.getHeader(Constants.BUNDLE_NAME));
        }
        if (headers.getHeader(Constants.BUNDLE_DESCRIPTION) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__DESCRIPTION, headers.getHeader(Constants.BUNDLE_DESCRIPTION));
        }
        if (headers.getHeader(BUNDLE_LICENSE) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__LICENSE_URI, headers.getHeader(BUNDLE_LICENSE));
        }
        if (headers.getHeader(Constants.BUNDLE_COPYRIGHT) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__COPYRIGHT, headers.getHeader(Constants.BUNDLE_COPYRIGHT));
        }
        if (headers.getHeader(Constants.BUNDLE_DOCURL) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__DOCUMENTATION_URI, headers.getHeader(Constants.BUNDLE_DOCURL));
        }
        if (headers.getHeader(BUNDLE_SOURCE) != null) {
            identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__SOURCE_URI, headers.getHeader(BUNDLE_SOURCE));
        }

        doCategories(resource, headers);

        Capability bundle = doBundle(resource, headers);

        doExportServices(resource, bundle, headers);
        doExports(resource, bundle, headers);
        doImports(resource, headers);
        doImportServices(resource, headers);
        doRequires(resource, headers);
        doFragment(resource, bundle, headers);
        doExecutionEnvironment(resource, headers);
    }

    private void doCategories(Resource resource, Headers headers) throws IOException {
        Capability identity = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.BUNDLE_CATEGORY));
        for (int i = 0; clauses != null && i < clauses.length; i++) {
            List<String> categories = identity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__CATEGORY);
            if (categories == null) {
                categories = new ArrayList<>();
                identity.setAttribute(NsOsgiIdentity.ATTRIBUTE__CATEGORY, categories);
            }
            categories.add(clauses[i].getName());
        }
    }

    private void doImportServices(Resource resource, Headers headers) throws IOException {
        Clause[] imports = Parser.parseHeader(headers.getHeader(Constants.IMPORT_SERVICE));
        for (int i = 0; imports != null && i < imports.length; i++) {
//            RequirementImpl ri = new RequirementImpl(Capability.SERVICE);
            Requirement ri = resourceFactory.createRequirement(NsOsgiService.NAMESPACE__OSGI_SERVICE);
            createServiceFilter(ri, imports[i]);
            ri.setDirective("text", "Import Service " + imports[i].getName()); // TODO constant

            String avail = imports[i].getDirective("availability");
            String mult = imports[i].getDirective("multiple");
            ri.setDirective("optional", Boolean.toString("optional".equalsIgnoreCase(avail))); // TODO constant
            ri.setDirective("multiple", Boolean.toString(!"false".equalsIgnoreCase(mult))); // TODO constant
            ri.setResource(resource);
            resource.addRequirement(ri);
        }
    }

    private void doExportServices(Resource resource, Capability root, Headers headers) throws IOException {
        Clause[] exports = Parser.parseHeader(headers.getHeader(Constants.EXPORT_SERVICE));
        if (exports != null) {
            for (Clause export : exports) {
                Capability cap = createServiceCapability(export);
                cap.setResource(resource);
                cap.setParent(root);
                root.addChild(cap);
                resource.addCapability(cap);
            }
        }
    }

    private void createServiceFilter(Requirement ri, Clause clause) {
        String f = clause.getAttribute("filter");
        ri.addAttribute(NsOsgiService.ATTRIBUTE__NAME, clause.getName());

        if (f != null) {
            ri.setDirective("filter", f); // TODO constant
        }
    }

    private Capability createServiceCapability(Clause clause) {
        Capability capability = resourceFactory.createCapability(NsOsgiService.NAMESPACE__OSGI_SERVICE);
        capability.setAttribute(NsOsgiService.ATTRIBUTE__NAME, clause.getName());
        Attribute[] attributes = clause.getAttributes();
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                capability.setAttribute(new SimpleAttributeType<>(attribute.getName(), String.class), attribute.getValue());
            }
        }
        return capability;
    }

    private void doFragment(Resource resource, Capability root, Headers headers) throws IOException {
        // Check if we are a fragment
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.FRAGMENT_HOST));
        if (clauses != null && clauses.length == 1) {
            // We are a fragment, create a requirement
            // to our host.
            Requirement r = resourceFactory.createRequirement(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
            r.addAttribute(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME, clauses[0].getName());
            appendVersion(r, NsOsgiBundle.ATTRIBUTE__VERSION, VersionRange.parseVersionRange(clauses[0].getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE)));
            r.setDirective("text", "Required Host " + clauses[0].getName()); // TODO constant
            r.setDirective("extend", "true"); // TODO constant
            r.setDirective("optional", "false"); // TODO constant
            r.setDirective("multiple", "false"); // TODO constant
            r.setResource(resource);
            resource.addRequirement(r);

            // And insert a capability that we are available
            // as a fragment. ### Do we need that with extend?
            Capability capability = resourceFactory.createCapability(NsOsgiFragment.NAMESPACE__OSGI_FRAGMENT);
            capability.setAttribute(NsOsgiFragment.ATTRIBUTE__HOST, clauses[0].getName());
            capability.setAttribute(NsOsgiFragment.ATTRIBUTE__VERSION, new Version(getVersion(clauses[0])));
            capability.setResource(resource);
            capability.setParent(root);
            root.addChild(capability);
            resource.addCapability(capability);
        }
    }

    private void doRequires(Resource resource, Headers headers) throws IOException {
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.REQUIRE_BUNDLE));
        for (int i = 0; clauses != null && i < clauses.length; i++) {
            Requirement r = resourceFactory.createRequirement(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);

            VersionRange v = VersionRange.parseVersionRange(clauses[i].getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE));

            r.addAttribute(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME, clauses[i].getName());

            appendVersion(r, NsOsgiBundle.ATTRIBUTE__VERSION, v);

            r.setDirective("text", "Require Bundle " + clauses[i].getName() + "; " + v); // TODO constant
            r.setDirective("optional", Boolean.toString( // TODO constant
                    Constants.RESOLUTION_OPTIONAL.equalsIgnoreCase(clauses[i].getDirective(Constants.RESOLUTION_DIRECTIVE))));
            r.setResource(resource);
            resource.addRequirement(r);
        }
    }

    private Capability doBundle(Resource resource, Headers headers) throws IOException {
        Capability capability = resourceFactory.createCapability(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        capability.setAttribute(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME, getSymbolicName(headers));
        if (headers.getHeader(Constants.BUNDLE_NAME) != null) {
            capability.setAttribute(NsOsgiBundle.ATTRIBUTE__PRESENTATION_NAME, headers.getHeader(Constants.BUNDLE_NAME));
        }
        capability.setAttribute(NsOsgiBundle.ATTRIBUTE__VERSION, new Version(getVersion(headers)));
        capability.setAttribute(NsOsgiBundle.ATTRIBUTE__MANIFEST_VERSION, getManifestVersion(headers));
        capability.setResource(resource);
        resource.addCapability(capability);
        resource.addRootCapability(capability);
        return capability;
    }

    private void doExports(Resource resource, Capability root, Headers headers) throws IOException {
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.EXPORT_PACKAGE));
        if (clauses != null) {
            for (Clause clause : clauses) {
                Capability capability = createCapability(clause); // Capability.PACKAGE,
                capability.setResource(resource);
                capability.setParent(root);
                root.addChild(capability);
                resource.addCapability(capability);
            }
        }
    }

    private Capability createCapability(Clause clause) {
        Capability capability = resourceFactory.createCapability(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE);
        capability.setAttribute(NsOsgiPackage.ATTRIBUTE__NAME, clause.getName());
        capability.setAttribute(NsOsgiPackage.ATTRIBUTE__VERSION, new Version(getVersion(clause)));
        Attribute[] attributes = clause.getAttributes();
        for (int i = 0; attributes != null && i < attributes.length; i++) {
            String key = attributes[i].getName();
            if (key.equalsIgnoreCase(Constants.PACKAGE_SPECIFICATION_VERSION) || key.equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)) {
                continue;
            } else {
                String value = attributes[i].getValue();
                capability.setAttribute(new SimpleAttributeType<>(key, String.class), value);
            }
        }
        Directive[] directives = clause.getDirectives();
        for (int i = 0; directives != null && i < directives.length; i++) {
            String key = directives[i].getName();
            String value = directives[i].getValue();
            capability.setDirective(key, value);
        }
        return capability;
    }

    private void doImports(Resource resource, Headers headers) throws IOException {
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.IMPORT_PACKAGE));
        for (int i = 0; clauses != null && i < clauses.length; i++) {
            Requirement requirement = resourceFactory.createRequirement(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE);

            createImportFilter(requirement, NsOsgiPackage.ATTRIBUTE__NAME, clauses[i]);
            requirement.setDirective("text", "Import package " + clauses[i]); // TODO constant
            requirement.setDirective("optional", Boolean.toString( // TODO constant
                    Constants.RESOLUTION_OPTIONAL.equalsIgnoreCase(clauses[i].getDirective(Constants.RESOLUTION_DIRECTIVE))));
            requirement.setResource(resource);
            resource.addRequirement(requirement);
        }
    }

    private void createImportFilter(Requirement requirement, AttributeType<String> name, Clause clause) {
        requirement.addAttribute(name, clause.getName());
        appendVersion(requirement, NsOsgiPackage.ATTRIBUTE__VERSION, getVersionRange(clause));
        Attribute[] attributes = clause.getAttributes();
        Set<String> attrs = doImportPackageAttributes(requirement, attributes);

        // The next code is using the subset operator
        // to check mandatory attributes, it seems to be
        // impossible to rewrite. It must assert that whateber
        // is in mandatory: must be in any of the attributes.
        // This is a fundamental shortcoming of the filter language.
        if (!attrs.isEmpty()) {
            logger.warn("'mandatory:<*' part of OSGi import filter is not implemented for CRCE. Mandatory attributes: {}", attrs);
            // TODO - is 'mandatory:<*' necessary for CRCE?
//            String del = "";
//            filter.append("(mandatory:<*");
//            for (Iterator i = attrs.iterator(); i.hasNext();)
//            {
//                filter.append(del);
//                filter.append(i.next());
//                del = ", ";
//            }
//            filter.append(")");
        }
//        filter.append(")");
//        requirement.setFilter(filter.toString());
    }

    private Set<String> doImportPackageAttributes(Requirement requirement, Attribute[] attributes) {
        HashSet<String> set = new HashSet<>();
        for (int i = 0; attributes != null && i < attributes.length; i++) {
            String name = attributes[i].getName();
            String value = attributes[i].getValue();
            if (name.equalsIgnoreCase(Constants.PACKAGE_SPECIFICATION_VERSION) || name.equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)) {
                continue;
            } else if (name.equalsIgnoreCase(Constants.RESOLUTION_DIRECTIVE + ":")) {
                requirement.setDirective("optional", Boolean.toString(Constants.RESOLUTION_OPTIONAL.equalsIgnoreCase(value))); // TODO constant
            }
            if (!name.endsWith(":")) {
                requirement.setDirective(name, value);
                set.add(name);
            }
        }
        return set;
    }

    private void doExecutionEnvironment(Resource resource, Headers headers) throws IOException {
        Clause[] clauses = Parser.parseHeader(headers.getHeader(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT));
        if (clauses != null && clauses.length > 0) {
            Requirement req = resourceFactory.createRequirement(NsOsgiExecutionEnvironment.NAMESPACE__OSGI_EXECUTION_ENVIRONMENT);
            req.setDirective("operation", "or"); // TODO constant

            StringBuffer sb = new StringBuffer();
            sb.append("(|");
            for (int i = 0; i < clauses.length; i++) {
                req.addAttribute(NsOsgiExecutionEnvironment.ATTRIBUTE__EXECUTION_ENVIRONMENT, clauses[i].getName());
                sb.append("(");
                sb.append(NsOsgiExecutionEnvironment.ATTRIBUTE__EXECUTION_ENVIRONMENT.getName());
                sb.append("=");
                sb.append(clauses[i].getName());
                sb.append(")");
            }
            sb.append(")");

            req.setDirective("text", "Execution Environment " + sb.toString());
            req.setResource(resource);
            resource.addRequirement(req);
        }
    }

    private static String getVersion(Clause clause) {
        String v = clause.getAttribute(Constants.VERSION_ATTRIBUTE);
        if (v == null) {
            v = clause.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION);
        }
        if (v == null) {
            v = clause.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE);
        }
        return VersionCleaner.clean(v);
    }

    private VersionRange getVersionRange(Clause clause) {
        String v = clause.getAttribute(Constants.VERSION_ATTRIBUTE);
        if (v == null) {
            v = clause.getAttribute(Constants.PACKAGE_SPECIFICATION_VERSION);
        }
        if (v == null) {
            v = clause.getAttribute(Constants.BUNDLE_VERSION_ATTRIBUTE);
        }
        return VersionRange.parseVersionRange(v);
    }

    private static String getSymbolicName(Headers headers) throws IOException {
        String bsn = headers.getHeader(Constants.BUNDLE_SYMBOLICNAME);
        if (bsn == null) {
            bsn = headers.getHeader(Constants.BUNDLE_NAME);
            if (bsn == null) {
                bsn = "Untitled-" + headers.hashCode();
            }
        }
        Clause[] clauses = Parser.parseHeader(bsn);
        return clauses[0].getName();
    }

    private static String getVersion(Headers headers) throws IOException {
        String v = headers.getHeader(Constants.BUNDLE_VERSION);
        return VersionCleaner.clean(v);
    }

    private static String getManifestVersion(Headers headers) throws IOException {
        String v = headers.getHeader(Constants.BUNDLE_MANIFESTVERSION);
        if (v == null) {
            v = "1";
        }
        return v;
    }

    private static void appendVersion(Requirement requirement, AttributeType<Version> type, VersionRange version) {
        if (version != null) {
            if (!version.isOpenFloor()) {
                if (!Version.emptyVersion.equals(version.getFloor())) {
                    requirement.addAttribute(type, version.getFloor(), Operator.GREATER_EQUAL);
                }
            } else {
                requirement.addAttribute(type, version.getFloor(), Operator.GREATER);
            }

            if (!VersionRange.INFINITE_VERSION.equals(version.getCeiling())) {
                if (!version.isOpenCeiling()) {
                    requirement.addAttribute(type, version.getCeiling(), Operator.LESS_EQUAL);
                } else {
                    requirement.addAttribute(type, version.getCeiling(), Operator.LESS);
                }
            }
        }
    }

    private interface Headers {

        String getHeader(String name) throws IOException;
    }

}
