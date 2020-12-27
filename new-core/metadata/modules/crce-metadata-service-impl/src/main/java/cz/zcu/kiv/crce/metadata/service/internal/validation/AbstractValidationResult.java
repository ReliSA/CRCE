package cz.zcu.kiv.crce.metadata.service.internal.validation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import cz.zcu.kiv.crce.metadata.service.validation.Reason;
import cz.zcu.kiv.crce.metadata.service.validation.ValidationResult;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AbstractValidationResult implements ValidationResult {

    protected String entityId;
    protected final List<Reason> reasons = new ArrayList<>();
    protected boolean contextValid = true;
    protected boolean entityValid = true;

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String id) {
        this.entityId = id;
    }

    @Override
    public boolean isContextValid() {
        return contextValid;
    }

    public void setContextValid(boolean valid) {
        this.contextValid = valid;
    }

    @Override
    public boolean isEntityValid() {
        return entityValid;
    }

    @Override
    public void setEntityValid(boolean valid) {
        this.entityValid = valid;
    }

    @Override
    public List<Reason> getReasons() {
        return reasons;
    }

    @Override
    public void addReason(@Nonnull Reason reason) {
        reasons.add(reason);
    }

    @Override
    public String toString() {
        return "AbstractValidationResult{" + "reasons=" + reasons + ", contextValid=" + contextValid + ", entityValid=" + entityValid + '}';
    }
}
