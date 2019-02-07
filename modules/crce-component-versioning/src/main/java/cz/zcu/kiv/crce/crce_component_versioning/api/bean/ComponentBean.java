package cz.zcu.kiv.crce.crce_component_versioning.api.bean;

public class ComponentBean {
    private String id;
    private String name;
    private String version;
    private boolean composite;

    public ComponentBean(String id, String name, String version, boolean composite) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.composite = composite;
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

    public boolean isComposite() { return composite; }

    public void setComposite(boolean composite) { this.composite = composite; }

    @Override
    public String toString(){
        return name;
    }
}
