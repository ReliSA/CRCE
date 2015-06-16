package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdl extends WebserviceTypeBase implements WebserviceType {

    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);
    
    private static final String WSDL_V_1_1 = "1.1";
    private static final String WSDL_V_2_0 = "2.0";
    
    private static final String WSDL_DEFINITIONS = "definitions";
    private static final String WSDL_SERVICE = "service";
    
    public WebserviceTypeWsdl(MetadataFactory mf, MetadataService ms) {
        super(mf, ms);
    }
    
    @Override
    public String getSpecificWebserviceCategory() {
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
        if (!root.getNodeName().equalsIgnoreCase(WSDL_DEFINITIONS)) {
            logger.debug("IDL is not a valid WSDL. Does not have a root element \"{}\"", WSDL_DEFINITIONS);
            return false;
        }
        
        // check whether WSDL has at least one service defined
        if (root.getElementsByTagName(WSDL_SERVICE).getLength() < 1) {
            logger.debug("WSDL does not have any \"{}\" elements defined i.e. no webservice IDLs to parse.", WSDL_SERVICE);
            return false;
        }
        
        return true;
    }

    @Override
    public Resource parseIDL(String idl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
