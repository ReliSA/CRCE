package cz.zcu.kiv.crce.crce_component_versioning.api.bean;

import java.util.List;

public class ComponentDetailBean {
    private String id;
    private String name;
    private String version;
    private List<String> content;

    public ComponentDetailBean(String id, String name, String version, List<String> content) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getContent() {
        return content;
    }
}
