package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

/**
 * This is a data class for inner representation of abstract WSDL operation element.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlOperation {
    
    private String name;
    
    private String inputName;
    private String inputMessage;
    private String inputElement;
    
    private String outputName;
    private String outputMessage;
    private String outputElement;

    /**
     * Constructor.
     *
     * @param name Name of this operation.
     * @param inputName Name of element representing the input structure of this operation.
     * @param inputMessage Reference to a message (WSDL 1.1) representing the input structure of this operation.
     * @param inputElement Reference to an element (WSDL 2.0) representing the input structure of this operation.
     * @param outputName Name of element representing the output structure of this operation.
     * @param outputMessage Reference to a message (WSDL 1.1) representing the output structure of this operation.
     * @param outputElement Reference to an element (WSDL 2.0) representing the output structure of this operation.
     */
    public WebserviceTypeWsdlOperation(String name, String inputName, String inputMessage, String inputElement, String outputName, String outputMessage, String outputElement) {
        this.name = name;
        this.inputName = inputName;
        this.inputMessage = inputMessage;
        this.inputElement = inputElement;
        this.outputName = outputName;
        this.outputMessage = outputMessage;
        this.outputElement = outputElement;
    }

    @Override
    public String toString() {
        return String.format("name: %s, inputName: %s, inputMessage: %s, inputElement: %s, outputName: %s, outputMessage: %s, outputElement: %s",
                name, inputName, inputMessage, inputElement, outputName, outputMessage, outputElement);
    }
    
    /**
     * @return Name of this operation.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this operation.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Name of element representing the input structure of this operation.
     */
    public String getInputName() {
        return inputName;
    }

    /**
     * @param inputName Name of element representing the input structure of this operation.
     */
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    /**
     * @return Reference to a message (WSDL 1.1) representing the input structure of this operation.
     */
    public String getInputMessage() {
        return inputMessage;
    }

    /**
     * @param inputMessage Reference to a message (WSDL 1.1) representing the input structure of this operation.
     */
    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    /**
     * @return Reference to an element (WSDL 2.0) representing the input structure of this operation.
     */
    public String getInputElement() {
        return inputElement;
    }

    /**
     * @param inputElement Reference to an element (WSDL 2.0) representing the input structure of this operation.
     */
    public void setInputElement(String inputElement) {
        this.inputElement = inputElement;
    }

    /**
     * @return Name of element representing the output structure of this operation.
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     * @param outputName Name of element representing the output structure of this operation.
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    /**
     * @return Reference to a message (WSDL 1.1) representing the output structure of this operation.
     */
    public String getOutputMessage() {
        return outputMessage;
    }

    /**
     * @param outputMessage Reference to a message (WSDL 1.1) representing the output structure of this operation.
     */
    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    /**
     * @return Reference to an element (WSDL 2.0) representing the output structure of this operation.
     */
    public String getOutputElement() {
        return outputElement;
    }

    /**
     * @param outputElement Reference to an element (WSDL 2.0) representing the output structure of this operation.
     */
    public void setOutputElement(String outputElement) {
        this.outputElement = outputElement;
    }
}
