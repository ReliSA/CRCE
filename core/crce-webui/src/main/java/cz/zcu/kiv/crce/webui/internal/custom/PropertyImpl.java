package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.webui.internal.legacy.Property;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

public class PropertyImpl implements Property {

    private final Attribute<?> attribute;

    public PropertyImpl(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getName() {
        return attribute.getAttributeType().getName();
    }

    @Override
    public Type getType() {
        return Type.getValue(attribute.getAttributeType().getType().getSimpleName());
    }

    @Override
    public String getValue() {

        Object value = attribute.getValue();
        if (value instanceof Double) {
            return String.format("%.3f", value);
        }

        return attribute.getStringValue();
    }

    @Override
    public Object getConvertedValue() {
        return attribute.getValue();
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
