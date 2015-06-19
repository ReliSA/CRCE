package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpoint;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointParameter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>This class can recognize and parse remote IDL documents representing WADL (Web Application Description Language)
 * (see <a href="http://www.w3.org/Submission/wadl/#x3-70002.2">http://www.w3.org/Submission/wadl/#x3-70002.2</a>).
 * 
 * <p>Parsed WADL description object describing webservice is then represented by {@link cz.zcu.kiv.crce.metadata.Capability} returned in a
 * {@link java.util.List} with a single item.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWadl extends WebserviceTypeBase implements WebserviceType {

    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);
    
    private static final String WADL_WEBSERVICE_NAME = "REST webservice"; // WADL does not define any WS name, so we use a default generic one
    private static final String WADL_MIME = "application/wadl+xml";
    
    private static final String WADL_APPLICATION = "application";
    private static final String WADL_RESOURCES = "resources";
    private static final String WADL_RESOURCE = "resource";
    private static final String WADL_RESOURCE_PARAM = "param";
    private static final String WADL_RESOURCE_METHOD = "method";
    private static final String WADL_RESOURCE_METHOD_REQUEST = "request";
    private static final String WADL_RESOURCE_METHOD_RESPONSE = "response";
    
    /**
     * Constructor
     *
     * @param mf
     * @param ms
     */
    public WebserviceTypeWadl(MetadataFactory mf, MetadataService ms) {
        super(mf, ms);
    }
    
    @Override
    public String getSpecificIdlCategory() {
        return "wadl";
    }

    @Override
    public String getSpecificWebserviceType() {
        return "rest";
    }

    @Override
    public boolean recognizeIDL(String idl) {
        
        // check whether IDL is a valid XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(idl)));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.debug("IDL is not a valid XML object", ex);
        }
        if (document == null) {
            return false;
        }
        
        // check whether IDL has a valid WSDL structure
        Element root = document.getDocumentElement();
        stripOfNamespaceRecursive(root);
        if (!root.getNodeName().equalsIgnoreCase(WADL_APPLICATION)) {
            logger.debug("IDL is not a valid WADL. Does not have a root element \"{}\"", WADL_APPLICATION);
            return false;
        }
        
        // check whether WSDL has at least one resource set defined
        if (root.getElementsByTagName(WADL_RESOURCES).getLength() < 1) {
            logger.debug("WADL does not have any \"{}\" elements defined i.e. no webservice IDL endpoints to parse.", WADL_RESOURCES);
            return false;
        }
        
        return true;
    }

    @Override
    public List<Resource> parseIDL(String idl) {
        
        ////////////////////////////////////////////
        // process idl and get all necessary info //
        ////////////////////////////////////////////
        
        // check whether IDL is a valid XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(idl)));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.debug("IDL is not a valid XML object", ex);
        }
        if (document == null) {
            return null;
        }
        
        // check whether IDL has a valid WSDL structure
        Element root = document.getDocumentElement();
        stripOfNamespaceRecursive(root);
        if (!root.getNodeName().equalsIgnoreCase(WADL_APPLICATION)) {
            logger.debug("IDL is not a valid WADL. Does not have a root element \"{}\"", WADL_APPLICATION);
            return null;
        }
        
        // TODO WADL can define multiple "method", "representation" and "param" elements in its root element and then point to these abstract elements from
        // particular ones (see http://www.w3.org/Submission/wadl/#x3-70002.2). Process those elements and then use them while parsing concrete representations.
        
        // process all resource sets defined in this WADL
        List<WebserviceEndpoint> processedEndpoints = new ArrayList<>();
        NodeList resourceSets = root.getElementsByTagName(WADL_RESOURCES);
        for (int i = 0; i < resourceSets.getLength(); i++) {
            Node resourceSet = resourceSets.item(i);
            String resourceSetBaseUrl = resourceSet.getAttributes().getNamedItem("base").getNodeValue();
            NodeList resources = resourceSet.getChildNodes();
            processWadlResourcesRecursive(resources, resourceSetBaseUrl, processedEndpoints);
        }
        
        ////////////////////////////////////////////////////////////////////////////
        // create CRCE metadata structures and fill it by retrieved info from idl //
        ////////////////////////////////////////////////////////////////////////////
        
        // create new resource and varible for holding reference to current capability
        Resource resource = metadataFactory.createResource();
        Capability capability;
        
        // Capability - CRCE Identity
        capability = metadataService.getIdentity(resource);
        metadataService.addCategory(resource, getSpecificIdlCategory()); // add specific category for this type of web service
        metadataService.setPresentationName(resource, WADL_WEBSERVICE_NAME);
        metadataService.setSize(resource, idl.length());
        capability.setAttribute(ATTRIBUTE__CRCE_IDENTITY__MIME, WADL_MIME);
        capability.setAttribute(ATTRIBUTE__CRCE_IDENTITY__HASH, getIdlHash(idl));
        
        // Capability - Webservice Identity
        capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICE_IDENTITY);
        capability.setAttribute(ATTRIBUTE__WEBSERVICE_IDENTITY__TYPE, getSpecificWebserviceType());
        resource.addCapability(capability);
        resource.addRootCapability(capability);
        
        // Capabilities - Webservice Endpoint
        for (int i = 0; i < processedEndpoints.size(); i++) {
            capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICE_ENDPOINT);
            setIfSet(capability, ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME, processedEndpoints.get(i).getName());
            setIfSet(capability, ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, processedEndpoints.get(i).getUrl());
            
            // Properties - Webservice Enpoint Parameter
            List<WebserviceEndpointParameter> processedParams = processedEndpoints.get(i).getParameters();
            for (int j = 0; j < processedParams.size(); j++) {
                Property property = metadataFactory.createProperty(NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER);
                setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME, processedParams.get(j).getName());
                setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE, processedParams.get(j).getType());
                if (processedParams.get(j).isOptional() != null) {
                    setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL, (long)(processedParams.get(j).isOptional() ? 1 : 0));
                }
                capability.addProperty(property);
            }
            
            resource.addCapability(capability);
            resource.addRootCapability(capability);
        }
        
        // return this single resource in a list (WADL description document can describe only one webservice at once)
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        return resources;
    }
    
    /**
     * Each WADL "resource" element can contain arbitrary number of "resource", "method" and "param" elements. This function process this structure recursively
     * (by processing "resource" elements nested to each other) and creates {@link cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpoint} from every
     * "method" element along the way with parameters as all known "params" elements known at a current scope. 
     * 
     * @param resources Represent set of "resource", "method" and "param" elements on each level of recursion.
     * @param baseUrl With each recursive call this parameter gets longer. It is a path used for all "method" elements and it's pieces are defined
     * hierarchically in parent "resource" elements.
     * @param processedEndpoints Passed {@link java.util.List} of {@link cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpoint} discovered so far.
     */
    private void processWadlResourcesRecursive(NodeList resources, String baseUrl, List<WebserviceEndpoint> processedEndpoints) {
        
        // process all parameters, methods and sub-resources defined in this resource
        List<WebserviceEndpointParameter> processedResourceParameters = new ArrayList<>();
        for (int j = 0; j < resources.getLength(); j++) {
            Node node = resources.item(j);
            if (node.getNodeName().equalsIgnoreCase(WADL_RESOURCE)) {
                
                // process sub-resource
                String resourcePath = node.getAttributes().getNamedItem("path").getNodeValue();
                NodeList subnodes = node.getChildNodes();
                processWadlResourcesRecursive(subnodes, baseUrl + resourcePath, processedEndpoints);
                
            } else if (node.getNodeName().equalsIgnoreCase(WADL_RESOURCE_PARAM)) {

                // process parameter
                processedResourceParameters.add(processParameter(node.getAttributes()));

            } else if (node.getNodeName().equalsIgnoreCase(WADL_RESOURCE_METHOD)) {

                // process method
                NamedNodeMap resourceAttributes = node.getAttributes();
                String methodId = null;
                if (resourceAttributes.getNamedItem("id") != null) {
                    methodId = resourceAttributes.getNamedItem("id").getNodeValue();
                }
                String methodName = null;
                if (resourceAttributes.getNamedItem("name") != null) {
                    methodName = resourceAttributes.getNamedItem("name").getNodeValue();
                }

                // process all requests and response of this method
                List<WebserviceEndpointParameter> processedMethodParameters = new ArrayList<>();
                NodeList subsubnodes = node.getChildNodes();
                for (int l = 0; l < subsubnodes.getLength(); l++) {
                    Node subsubnode = subsubnodes.item(l);

                    if (subsubnode.getNodeName().equalsIgnoreCase(WADL_RESOURCE_METHOD_REQUEST)) {

                        // proces request
                        NodeList params = subsubnode.getChildNodes();

                        // process all aditional params of this method request
                        for (int m = 0; m < params.getLength(); m++) {
                            Node param = params.item(m);
                            if (param.getNodeName().equalsIgnoreCase("param")) {
                                NamedNodeMap paramAttributes = param.getAttributes();

                                // process parameter
                                processedMethodParameters.add(processParameter(paramAttributes));
                            }
                        }

                    } else if (subsubnode.getNodeName().equalsIgnoreCase(WADL_RESOURCE_METHOD_REQUEST)) {
                        // process response
                        // TODO WADL method can define multiple responses, each with multiple representations, refactoring will be needed in order to represent them.
                    }
                }

                // create new endpoint
                String endpointName;
                methodName = "(" + methodName + ")";
                if (methodId == null) {
                    endpointName = methodName;
                } else {
                    endpointName = methodId + " " + methodName;
                }
                String endpointUrl = baseUrl;
                processedMethodParameters.addAll(processedResourceParameters);
                processedEndpoints.add(new WebserviceEndpoint(endpointName, endpointUrl, processedMethodParameters, null));

            }
        }
        
    }
    
    /**
     * Processes attributes of WADL "param" element into {@link cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointParameter}.
     * 
     * @param nodeAttributes Attributes of WADL "param" element.
     * @return 
     */
    private WebserviceEndpointParameter processParameter(NamedNodeMap nodeAttributes) {
        String paramName = null;
        if (nodeAttributes.getNamedItem("name") != null) {
            paramName = nodeAttributes.getNamedItem("name").getNodeValue();
        }
        String paramType = null;
        if (nodeAttributes.getNamedItem("type") != null) {
            paramType = nodeAttributes.getNamedItem("type").getNodeValue();
        }
        Boolean paramRequired = null;
        if (nodeAttributes.getNamedItem("required") != null) {
            if (nodeAttributes.getNamedItem("required").getNodeValue().equalsIgnoreCase("true")) {
                paramRequired = true;
            } else if (nodeAttributes.getNamedItem("required").getNodeValue().equalsIgnoreCase("true")) {
                paramRequired = false;
            }
        }
        return new WebserviceEndpointParameter(paramName, paramType, null, paramRequired == null ? null : !paramRequired, null);
    }

    /**
     * Removes namespace part of XML element node name. E.g. turns "tns:address" into "address".
     * 
     * @param value XML element node name.
     * @return XML element node name without namespace.
     */
    private String stripOfNamespace(String value) {
        return value.substring(value.indexOf(':') + 1);
    }
    
    /**
     * Recursively removes the namespace of a node.
     * @param node the starting node.
     */
    private void stripOfNamespaceRecursive(Node node) {
        Document document = node.getOwnerDocument();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            document.renameNode(node, null, stripOfNamespace(node.getNodeName()));
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            stripOfNamespaceRecursive(list.item(i));
        }
    }    
}
