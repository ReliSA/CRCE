package cz.zcu.kiv.crce.crce_component_collection.api.bean;

/**
 * The bean represents a set of artifacts (collection).
 * <p/>
 * Date: 05.02.19
 *
 * @author Roman Pesek
 */
public class CollectionBean {
    private String id;
    private String name;
    private String version;
    private boolean collection;

    public CollectionBean(String id, String name, String version, boolean collection) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.collection = collection;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isCollection() { return collection; }

    public void setCollection(boolean collection) { this.collection = collection; }

    @Override
    public String toString(){
        return name;
    }
}
