package cz.zcu.kiv.crce.crce_component_collection.api.bean;

import java.util.List;

/**
 * The bean represents detailed artifact data.
 * <p/>
 * Date: 05.02.19
 *
 * @author Roman Pesek
 */
public class CollectionDetailBean {
    private String id;
    private String name;
    private String version;
    private List<String> specificArtifacts;
    private List<String> parameters;
    private List<String> rangeArtifacts;

    public CollectionDetailBean(String id, String name, String version, List<String> specificArtifacts, List<String> parameters,
                                List<String> rangeArtifacts) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.specificArtifacts = specificArtifacts;
        this.parameters = parameters;
        this.rangeArtifacts = rangeArtifacts;
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

    public List<String> getSpecificArtifacts() { return specificArtifacts; }

    public List<String> getParameters() { return parameters; }

    public List<String> getRangeArtifacts() { return rangeArtifacts; }
}
