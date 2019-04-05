package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import cz.zcu.kiv.crce.crce_component_collection.api.ExportCollectionServiceApi;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
import cz.zcu.kiv.crce.crce_component_collection.internal.Activator;
import cz.zcu.kiv.crce.metadata.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportCollectionService implements ExportCollectionServiceApi {
    private CollectionService collectionService = new CollectionService();
    private HelperFileWriter helperFileWriter = new HelperFileWriter();
    private HelperResource helperResource;

     public ExportCollectionService(){
         helperResource = new HelperResource(Activator.instance().getMetadataService());
     }

    @Override
    public boolean exportCollection(String idCollection, File path, String idRepository, LimitRange range, boolean details) {
        if(!path.exists()){
            try{
                path.mkdirs();
            }
            catch(Exception ex){
                ex.printStackTrace();
                return false;
            }
        }
        CollectionDetailBean collectionDetailBean = collectionService.getCollectionComponentDetail(idCollection);
        if(collectionDetailBean != null) {
            File nextPath = new File(path + File.separator + collectionDetailBean.getName() +
                    "-" + collectionDetailBean.getVersion());
            if(!nextPath.exists()){
                try{
                    nextPath.mkdirs();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    return false;
                }
            }
            //save parameters collection to xml file
            File parametersXml = new File(nextPath + File.separator + "parameters-" + collectionDetailBean.getName() +
                    "-" + collectionDetailBean.getVersion() + ".xml");
            try {
                helperFileWriter.createParametersXmlFile(parametersXml, collectionDetailBean.getParameters());
            }
            catch (IOException ex){
                ex.printStackTrace();
                return false;
            }

            // export artifact whit range
            for (String s :collectionDetailBean.getRangeArtifacts()){
                String[] pom = s.split("=");
                if(range.equals(LimitRange.MAX)){
                    Resource resource = helperResource.getResourceMaxVersionFromStore(pom[0], idRepository, pom[1]);
                    if(resource != null){
                        // export resource
                        File src = new File(helperResource.getUri(resource));
                        try{
                            Path copied = Paths.get(nextPath + File.separator + helperResource.getFileName(resource));
                            Path originalPath = src.toPath();
                            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                            File artifactMetadataFile  = new File(nextPath + File.separator + "metadata-"
                                    + helperResource.getFileName(resource) + ".xml");
                            helperFileWriter.createArtifactMetadataXmlFile(artifactMetadataFile, resource, details);
                        }
                        catch(IOException ex){
                            ex.printStackTrace();
                            return false;
                        }
                    }
                }
                // export min artifact available in store (range.equals(LimitRange.MIN))
                else {
                    Resource resource = helperResource.getResourceMinVersionFromStore(pom[0], idRepository, pom[1]);
                    if(resource != null){
                        // export resource
                        File src = new File(helperResource.getUri(resource));
                        try{
                            Path copied = Paths.get(nextPath + File.separator + helperResource.getFileName(resource));
                            Path originalPath = src.toPath();
                            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                            File artifactMetadataFile  = new File(nextPath + File.separator + "metadata-"
                                    + helperResource.getFileName(resource) + ".xml");
                            helperFileWriter.createArtifactMetadataXmlFile(artifactMetadataFile, resource, details);
                        }
                        catch(IOException ex){
                            ex.printStackTrace();
                            return false;
                        }
                    }
                }
            }

            for(String s : collectionDetailBean.getSpecificArtifacts()){
                exportCollection(s, nextPath, idRepository, range, details);
            }
        }
        // artefact in store
        else{
            Resource resource = helperResource.getResourceFromStore(idRepository, idCollection);
            File src = new File(helperResource.getUri(resource));
            try{
                Path copied = Paths.get(path + File.separator + helperResource.getFileName(resource));
                Path originalPath = src.toPath();
                Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                File artifactMetadataFile  = new File(path + File.separator + "metadata-"
                        + helperResource.getFileName(resource) + ".xml");
                helperFileWriter.createArtifactMetadataXmlFile(artifactMetadataFile, resource, details);
            }
            catch(IOException ex){
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        FileInputStream fis = null;
        try {
            if (fileToZip.isHidden()) {
                return;
            }
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith(File.separator)) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                    zipOut.closeEntry();
                }
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
                }
                return;
            }
            fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}
