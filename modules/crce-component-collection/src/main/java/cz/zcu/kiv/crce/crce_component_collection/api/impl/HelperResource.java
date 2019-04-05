package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import cz.zcu.kiv.crce.crce_component_collection.internal.Activator;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class HelperResource {
    private Resource resource;
    private transient MetadataService metadataService;

    public HelperResource(MetadataService metadataService){
        this.metadataService = metadataService;
    }

    public HelperResource(Resource resource, MetadataService metadataService){
        this.resource = resource;
        this.metadataService = metadataService;
    }

    public void setResource(Resource resource){
        this.resource = resource;
    }

    public String getPresentationName() {
        return metadataService.getPresentationName(resource);
    }

    public String getSymbolicName() {
        String name = "unknown-symbolic-name";
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            name = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
        }
        return name;
    }

    public Version getVersion() {
        Version version = null;
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            version = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
        }
        return version;
    }

    public long getSize() {
        return metadataService.getSize(resource);
    }

    public URI getUri(Resource resource) {
        return metadataService.getUri(resource);
    }

    public String getFileName(Resource resource) {
        return metadataService.getFileName(resource);
    }

    public Resource getResourceFromStore(String repositoryId, String uid){
        Resource resource = Activator.instance().getStore(getRepositoryId(repositoryId)).getResource(uid,true);
        return resource;
    }

    private Version getVersion(Resource resource) {
        Version version = null;
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            version = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
        }
        return version;
    }

    private String getSymbolicName(Resource resource) {
        String name = "unknown-symbolic-name";
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            name = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
        }
        return name;
    }

    public Resource getResourceMaxVersionFromStore(String searchSymbolicName, String repositoryId, String range){
        Resource resourceFindMax = null;
        Version maxVersionAvailableInStore = null;
        String[] pom = range.replace("[","").replace("]","").split(",");
        Version versionMin = new Version(pom[0]);
        Version versionMax = new Version(pom[1]);

        for(Resource resource : Activator.instance().getStore(getRepositoryId(repositoryId)).getResources()) {
            Version resourceVersion = getVersion(resource);
            String resourceSymbolicName = getSymbolicName(resource);
            if (resourceSymbolicName.equals(searchSymbolicName) &&
                    compareVersion(versionMin, resourceVersion, versionMax)) {
                if (resourceFindMax == null) {
                    resourceFindMax = resource;
                    maxVersionAvailableInStore = resourceVersion;
                }
                else if (maxVersionAvailableInStore.getMajor() < resourceVersion.getMajor()) {
                    resourceFindMax = resource;
                    maxVersionAvailableInStore = resourceVersion;
                }
                else if(maxVersionAvailableInStore.getMajor() == resourceVersion.getMajor()) {
                    if(maxVersionAvailableInStore.getMinor() < resourceVersion.getMinor()){
                        resourceFindMax = resource;
                        maxVersionAvailableInStore = resourceVersion;
                    }
                    else if(maxVersionAvailableInStore.getMinor() == resourceVersion.getMinor() &&
                            maxVersionAvailableInStore.getMicro() < resourceVersion.getMicro()){
                            resourceFindMax = resource;
                            maxVersionAvailableInStore = resourceVersion;
                    }
                }
            }
        }
        return resourceFindMax;
    }

    public Resource getResourceMinVersionFromStore(String searchSymbolicName, String repositoryId, String range){
        Resource resourceFindMin = null;
        Version minVersionAvailableInStore = null;
        String[] pom = range.replace("[","").replace("]","").split(",");
        Version versionMin = new Version(pom[0]);
        Version versionMax = new Version(pom[1]);

        for(Resource resource : Activator.instance().getStore(getRepositoryId(repositoryId)).getResources()) {
            Version resourceVersion = getVersion(resource);
            String resourceSymbolicName = getSymbolicName(resource);
            if (resourceSymbolicName.equals(searchSymbolicName) &&
                    compareVersion(versionMin, resourceVersion, versionMax)) {
                if (resourceFindMin == null) {
                    resourceFindMin = resource;
                    minVersionAvailableInStore = resourceVersion;
                }
                else if (minVersionAvailableInStore.getMajor() > resourceVersion.getMajor()) {
                    resourceFindMin = resource;
                    minVersionAvailableInStore = resourceVersion;
                }
                else if(minVersionAvailableInStore.getMajor() == resourceVersion.getMajor()) {
                    if(minVersionAvailableInStore.getMinor() > resourceVersion.getMinor()){
                        resourceFindMin = resource;
                        minVersionAvailableInStore = resourceVersion;
                    }
                    else if(minVersionAvailableInStore.getMinor() == resourceVersion.getMinor() &&
                            minVersionAvailableInStore.getMicro() > resourceVersion.getMicro()){
                        resourceFindMin = resource;
                        minVersionAvailableInStore = resourceVersion;

                    }
                }
            }
        }
        return resourceFindMin;
    }

    private boolean compareVersion(Version min, Version custom, Version max){
       if(min.getMajor() == max.getMajor() && min.getMajor() == custom.getMajor()){
           if(min.getMinor() == max.getMinor() && min.getMinor() == custom.getMinor()){
               if(min.getMicro() == max.getMicro() && min.getMicro() == custom.getMicro()){
                   return true;
               }
               else if(min.getMicro() <= custom.getMicro() && custom.getMicro() < max.getMicro()){
                   return true;
               }
               else {
                   return false;
               }
           }
           else if (min.getMinor() == custom.getMinor()){
               if(min.getMicro() <= custom.getMicro()){
                   return true;
               }
               else{
                   return false;
               }
           }
           else if(custom.getMinor() == max.getMinor()){
               if(custom.getMicro() < max.getMicro()){
                   return true;
               }
               else {
                   return false;
               }
           }
           else if(min.getMinor() < custom.getMinor() && custom.getMinor() < max.getMinor()){
               return true;
           }
           else {
               return false;
           }
       }
       else if(min.getMajor() == custom.getMajor()){
           if(min.getMinor() < custom.getMinor()){
               return true;
           }
           else if(min.getMinor() > custom.getMinor()){
               return false;
           }
           //(min == custom)
           else{
               if(min.getMicro() <= custom.getMicro()){
                    return true;
               }
               else {
                   return false;
               }
           }
       }
       else if(custom.getMajor() == max.getMajor()){
           if(custom.getMinor() < max.getMinor()){
               return true;
           }
           else if(custom.getMinor() > max.getMinor()){
               return false;
           }
           //(custom == max)
           else{
               if(custom.getMicro() < max.getMicro()){
                   return true;
               }
               else{
                   return false;
               }
           }
       }
       else if(min.getMajor() < custom.getMajor() && custom.getMajor() < max.getMajor()){
           return true;
       }
       else{
           return false;
       }
    }

    private String getRepositoryId(String repositoryId) {
        if (repositoryId == null) {
            Map<String, String> stores = Activator.instance().getRepositories();
            if (stores.isEmpty()) {
                return null;
            }
            repositoryId = stores.keySet().iterator().next();
        }
        return repositoryId;
    }

}
