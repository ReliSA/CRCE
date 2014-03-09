package cz.zcu.kiv.crce.rest.internal.structures;

import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Representation one of requirement's version demands.
 * In metadata, version demand is version attribute inside osgi.wiring.package requirement.
 *
 * Example im metadata: <attribute name='version' value='1.0.0' op='greater-than' />
 *
 * @author Jan reznicek
 *
 */
public class VersionDemand {

    public static final String GREATER_THAN = "greater-than";
    public static final String LESS_THAN = "less-than";
    public static final String EQUEAL = "equal";
    public static final String LESS_EQUEAL = "less-equal";
    public static final String GREATER_EQUEAL = "greater-equal";
    public static final String NOT_EQUEAL = "not-equal";

    private Version version;
    private String operation;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
