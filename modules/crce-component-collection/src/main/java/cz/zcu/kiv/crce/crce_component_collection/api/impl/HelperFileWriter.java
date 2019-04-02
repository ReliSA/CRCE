package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import cz.zcu.kiv.crce.crce_component_collection.internal.Activator;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HelperFileWriter {
        BufferedWriter bufferedWriter = null;
        public void createParametersXmlFile(File file, List<String> parameters) throws IOException {
            try{
                if(!file.exists() && !file.createNewFile()){
                    throw new IOException();
                }

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                bufferedWriter.newLine();
                bufferedWriter.write("<parameters>");
                bufferedWriter.newLine();
                for(String s : parameters){
                    bufferedWriter.write("\t<parameter>");
                    bufferedWriter.newLine();
                    bufferedWriter.write("\t\t" + s);
                    bufferedWriter.newLine();
                    bufferedWriter.write("\t</parameter>");
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("</parameters>");
                bufferedWriter.newLine();
            }
            catch(IOException e){
                e.printStackTrace();
                throw new IOException();
            }
            finally {
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
            }
        }

        public void createArtifactMetadataXmlFile(File file, Resource resource) throws  IOException{
            HelperResource resourceService = new HelperResource(resource, Activator.instance().getMetadataService());
            try{
                if(!file.exists() && !file.createNewFile()){
                    throw new IOException();
                }

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                bufferedWriter.newLine();
                bufferedWriter.write("<properties>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t<repository_id>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t\t" + resource.getId());
                bufferedWriter.newLine();
                bufferedWriter.write("\t</repository_ID>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t<symbolic_name>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t\t" + resourceService.getSymbolicName());
                bufferedWriter.newLine();
                bufferedWriter.write("\t</symbolic_name>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t<presentation_name>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t\t" + resourceService.getPresentationName());
                bufferedWriter.newLine();
                bufferedWriter.write("\t</presentation_name>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t<version>");
                bufferedWriter.newLine();
                bufferedWriter.write("\t\t" + resourceService.getVersion());
                bufferedWriter.newLine();
                bufferedWriter.write("\t</version>");
                bufferedWriter.newLine();
                bufferedWriter.write("</properties>");
                bufferedWriter.newLine();
                bufferedWriter.write("<requirements>");
                bufferedWriter.newLine();
                for(Requirement requirement : resource.getRequirements()){
                    for(Attribute<?> a : requirement.getAttributes()){
                        bufferedWriter.write("\t<requirement>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<namespace>");
                        bufferedWriter.write(requirement.getNamespace());
                        bufferedWriter.write("</namespace>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<designation>");
                        bufferedWriter.write(a.getName());
                        bufferedWriter.write("</designation>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<type>");
                        bufferedWriter.write(a.getType().toString());
                        bufferedWriter.write("</type>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<value>");
                        bufferedWriter.write(a.getStringValue());
                        bufferedWriter.write("</value>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t<requirement>");
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.write("</requirements>");
                bufferedWriter.newLine();

                bufferedWriter.write("<capabilities>");
                bufferedWriter.newLine();
                for(Capability capability : resource.getCapabilities()){
                    for(Attribute<?> a : capability.getAttributes()){
                        bufferedWriter.write("\t<capability>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<namespace>");
                        bufferedWriter.write(capability.getNamespace());
                        bufferedWriter.write("</namespace>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<designation>");
                        bufferedWriter.write(a.getName());
                        bufferedWriter.write("</designation>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<type>");
                        bufferedWriter.write(a.getType().toString());
                        bufferedWriter.write("</type>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t\t<value>");
                        bufferedWriter.write(a.getStringValue());
                        bufferedWriter.write("</value>");
                        bufferedWriter.newLine();

                        bufferedWriter.write("\t<capability>");
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.write("</capabilities>");
                bufferedWriter.newLine();
            }
            catch(IOException e){
                e.printStackTrace();
                throw new IOException();
            }
            finally {
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
            }
        }
}
