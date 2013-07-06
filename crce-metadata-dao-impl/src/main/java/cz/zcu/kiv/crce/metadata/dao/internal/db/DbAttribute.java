package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author Cihlář
 */
public class DbAttribute {

    private long entityId;
    private String name;
    private Short attributeIndex;
    private Short listIndex;
    private Short type;
    private Short operator;
    private String stringValue;
    private Long longValue;
    private Double doubleValue;
    private Integer versionMajorValue;
    private Integer versionMinorValue;
    private Integer versionMicroValue;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getListIndex() {
        return listIndex;
    }

    public void setListIndex(Short listIndex) {
        this.listIndex = listIndex;
    }

    public Short getAttributeIndex() {
        return attributeIndex;
    }

    public void setAttributeIndex(Short attributeIndex) {
        this.attributeIndex = attributeIndex;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getOperator() {
        return operator;
    }

    public void setOperator(Short operator) {
        this.operator = operator;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Integer getVersionMajorValue() {
        return versionMajorValue;
    }

    public void setVersionMajorValue(Integer versionMajorValue) {
        this.versionMajorValue = versionMajorValue;
    }

    public Integer getVersionMinorValue() {
        return versionMinorValue;
    }

    public void setVersionMinorValue(Integer versionMinorValue) {
        this.versionMinorValue = versionMinorValue;
    }

    public Integer getVersionMicroValue() {
        return versionMicroValue;
    }

    public void setVersionMicroValue(Integer versionMicroValue) {
        this.versionMicroValue = versionMicroValue;
    }
}
