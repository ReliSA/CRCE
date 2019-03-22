package cz.zcu.kiv.crce.crce_webui_v2.collection.classes;

public class ArtifactRangeBean {
    private String name;
    private String range;

    public ArtifactRangeBean(String name, String range) {
        this.name = name;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public String getRange() {
        return range;
    }
}
