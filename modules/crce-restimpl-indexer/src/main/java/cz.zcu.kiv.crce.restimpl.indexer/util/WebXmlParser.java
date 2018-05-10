package cz.zcu.kiv.crce.restimpl.indexer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.zcu.kiv.crce.restimpl.indexer.definition.DispatcherDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by ghessova on 08.05.2018.
 */
public class WebXmlParser {

    private final String DEF_FILE = "../crce-restimpl-indexer/config/def/dispatcher.yml";

    private DispatcherDefinition definition;

    public WebXmlParser() throws IOException {
        definition = loadDefinition();
    }

    private DispatcherDefinition loadDefinition() throws IOException{
        DispatcherDefinition definition = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File file = new File(DEF_FILE);

        definition = mapper.readValue(file, DispatcherDefinition.class);

        return definition;

    }

    public Result parseWebXml(InputStream is) {
        if (is == null) {
            return null;
        }
        // check whether IDL is a valid XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            //logger.debug("IDL is not a valid XML object", ex);
            ex.printStackTrace(); // todo log
        }
        if (document == null) {
            return null;
        }

        Element root = document.getDocumentElement();

        // go through servlets
        List<Servlet> servlets = getServlets(root);
        Map<String, List<String>> mappings = getServletMappings(root);

        for (Servlet servlet : servlets) {
            Map.Entry<String,DispatcherDefinition.Dispatcher> entry = findMatch(servlet);
            if (entry != null) {
                Result result = new Result();
                result.setUrlPrefixes(getUrlPrefixes(mappings.get(servlet.getName())));
                String providersParamName = entry.getValue().getProviders();
                result.setProviders(getProviders(servlet.initParams.get(providersParamName)));
                return result;
            }
        }
        return null;
    }

    private Set<String> getProviders(String providerString) {
        if (providerString == null) {
            return null;
        }
        String[] providers = providerString.split(",");
        Set<String> set = new HashSet<>();
        for (String provider : providers) {
            set.add(provider.trim().replaceAll("\\.", "/"));
        }
        return set;
    }

    private List<String> getUrlPrefixes(List<String> urlMappings) {
        List<String> prefixes = new ArrayList<>();
        for (String urlMapping : urlMappings) {
            if (urlMapping.endsWith("*")) {
                prefixes.add(urlMapping.replaceAll("\\*", ""));
            }
        }
        return prefixes;
    }

    private Map.Entry<String,DispatcherDefinition.Dispatcher> findMatch(Servlet servlet) {
        for (Map.Entry<String,DispatcherDefinition.Dispatcher> entry : definition.getDispatchers().entrySet()) {
            DispatcherDefinition.Dispatcher d = entry.getValue();
            if (d.getDispatcher().equals(servlet.getClassName())) {
                return entry;
            }
        }
        return null;

    }

    private List<Servlet> getServlets(Element root) {
        NodeList servletNodes = root.getElementsByTagName("servlet");
        Servlet servlet;
        List<Servlet> servlets = new ArrayList<>();
        for (int i = 0; i < servletNodes.getLength(); i++) {
            Node resourceSet = servletNodes.item(i);
            NodeList childNodes = resourceSet.getChildNodes();
            servlet = new Servlet();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node node = childNodes.item(j);
                if ("servlet-name".equals(node.getNodeName())) {
                    servlet.setName(getTextContent(node));
                }
                else if ("servlet-class".equals(node.getNodeName())) {
                    servlet.setClassName(getTextContent(node));
                }
                else if ("init-param".equals(node.getNodeName())) {
                    NodeList initParamChildren = node.getChildNodes();
                    String paramName = null;
                    String paramValue = null;
                    for (int k = 0; k < initParamChildren.getLength(); k++) {
                        Node paramNode = initParamChildren.item(k);
                        if ("param-name".equals(paramNode.getNodeName())) {
                           paramName = getTextContent(paramNode);
                        }
                        else if ("param-value".equals(paramNode.getNodeName())) {
                            paramValue = getTextContent(paramNode);
                        }
                    }
                    servlet.initParams.put(paramName, paramValue);
                }
            }
            servlets.add(servlet);
        }
        return servlets;
    }

    private String getTextContent(Node node) {
        return node.getFirstChild().getTextContent().trim();
    }

    private Map<String, List<String>> getServletMappings(Element root) {
        NodeList servletNodes = root.getElementsByTagName("servlet-mapping");
        Map<String, List<String>> mapping = new HashMap<>();
        for (int i = 0; i < servletNodes.getLength(); i++) {
            Node resourceSet = servletNodes.item(i);
            NodeList childNodes = resourceSet.getChildNodes();
            String name = null;
            String url = null;
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node node = childNodes.item(j);
                if ("servlet-name".equals(node.getNodeName())) {
                    name = getTextContent(node);
                }
                else if ("url-pattern".equals(node.getNodeName())) {
                    url = getTextContent(node);
                }

            }
            if (mapping.containsKey(name)) {
                mapping.get(name).add(url);
            }
            else {
                List<String> urls = new ArrayList<>();
                urls.add(url);
                mapping.put(name, urls);
            }
        }
        return mapping;
    }

    class Servlet {
        private String name;
        private String className;
        private Map<String, String> initParams = new HashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

    }

    public static class Result {
        private Set<String> providers;
        private List<String> urlPrefixes;

        public Set<String> getProviders() {
            return providers;
        }

        public void setProviders(Set<String> providers) {
            this.providers = providers;
        }

        public List<String> getUrlPrefixes() {
            return urlPrefixes;
        }

        public void setUrlPrefixes(List<String> urlPrefixes) {
            this.urlPrefixes = urlPrefixes;
        }
    }
}
