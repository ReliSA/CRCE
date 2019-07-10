package cz.zcu.kiv.crce.crce_component_collection.api.impl;

import cz.zcu.kiv.crce.metadata.*;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * The class methods export the set parameters and the xml artifact metadata to the file system.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class HelperFileWriter {
        BufferedWriter bufferedWriter = null;
        /**
        * A method for exporting artifact set parameters.
        */
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

    /**
     * Method to export file metadata in xml format. Maybe an implementation using the Response Web Service object.
     */
        public void createArtifactMetadataXmlFile(File file, Resource resource, boolean details) throws  IOException{
            try{
                if(!file.exists() && !file.createNewFile()){
                    throw new IOException();
                }

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                bufferedWriter.newLine();
                bufferedWriter.write("<resource uuid=\"" + resource.getId() + "\">");
                bufferedWriter.newLine();
                for(Capability c : resource.getCapabilities()) {
                    if(details || c.getNamespace().equals("osgi.identity") || c.getNamespace().equals("crce.identity")){
                        bufferedWriter.write("\t<capability uuid=\"" + c.getId() + "\" namespace=\"" + c.getNamespace() + "\">");
                        bufferedWriter.newLine();
                        for (Attribute<?> a : c.getAttributes()) {
                            bufferedWriter.write("\t\t<attribute value=\"" + a.getValue());
                            if(!a.getType().toString().equals("")){
                                bufferedWriter.write("\" type=\"" + a.getType());
                            }
                            if(!a.getName().equals("")){
                                bufferedWriter.write("\" name=\"" + a.getName() +  "\"");
                            }
                            bufferedWriter.write("/>");
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.write("\t</capability>");
                        bufferedWriter.newLine();
                    }
                }
                if(details){
                    for(Requirement r : resource.getRequirements()) {
                        bufferedWriter.write("\t<requirement uuid=\"" + r.getId() + "\" namespace=\"" + r.getNamespace() + "\">");
                        bufferedWriter.newLine();
                        for (Attribute<?> a : r.getAttributes()) {
                            bufferedWriter.write("\t\t<attribute value=\"" + a.getValue());
                            if (!a.getType().toString().equals("")) {
                                bufferedWriter.write("\" type=\"" + a.getType());
                            }
                            if (!a.getName().equals("")) {
                                bufferedWriter.write("\" name=\"" + a.getName() + "\"");
                            }
                            bufferedWriter.write("/>");
                            bufferedWriter.newLine();
                        }
                        for (Map.Entry<String, String> entry : r.getDirectives().entrySet()) {
                            bufferedWriter.write("\t\t<directive value=\"" + entry.getValue()
                                    .replace("\"", "&quot;") + "\" name=\"" + entry.getKey() + "\"/>");
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.write("\t</requirement>");
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.write("</resource>");
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

            /*Client client = ClientBuilder.newClient();
            Response response = client.target("http://localhost:8080/rest/v2/metadata/" + resource.getId())
                    .request(MediaType.TEXT_PLAIN_TYPE)
                    .get();

            try{
                if(!file.exists() && !file.createNewFile()){
                    throw new IOException();
                }
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1));
                //bufferedWriter.write(response.getStatus());
                //bufferedWriter.write(response.getHeaders().toString());
                bufferedWriter.write(response.readEntity(String.class));
            }
            catch(IOException e){
                e.printStackTrace();
                throw new IOException();
            }
            finally {
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
            }*/
        }
}
