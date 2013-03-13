package cz.zcu.kiv.crce.metadata.cosi.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.cosi.core.bundlemetadata.BundleMetadata;
import cz.zcu.kiv.cosi.core.bundlemetadata.ManifestComplexHeader;
import cz.zcu.kiv.cosi.core.bundlemetadata.ProvidingNamedTypeHeaderEntry;
import cz.zcu.kiv.cosi.core.bundlemetadata.ProvidingTypeHeaderEntry;
import cz.zcu.kiv.cosi.core.bundlemetadata.RequiringNamedTypeHeaderEntry;
import cz.zcu.kiv.cosi.core.bundlemetadata.RequiringTypeHeaderEntry;
import cz.zcu.kiv.cosi.core.bundlemetadata.Version;
import cz.zcu.kiv.cosi.core.bundlemetadata.VersionFormatException;
import cz.zcu.kiv.cosi.core.bundlemetadata.VersionRange;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;



/**
 * implementation of <code>ResourceIndexer</code> which provides support for
 * indexing CoSi components metadata.
 * @author Natalia Rubinova
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CoSiManifestBundleIndexer extends AbstractResourceIndexer {

    private static final Logger logger = LoggerFactory.getLogger(CoSiManifestBundleIndexer.class);
    
    @Override
    public String[] index(final InputStream input, Resource resource) {
        BundleMetadata m_bundleMetadata;
        try {
            JarManifestExtractor jarReader = new JarManifestExtractor(input);
            if (jarReader.getManifest() != null) {
                m_bundleMetadata = new BundleMetadata(jarReader.getManifest());
            } else {
                return new String[0];
            }
        } catch (IOException e) {
            logger.error("I/O error on indexing resource: {}", resource.getId(), e);
            return new String[0];
        } catch (VersionFormatException e) {
            return new String[0];
        } catch (Exception e) {
            logger.error("Unknown exception on indexing by CoSi indexer: {}", resource.getId(), e);
            return new String[0];
        }

        if (m_bundleMetadata == null) {
            return new String[0];
        }

        if (!m_bundleMetadata.isManifestValid()) {
            return new String[0];
        }


        resource.setSymbolicName(m_bundleMetadata.getBundleName(), true);
        Version version = m_bundleMetadata.getBundleVersion();

        if (version != null) {
            resource.setVersion(version.toString(), true);
        }

        resource.setPresentationName(m_bundleMetadata.getBundleDescription());
        
        createCapabilities(m_bundleMetadata, resource);

        createRequirements(m_bundleMetadata, resource);

        // TODO properties, if necessary
        resource.addCategory("cosi");

        return new String[]{"cosi"};
    }

    @Override
    public String[] getProvidedCategories() {
        return new String[]{"cosi", "corrupted"};
    }

    /**
     * 
     * @param bundleMetadata
     * @param res
     */
    private void createCapabilities(BundleMetadata bundleMetadata, Resource res) {

        if (bundleMetadata == null) {
            return;
        }
        //bundle
        Capability capBundle = res.createCapability("cosi-bundle");

        capBundle.setProperty("symbolicname", bundleMetadata.getBundleName());
        capBundle.setProperty("presentationname", bundleMetadata.getBundleDescription());
        capBundle.setProperty("version", convertCoSiToOSGiVersion(bundleMetadata.getBundleVersion()));
        capBundle.setProperty("manifestversion", bundleMetadata.getCosiVersion());

        res.addCapability(capBundle);

        //types
        if (bundleMetadata.getProvideTypes() != null) {
            for (ProvidingTypeHeaderEntry headerEntry : bundleMetadata.getProvideTypes()) {
                Capability capProvideTypes = res.createCapability("type");

                setCapabilityProperties(capProvideTypes, "type", headerEntry.getValue(), headerEntry.getNameAttribute(), null, headerEntry.getVersionAttribute(), headerEntry.getExtraFuncAttribute());

                res.addCapability(capProvideTypes);
            }
        }
        //services
        if (bundleMetadata.getProvideInterfaces() != null) {
            for (ProvidingTypeHeaderEntry headerEntry : bundleMetadata.getProvideInterfaces()) {
                Capability capProvideServices = res.createCapability("service");

                setCapabilityProperties(capProvideServices, "service", headerEntry.getValue(), headerEntry.getNameAttribute(), null, headerEntry.getVersionAttribute(), headerEntry.getExtraFuncAttribute());

                res.addCapability(capProvideServices);
            }
        }
        //attributes
        if (bundleMetadata.getProvideAttributes() != null) {
            for (ProvidingNamedTypeHeaderEntry headerEntry : bundleMetadata.getProvideAttributes()) {
                Capability capProvideAttributes = res.createCapability("attribute");

                setCapabilityProperties(capProvideAttributes, null, null, headerEntry.getName(), headerEntry.getTypeParameter(), headerEntry.getVersionParameter(), (ExtraFunc) headerEntry.getParameterValue(ManifestComplexHeader.EXTRAFUNC_PARAMETER));


                res.addCapability(capProvideAttributes);
            }
        }
        //events
        if (bundleMetadata.getGenerateEvents() != null) {
            for (ProvidingNamedTypeHeaderEntry headerEntry : bundleMetadata.getGenerateEvents()) {
                Capability capGeneratedEvents = res.createCapability("event");

                setCapabilityProperties(capGeneratedEvents, null, null, headerEntry.getName(), headerEntry.getTypeParameter(), headerEntry.getVersionParameter(), (ExtraFunc) headerEntry.getParameterValue(ManifestComplexHeader.EXTRAFUNC_PARAMETER));


                res.addCapability(capGeneratedEvents);
            }
        }
        //packages
        if (bundleMetadata.getProvidePackages() != null) {
            for (ProvidingTypeHeaderEntry headerEntry : bundleMetadata.getProvidePackages()) {
                Capability capProvidePackages = res.createCapability("package");

                setCapabilityProperties(capProvidePackages, "package", headerEntry.getValue(), headerEntry.getNameAttribute(), null, headerEntry.getVersionAttribute(), headerEntry.getExtraFuncAttribute());

                res.addCapability(capProvidePackages);
            }
        }
    }

    /**
     * 
     * @param cap
     * @param capName
     * @param value
     * @param name
     * @param type
     * @param version
     * @param extrafunc
     */
    private void setCapabilityProperties(Capability cap, String capName, String value, String name, String type, Version version, ExtraFunc extrafunc) {

        if (capName != null && !capName.toString().isEmpty()) {
            cap.setProperty(capName, value);
        }

        if (name != null && !name.toString().isEmpty()) {
            cap.setProperty("name", name);
        }

        if (type != null && !type.toString().isEmpty()) {
            cap.setProperty("type", type);
        }

        if (version != null && !version.toString().equals("0.0.0")) {
            cap.setProperty("version", convertCoSiToOSGiVersion(version));
        }

        if (extrafunc != null && !extrafunc.toString().isEmpty()) {
            cap.setProperty("extrafunc", extrafunc.toString());
        }
    }

    /**
     * 
     * @param version
     * @return
     */
    private org.osgi.framework.Version convertCoSiToOSGiVersion(Version version) {
        org.osgi.framework.Version osgiVersion = new org.osgi.framework.Version(version.toString());

        return osgiVersion;
    }

    /**
     * 
     * @param bundleMetadata
     * @param res
     */
    private void createRequirements(BundleMetadata bundleMetadata, Resource res) {
        //types	
        if (bundleMetadata.getRequireTypes() != null) {
            for (RequiringTypeHeaderEntry headerEntry : bundleMetadata.getRequireTypes()) {
                Requirement req = res.createRequirement("type");
                setRequirementProperties(req, "type", headerEntry.getValue(), headerEntry.getBundleNameAttribute(), null, headerEntry.getInterfaceVersionRangeAttribute(), headerEntry.getBundleExtraFuncAttribute(), headerEntry.isOptional());
            }
        }
        //services
        if (bundleMetadata.getRequireInterfaces() != null) {
            for (RequiringTypeHeaderEntry headerEntry : bundleMetadata.getRequireInterfaces()) {
                Requirement req = res.createRequirement("service");
                setRequirementProperties(req, "service", headerEntry.getValue(), headerEntry.getBundleNameAttribute(), null, headerEntry.getInterfaceVersionRangeAttribute(), headerEntry.getBundleExtraFuncAttribute(), headerEntry.isOptional());
            }
        }
        //attributes
        if (bundleMetadata.getRequireAttributes() != null) {
            for (RequiringNamedTypeHeaderEntry headerEntry : bundleMetadata.getRequireAttributes()) {
                Requirement req = res.createRequirement("attribute");
                setRequirementProperties(req, "name", headerEntry.getName(), null, headerEntry.getTypeParameter(), headerEntry.getVersionRangeParameter(), null, false);
            }
        }
        //events
        if (bundleMetadata.getConsumeEvents() != null) {
            for (RequiringNamedTypeHeaderEntry headerEntry : bundleMetadata.getConsumeEvents()) {
                Requirement req = res.createRequirement("event");
                setRequirementProperties(req, "name", headerEntry.getName(), null, headerEntry.getTypeParameter(), headerEntry.getVersionRangeParameter(), null, false);
            }
        }
        //packages
        if (bundleMetadata.getRequirePackages() != null) {
            for (RequiringTypeHeaderEntry headerEntry : bundleMetadata.getRequirePackages()) {
                Requirement req = res.createRequirement("package");
                setRequirementProperties(req, "package", headerEntry.getValue(), headerEntry.getBundleNameAttribute(), null, headerEntry.getInterfaceVersionRangeAttribute(), headerEntry.getBundleExtraFuncAttribute(), false);
            }
        }
    }

    /**
     *  
     * @param req
     * @param requirementName
     * @param value
     * @param name
     * @param type
     * @param versionRange
     * @param extrafunc
     * @param isOptional
     */
    private void setRequirementProperties(Requirement req, String requirementName, String value, String name, String type, VersionRange versionRange, ExtraFunc extrafunc, boolean isOptional) {
        StringBuffer sb = new StringBuffer();

        boolean isNamePresent = false;
        boolean isExtrafuncPresent = false;
        boolean isVersionrangePresent = false;
        boolean isTypePresent = false;

        if (name != null && !name.isEmpty()) {
            isNamePresent = true;
        }
        if (type != null && !type.isEmpty()) {
            isTypePresent = true;
        }
        if (extrafunc != null && !extrafunc.toString().isEmpty()) {
            isExtrafuncPresent = true;
        }
        if (versionRange != null && !versionRange.toString().equals("[0.0.0,infinity)")) {
            isVersionrangePresent = true;
        }

        if (isNamePresent || isTypePresent || isExtrafuncPresent || isVersionrangePresent) {
            sb.append("(&");
        }
        sb.append("(").append(requirementName).append("=");
        sb.append(value);
        sb.append(")");

        if (isNamePresent) {
            sb.append("(name=");
            sb.append(name);
            sb.append(")");
        }

        if (isTypePresent) {
            sb.append("(type=");
            sb.append(type);
            sb.append(")");
        }

        if (isVersionrangePresent) {
            appendVersion(sb, versionRange);
        }

        if (isExtrafuncPresent) {
            sb.append("(extrafunc=");
            sb.append(extrafunc);
            sb.append(")");
        }

        if (isNamePresent || isTypePresent || isExtrafuncPresent || isVersionrangePresent) {
            sb.append(")");
        }

        req.setFilter(sb.toString());

        String comment = BundleMetadata.REQUIRE_TYPES + ": " + value;
        if (isNamePresent) {
            comment += ";name=\"" + name + "\"";
        }
        if (isTypePresent) {
            comment += ";type=\"" + type + "\"";
        }
        if (isVersionrangePresent) {
            comment += ";versionrange=\"" + versionRange + "\"";
        }
        if (isExtrafuncPresent) {
            comment += ";extrafunc=\"" + extrafunc + "\"";
        }

        req.setComment(comment);
        req.setExtend(false);
        req.setMultiple(false);
        req.setOptional(isOptional);
    }

    /**
     * 
     * @param filter
     * @param version
     */
    private void appendVersion(StringBuffer filter, VersionRange version) {
        Version emptyVersion = new Version("0.0.0");
        Version infinityVersion = new Version(Integer.MAX_VALUE + "." + Integer.MAX_VALUE + "." + Integer.MAX_VALUE);

        if (version != null) {
            if (version.getFloorVersion() != null && !emptyVersion.equals(version.getFloorVersion())) {
                if (version.isIncludingFloor()) {
                    filter.append("(");
                    filter.append("version");
                    filter.append(">=");
                    filter.append(version.getFloorVersion());
                    filter.append(")");
                } else {
                    {
                        filter.append("(!(");
                        filter.append("version");
                        filter.append("<=");
                        filter.append(version.getFloorVersion());
                        filter.append("))");
                    }
                }
            }
            if (version.getCeilingVersion() != null && !infinityVersion.equals(version.getCeilingVersion())) {
                if (version.isIncludingCeiling()) {
                    filter.append("(");
                    filter.append("version");
                    filter.append("<=");
                    filter.append(version.getCeilingVersion());
                    filter.append(")");
                } else {
                    filter.append("(!(");
                    filter.append("version");
                    filter.append(">=");
                    filter.append(version.getCeilingVersion());
                    filter.append("))");
                }
            }
        }
    }

    // TODO move to outer class
    private class JarManifestExtractor {

        private InputStream manifest;

        public JarManifestExtractor(InputStream is) {
            JarInputStream jis = null;

            try {
                jis = new JarInputStream(is);

                while (true) {
                    JarEntry ent = jis.getNextJarEntry();
                    if (ent == null) {
                        break;
                    }

                    if (ent.isDirectory()) {
                        continue;
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];

                    while (true) {
                        int l = jis.read(buffer);
                        if (l <= 0) {
                            break;
                        }
                        baos.write(buffer, 0, l);
                    }

                    if (JarFile.MANIFEST_NAME.equals(ent.getName().toUpperCase())) {
                        manifest = new ByteArrayInputStream(baos.toByteArray());
                        break;
                    }
                }

            } catch (Throwable e) {

                logger.error("I/O error {}", e.getMessage() , e);

            } finally {
                try {
                    if (jis != null) {
                        jis.close();
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable e) {
                    logger.error("I/O error {}", e.getMessage(), e);
                }
            }
        }

        public InputStream getManifest() {
            return manifest;
        }
    }
}