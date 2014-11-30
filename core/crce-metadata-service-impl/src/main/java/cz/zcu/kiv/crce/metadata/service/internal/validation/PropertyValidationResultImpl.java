package cz.zcu.kiv.crce.metadata.service.internal.validation;


import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.service.validation.PropertyValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.Reason;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PropertyValidationResultImpl extends AbstractValidationResult implements PropertyValidationResult {

    private Property property;

    @Override
    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public Property getProperty() {
        return property;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Property context: ").append(isContextValid() ? "valid" : "invalid").append("\r\n");
        sb.append("Property");
        if (property != null) {
            sb.append(" - ").append(property.getNamespace()).append(" - ").append(property.getId());
        }
        sb.append(": ").append(isEntityValid() ? "valid" : "invalid").append("\r\n");
        for (Reason reason : getReasons()) {
            sb.append("  ").append(reason.getId()).append(" - ").append(reason.getType()).append(" - ").append(reason.getText()).append("\r\n");
        }
        return sb.toString();
    }
}
