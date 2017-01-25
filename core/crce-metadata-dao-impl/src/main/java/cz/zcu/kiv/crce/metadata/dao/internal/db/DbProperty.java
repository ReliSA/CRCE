package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class DbProperty {

    private long propertyId;
    private long parentId;
    private String id;
    private String namespace;

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
