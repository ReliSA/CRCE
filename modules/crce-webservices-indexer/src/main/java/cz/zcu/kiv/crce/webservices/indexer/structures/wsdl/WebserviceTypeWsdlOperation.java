package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

/**
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the inputName
     */
    public String getInputName() {
        return inputName;
    }

    /**
     * @param inputName the inputName to set
     */
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    /**
     * @return the inputMessage
     */
    public String getInputMessage() {
        return inputMessage;
    }

    /**
     * @param inputMessage the inputMessage to set
     */
    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    /**
     * @return the inputElement
     */
    public String getInputElement() {
        return inputElement;
    }

    /**
     * @param inputElement the inputElement to set
     */
    public void setInputElement(String inputElement) {
        this.inputElement = inputElement;
    }

    /**
     * @return the outputName
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     * @param outputName the outputName to set
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    /**
     * @return the outputMessage
     */
    public String getOutputMessage() {
        return outputMessage;
    }

    /**
     * @param outputMessage the outputMessage to set
     */
    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    /**
     * @return the outputElement
     */
    public String getOutputElement() {
        return outputElement;
    }

    /**
     * @param outputElement the outputElement to set
     */
    public void setOutputElement(String outputElement) {
        this.outputElement = outputElement;
    }
}
