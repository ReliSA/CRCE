package cz.zcu.kiv.crce.crce_webui_v2.collection.classes;

/**
 * Bean represents the artifact in the Store defined by the version range (name / range).
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
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
