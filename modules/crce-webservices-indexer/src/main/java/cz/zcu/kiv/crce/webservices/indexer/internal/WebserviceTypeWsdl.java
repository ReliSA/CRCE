package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.webservices.indexer.structures.Webservice;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpoint;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointParameter;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointResponse;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlBindedOperation;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlBinding;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlInterface;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlMessage;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlOperation;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlPart;
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
 * <p>This class can recognize and parse remote IDL documents representing WSDL (Web Services Description Language)
 * (see <a href="http://www.w3.org/TR/wsdl20/">http://www.w3.org/TR/wsdl20/</a>).
 * 
 * <p>Parsed WSDL description object describing webservices is then represented by {@link cz.zcu.kiv.crce.metadata.Capability} returned in a
 * {@link cz.zcu.kiv.crce.metadata.Resource} for each webservice description in the IDL.
 *
 * <p>Following aspects of IDL are parsed by this class and stored in CRCE repository as meta-data to the IDL document. Everything that is not explicitly
 * mentioned here is not being processed by this class.
 * 
 * <p>IDL aspects parsed by this class:
 * <ul>
 *     <li>WSDL version</li>
 *     <li>name of all webservices ("name" property of "service" element)</li>
 *     <li>webservice endpoints (each to it's corresponding webservice)</li>
 *     <ul>
 *         <li>endpoint name</li>
 *         <li></li>
 *         <li>parameters (for each parameter it's name, type and order is being processed)</li>
 *         <li>type of returned response (in document (i.e. messaging) communication style, output message can generally contain multiple parts, for now only
 *         first one is being grabbed)</li>
 *     </ul>
 * </ul>
 * 
 * <p>Almost entire WSDL document is being processed (except "types" section, "fault" elements and several attributes of "input" and "output" elements), but
 * due to the unified representation of webservice descriptions in CRCE repository only some of that information is being stored. For details see the code in
 * this class.
 * 
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdl extends WebserviceTypeBase implements WebserviceType {

    private static final Logger logger = LoggerFactory.getLogger(WebserviceDescriptionResourceIndexer.class);
    
    private static final String WSDL_V_1_1 = "1.1";
    private static final String WSDL_V_2_0 = "2.0";
    
    private static final String WSDL_MIME = "application/wsdl+xml";
    
    private static final String WSDL_DESCRIPTION_V_1_1 = "definitions";
    private static final String WSDL_DESCRIPTION_V_2_0 = "description";
    private static final String WSDL_MESSAGE = "message";
    private static final String WSDL_SERVICE_INTERFACE_V_1_1 = "portType";
    private static final String WSDL_SERVICE_INTERFACE_V_2_0 = "interface";
    private static final String WSDL_BINDING = "binding";
    private static final String WSDL_SERVICE = "service";
    private static final String WSDL_SERVICE_ENDPOINT_V_1_1 = "port";
    private static final String WSDL_SERVICE_ENDPOINT_V_2_0 = "endpoint";
    
    /**
     * Constructor
     *
     * @param mf
     * @param ms
     */
    public WebserviceTypeWsdl(MetadataFactory mf, MetadataService ms) {
        super(mf, ms);
    }
    
    @Override
    public String getSpecificIdlCategory() {
        return "wsdl";
    }

    @Override
    public String getSpecificWebserviceType() {
        return "rpc/messaging";
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
        if (!root.getNodeName().equalsIgnoreCase(WSDL_DESCRIPTION_V_1_1) && !root.getNodeName().equalsIgnoreCase(WSDL_DESCRIPTION_V_2_0)) {
            logger.debug("IDL is not a valid WSDL. Does not have a root element \"{}\" or \"{}\"", WSDL_DESCRIPTION_V_1_1, WSDL_DESCRIPTION_V_2_0);
            return false;
        }
        
//        // check whether WSDL has at least one service defined
//        if (root.getElementsByTagName(WSDL_SERVICE).getLength() < 1) {
//            logger.debug("WSDL does not have any \"{}\" elements defined i.e. no webservice IDLs to parse.", WSDL_SERVICE);
//            return false;
//        }
        
        return true;
    }

    @Override
    public int parseIDL(String idl, Resource resource) {
        
        ////////////////////////////////////////////
        // process idl and get all necessary info //
        ////////////////////////////////////////////
        
        // process IDL into XML DOM object
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
            return -1;
        }
        
        // get root WSDL element and thus detect WSDL version
        Element root = document.getDocumentElement();
        String rootWsdlVersion;
        stripOfNamespaceRecursive(root);
        if (root.getNodeName().equalsIgnoreCase(WSDL_DESCRIPTION_V_1_1)) {
            rootWsdlVersion = WSDL_V_1_1;
        } else {
            if (root.getNodeName().equalsIgnoreCase(WSDL_DESCRIPTION_V_2_0)) {
                rootWsdlVersion = WSDL_V_2_0;
            } else {
                logger.debug("IDL is not a valid WSDL. Does not have a root element \"{}\" or \"{}\"", WSDL_DESCRIPTION_V_1_1, WSDL_DESCRIPTION_V_2_0);
                return -1;
            }
        }
        
        // process all messages into list (WSDL 2.0 does not define any message elements)
        NodeList messages = root.getElementsByTagName(WSDL_MESSAGE);
        List<WebserviceTypeWsdlMessage> processedMessges = new ArrayList<>();
        if (messages.getLength() > 0) {
            processMessages(messages, processedMessges);
        }
        
        // process all portTypes / interfaces into list
        NodeList portTypes = root.getElementsByTagName(WSDL_SERVICE_INTERFACE_V_1_1);
        NodeList interfaces = root.getElementsByTagName(WSDL_SERVICE_INTERFACE_V_2_0);
        String wsdlVersion = null;
        List<WebserviceTypeWsdlInterface> processedInterfaces = new ArrayList<>();
        if (portTypes.getLength() > 0 && interfaces.getLength() == 0) {
            wsdlVersion = WSDL_V_1_1;
            processInterfaces(portTypes, processedInterfaces);
        } else if (portTypes.getLength() == 0 && interfaces.getLength() > 0) {
            wsdlVersion = WSDL_V_2_0;
            processInterfaces(interfaces, processedInterfaces);
        } else {
            logger.warn("Processed WSDL seems to use both {} and {} specification. Parsing both \"{}\" and \"{}\" elements.", WSDL_V_1_1, WSDL_V_2_0, WSDL_SERVICE_INTERFACE_V_1_1, WSDL_SERVICE_INTERFACE_V_2_0);
            processInterfaces(portTypes, processedInterfaces);
            processInterfaces(interfaces, processedInterfaces);
        }
        
        // process all bindings into list
        NodeList bindings = root.getElementsByTagName(WSDL_BINDING);
        List<WebserviceTypeWsdlBinding> processedBindings = new ArrayList<>();
        processBindings(bindings, processedBindings);
        
        // process all services defined in this WSDL
        List<Webservice> processedWebservices = new ArrayList<>();
        NodeList services = root.getElementsByTagName(WSDL_SERVICE);
        for (int i = 0; i < services.getLength(); i++) {
            Node service = services.item(i);
            NamedNodeMap serviceAttributes = service.getAttributes();
            String serviceName = returnNodeValue(serviceAttributes, "name");
            String serviceIdlVersion = null;
            
            // get info about service endpoints
            List<WebserviceEndpoint> processedEndpoints = new ArrayList<>();
            NodeList endpoints = service.getChildNodes();
            for(int j = 0; j < endpoints.getLength(); j++) {
                Node endpoint = endpoints.item(j);
                
                if (endpoint.getNodeName().equalsIgnoreCase(WSDL_SERVICE_ENDPOINT_V_1_1)) {
                    // detected as WSDL 1.1
                    serviceIdlVersion = WSDL_V_1_1;
                } else if (endpoint.getNodeName().equalsIgnoreCase(WSDL_SERVICE_ENDPOINT_V_2_0)) {
                    // detected as WSDL 2.0
                    serviceIdlVersion = WSDL_V_2_0;
                } else {
                    logger.warn("Unrecognizable element \"{}\" in WSDL \"{}\" element", endpoint.getNodeName(), WSDL_SERVICE);
                    continue;
                }
                
                NamedNodeMap endpointAttributes = endpoint.getAttributes();
                String endpointBinding = returnNodeValue(endpointAttributes, "binding");
                
                // get all operations implemented by this endpoint via its's binding object and list of operations defined in corresponding interface
                WebserviceTypeWsdlBinding binding = getBindingByName(processedBindings, endpointBinding);
                List<WebserviceTypeWsdlBindedOperation> bindedOperations = binding.getBindedOperations();
                List<WebserviceTypeWsdlOperation> interfaceOperations = getInterfaceByName(processedInterfaces, binding.getInterface_()).getOperations();
                
                // process all operation defined in binding
                for (WebserviceTypeWsdlBindedOperation bindedOperation : bindedOperations) {
                    // Note: This single binded operation represent endpoint in CRCE semantics. One WSDL port / endpoint can bind multiple particular operations
                    // each with their parameters, datatypes, return value etc. That is why CRCE Webservice Endpoint corresponds to WSDL Operation.
                    
                    WebserviceTypeWsdlOperation operation = getOperationByName(interfaceOperations, bindedOperation.getName());
                    
                    // proces all input parts defined in this operation as endpoint parameters
                    List<WebserviceEndpointParameter> processedParameters = new ArrayList<>();
                    if (operation.getInputMessage() != null) {
                        List<WebserviceTypeWsdlPart> parts = getMessageByName(processedMessges, operation.getInputMessage()).getParts();
                        for (int k = 0; k < parts.size(); k++) {
                            WebserviceTypeWsdlPart part = parts.get(k);
                            String endpointParameterType = part.getType() != null ? part.getType() : part.getElement();
                            processedParameters.add(new WebserviceEndpointParameter(part.getName(), endpointParameterType, (long)k + 1, null, null));
                        }
                    } else if (operation.getInputElement() != null) {
                        processedParameters.add(new WebserviceEndpointParameter(operation.getName(), operation.getInputElement(), null, null, null));
                    } else {
                        logger.warn("WSDL \"input\" element of operation \"{}\" does not have \"message\" or \"element\" attributes.");
                    }
                    
                    // proces all output parts defined in this operation as endpoint response
                    // TODO In document (i.e. messaging) communication style, output message can generally contain multiple parts - now we just grab a first one.
                    String endpointResponseType = null;
                    if (operation.getOutputMessage() != null) {
                        List<WebserviceTypeWsdlPart> parts = getMessageByName(processedMessges, operation.getOutputMessage()).getParts();
                        if (parts.size() > 0) {
                            endpointResponseType = parts.get(0).getType() != null ? parts.get(0).getType() : parts.get(0).getElement();
                        }
                    } else if (operation.getOutputElement() != null) {
                        endpointResponseType = operation.getOutputElement();
                    } else {
                        logger.warn("WSDL \"output\" element of operation \"{}\" does not have \"message\" or \"element\" attributes.");
                    }
                    WebserviceEndpointResponse processedResponse = new WebserviceEndpointResponse(endpointResponseType, null);
                    
                    // add info about new endpoint
                    processedEndpoints.add(new WebserviceEndpoint(operation.getName(), bindedOperation.getSoapAction(), processedParameters, processedResponse));
                }
            }
            
            processedWebservices.add(new Webservice(serviceName, null, serviceIdlVersion, getSpecificWebserviceType(), processedEndpoints));
        }
        
        ////////////////////////////////////////////////////////////////////////////
        // create CRCE metadata structures and fill it by retrieved info from idl //
        ////////////////////////////////////////////////////////////////////////////
        
        // create new variables for holding references to current capabilities
        Capability capability, webservice_capability, endpoint_capability;
        
        // Capability - CRCE Identity
        String main_name;
        switch (processedWebservices.size()) {
            case 0:
                main_name = "No webservice descriptions";
                break;
            case 1:
                main_name = processedWebservices.get(0).getName();
                break;
            default:
                main_name = "Multiple webservice descriptions";
                break;
        }
        capability = metadataService.getIdentity(resource);
        metadataService.setPresentationName(resource, main_name);
        metadataService.setSize(resource, idl.length());
        setIfSet(capability, ATTRIBUTE__CRCE_IDENTITY__MIME, WSDL_MIME);
        setIfSet(capability, ATTRIBUTE__CRCE_IDENTITY__HASH, getIdlHash(idl));
        
        // Capability - Webserviceschema Identity
        capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICESCHEMA_IDENTITY);
        setIfSet(capability, ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, rootWsdlVersion);
        resource.addCapability(capability);
        resource.addRootCapability(capability);

        // create one capability for each detected webservice representation
        for (Webservice processedWebservice : processedWebservices) {
            
            // Capability - Webserviceschema Webservice
            webservice_capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE);
            setIfSet(webservice_capability, ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME, processedWebservice.getName());
            setIfSet(webservice_capability, ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, processedWebservice.getType());
            setIfSet(webservice_capability, ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI, processedWebservice.getUrl());

            // Capabilities - Webservice Endpoint
            List<WebserviceEndpoint> processedEndpoints = processedWebservice.getEndpoints();
            for (int i = 0; i < processedEndpoints.size(); i++) {
                endpoint_capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICE_ENDPOINT);
                setIfSet(endpoint_capability, ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME, processedEndpoints.get(i).getName());

                // Properties - Webservice Enpoint Parameter
                List<WebserviceEndpointParameter> processedParams = processedEndpoints.get(i).getParameters();
                for (int j = 0; j < processedParams.size(); j++) {
                    Property property = metadataFactory.createProperty(NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER);
                    setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME, processedParams.get(j).getName());
                    setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE, processedParams.get(j).getType());
                    setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER, processedParams.get(j).getOrder());
                    endpoint_capability.addProperty(property);
                }

                // Property - Webservice Endpoint Response
                Property property = metadataFactory.createProperty(NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE);
                setIfSet(property, ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE, processedEndpoints.get(i).getResponse().getType());
                endpoint_capability.addProperty(property);

                resource.addCapability(endpoint_capability);
                webservice_capability.addChild(endpoint_capability);
            }
            
            resource.addCapability(webservice_capability);
            capability.addChild(webservice_capability);
        }

        return processedWebservices.size();
    }
    
    /**
     * Processes all portTypes (WSDL 1.1) / interfaces (WSDL 2.0) defined in WSDL IDL document.
     * 
     * @param interfaces Passed portTypes (WSDL 1.1) / interfaces (WSDL 2.0) defined in WSDL IDL document.
     * @param processedInterfaces Resulting processed portTypes (WSDL 1.1) / interfaces (WSDL 2.0).
     */
    private void processInterfaces(NodeList interfaces, List<WebserviceTypeWsdlInterface> processedInterfaces) {
        
        // iterate through all passed "portType" / "interface" elements
        for (int i = 0; i < interfaces.getLength(); i++) {
            Node interface_ = interfaces.item(i);
            NamedNodeMap interfaceAttributes = interface_.getAttributes();
            String interfaceName = returnNodeValue(interfaceAttributes, "name");
            
            // iterate through all "operation" elements in "portType" / "interface" element
            NodeList operations = interface_.getChildNodes();
            List<WebserviceTypeWsdlOperation> processedOperations = new ArrayList<>();
            for (int j = 0; j < operations.getLength(); j++) {
                Node operation = operations.item(j);
                if (!operation.getNodeName().equalsIgnoreCase("operation")) {
                    continue; // WSDL 2.0 can define "fault" elements at this structure level
                }
                NamedNodeMap operationAttributes = operation.getAttributes();
                String operationName = returnNodeValue(operationAttributes, "name");
                String inputName = null;
                String inputMessage = null;
                String inputElement = null;
                String outputName = null;
                String outputMessage = null;
                String outputElement = null;
                
                // iterate through all "input", "output" and "fault" elements in "operation" element
                NodeList subNodes = operation.getChildNodes();
                for (int k = 0; k < subNodes.getLength(); k++) {
                    Node subnode = subNodes.item(k);
                    if (subnode.getNodeName().equalsIgnoreCase("input")) {
                        // found the "input" element => save all neccessary info about it
                        NamedNodeMap subNodeAttributes = subnode.getAttributes();
                        inputName = returnNodeValue(subNodeAttributes, "name"); // WSDL 1.1
                        if (inputName == null) {
                            inputName = returnNodeValue(subNodeAttributes, "messageLabel"); // WSDL 2.0
                        }
                        inputMessage = returnNodeValue(subNodeAttributes, "message"); // WSDL 1.1
                        inputElement = returnNodeValue(subNodeAttributes, "element"); // WSDL 2.0
                    } else if (subnode.getNodeName().equalsIgnoreCase("output")) {
                        // found the "output" element => save all neccessary info about it
                        NamedNodeMap subNodeAttributes = subnode.getAttributes();
                        outputName = returnNodeValue(subNodeAttributes, "name"); // WSDL 1.1
                        if (outputName == null) {
                            outputName = returnNodeValue(subNodeAttributes, "messageLabel"); // WSDL 2.0
                        }
                        outputMessage = returnNodeValue(subNodeAttributes, "message"); // WSDL 1.1
                        outputElement = returnNodeValue(subNodeAttributes, "element"); // WSDL 2.0
                    }
                }
                
                // save info about processed operation
                processedOperations.add(new WebserviceTypeWsdlOperation(operationName, inputName, inputMessage, inputElement, outputName, outputMessage, outputElement));
            }
            
            // save info about processed interface
            processedInterfaces.add(new WebserviceTypeWsdlInterface(interfaceName, processedOperations));
        }
    }
    
   /**
     * Processes all messages (WSDL 1.1) defined in WSDL IDL document.
     * 
     * @param messages Passed messages (WSDL 1.1) defined in WSDL IDL document.
     * @param processedMessages Resulting processed messages (WSDL 1.1).
     */
    private void processMessages(NodeList messages, List<WebserviceTypeWsdlMessage> processedMessages) {
        
        // iterate through all passed "message" elements
        for (int i = 0; i < messages.getLength(); i++) {
            Node message = messages.item(i);
            NamedNodeMap messageAttributes = message.getAttributes();
            String messageName = returnNodeValue(messageAttributes, "name");
            
            // iterate through all "part" elements in "message" element
            NodeList parts = message.getChildNodes();
            List<WebserviceTypeWsdlPart> processedParts = new ArrayList<>();
            for (int j = 0; j < parts.getLength(); j++) {
                Node part = parts.item(j);
                if (part.getNodeName().equalsIgnoreCase("part")) {
                    NamedNodeMap partAttributes = part.getAttributes();
                    String partName = returnNodeValue(partAttributes, "name");
                    String partType = returnNodeValue(partAttributes, "type");
                    String partElement = returnNodeValue(partAttributes, "element");

                    // save info about processed parts
                    processedParts.add(new WebserviceTypeWsdlPart(partName, partType, partElement));
                }
            }
            
            // save info about processed message
            processedMessages.add(new WebserviceTypeWsdlMessage(messageName, processedParts));
        }
    }
    
    /**
     * Processes all bindings defined in WSDL IDL document.
     * 
     * @param bindings Passed bindings defined in WSDL IDL document.
     * @param processedBindings Resulting processed bindings.
     */
    private void processBindings(NodeList bindings, List<WebserviceTypeWsdlBinding> processedBindings) {
        
        // iterate through all passed "binding" elements
        for (int i = 0; i < bindings.getLength(); i++) {
            Node binding = bindings.item(i);
            NamedNodeMap bindingAttributes = binding.getAttributes();
            String bindingName = returnNodeValue(bindingAttributes, "name");
            String bindingInterface = returnNodeValue(bindingAttributes, "type"); // WSDL 1.1
            if (bindingInterface == null) {
                bindingInterface = returnNodeValue(bindingAttributes, "interface"); // WSDL 2.0
            }
            if(bindingInterface == null) {
                continue;
            }
            
            // get SOAP-specific binding info
            //Node bindingSoap = getNodeByNameAndNamespace(binding.getChildNodes(), "binding", "soap");
            //NamedNodeMap bindingSoapAttributes = bindingSoap.getAttributes();
            String bindingSoapStyle = returnNodeValue(bindingAttributes, "style");
            String bindingSoapTransport = returnNodeValue(bindingAttributes, "transport");
            
            // iterate through all "operation" elements in "binding" element
            NodeList operations = binding.getChildNodes();
            List<WebserviceTypeWsdlBindedOperation> processedBindedOperations = new ArrayList<>();
            for (int j = 0; j < operations.getLength(); j++) {
                Node operation = operations.item(j);
                if (operation.getNodeName().equalsIgnoreCase("operation")) {
                    String bindedOperationName = returnNodeValue(operation.getAttributes(), "name");
                    
                    // get SOAP-specific operation info
                    //Node bindedOperationSoap = getNodeByNameAndNamespace(operation.getChildNodes(), "operation", "soap");
                    //NamedNodeMap bindedOperationSoapAttributes = bindedOperationSoap.getAttributes();
                    String bindedOperationSoapAction = returnNodeValue(operation.getAttributes(), "action");
                    String bindedOperationSoapStyle = returnNodeValue(operation.getAttributes(), "style");
                    
                    // save info about binded operation
                    processedBindedOperations.add(new WebserviceTypeWsdlBindedOperation(bindedOperationName, bindedOperationSoapAction, bindedOperationSoapStyle));
                }
            }
            
            // save info about binding
            processedBindings.add(new WebserviceTypeWsdlBinding(bindingName, bindingInterface, processedBindedOperations, bindingSoapStyle, bindingSoapTransport));
        }
    }
    
    /**
     * Returns binding from a list based on it's name.
     * 
     * @param bindings List of bindings.
     * @param name Name of desired binding.
     * @return Returns binding from a list based on it's name.
     */
    private WebserviceTypeWsdlBinding getBindingByName(List<WebserviceTypeWsdlBinding> bindings, String name) {
        if (bindings == null || name == null) {
            return null;
        }
        for (WebserviceTypeWsdlBinding binding : bindings) {
            if (binding.getName().equals(stripOfNamespace(name))) {
                return binding;
            }
        }
        return null;
    }
    
    /**
     * Returns interface from a list based on it's name.
     * 
     * @param interfaces List of interfaces.
     * @param name Name of desired interface.
     * @return Returns interface from a list based on it's name.
     */
    private WebserviceTypeWsdlInterface getInterfaceByName(List<WebserviceTypeWsdlInterface> interfaces, String name) {
        if (interfaces == null || name == null) {
            return null;
        }
        for (WebserviceTypeWsdlInterface interface_ : interfaces) {
            if (interface_.getName().equals(stripOfNamespace(name))) {
                return interface_;
            }
        }
        return null;
    }
    
    /**
     * Returns operation from a list based on it's name.
     * 
     * @param operations List of operations.
     * @param name Name of desired operation.
     * @return Returns operation from a list based on it's name.
     */
    private WebserviceTypeWsdlOperation getOperationByName(List<WebserviceTypeWsdlOperation> operations, String name) {
        if (operations == null || name == null) {
            return null;
        }
        for (WebserviceTypeWsdlOperation operation : operations) {
            if (operation.getName().equals(stripOfNamespace(name))) {
                return operation;
            }
        }
        return null;
    }
    
    /**
     * Returns message from a list based on it's name.
     * 
     * @param messages List of messages.
     * @param name Name of desired message.
     * @return Returns message from a list based on it's name.
     */
    private WebserviceTypeWsdlMessage getMessageByName(List<WebserviceTypeWsdlMessage> messages, String name) {
        if (messages == null || name == null) {
            return null;
        }
        for (WebserviceTypeWsdlMessage message : messages) {
            if (message.getName().equals(stripOfNamespace(name))) {
                return message;
            }
        }
        return null;
    }
    
    /**
     * Returns value of attribute only if it exists in passed attributes.
     * 
     * @param namedNodeMap List of attributes. 
     * @param nodeName Name of attribute with desired value.
     * @return Value of attribute only if it exists in passed attributes. <code>null</code> otherwise.
     */
    private String returnNodeValue(NamedNodeMap namedNodeMap, String nodeName) {
        Node node = namedNodeMap.getNamedItem(nodeName);
        return node == null ? null : node.getNodeValue();
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

    @Override
    public String generateIDL(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
