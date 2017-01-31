package cz.zcu.kiv.crce.search.impl.central.json;

/**
 * This class represents an object which describes the artifact.
 * In the json returned by the maven repo, this object is used in
 * response.doc array.
 *
 * @author Zdenek Vales
 */
public class JsonArtifactDescriptor {

    /**
     * Artifact id in format:
     * GroupId:ArtifactId:Version
     */
    private String id;

    /**
     * Group id.
     */
    private String g;

    /**
     * Artifact id.
     */
    private String a;

    /**
     * Version.
     */
    private String v;

    /**
     * Packaging.
     */
    private String p;

    /**
     * Artifact timestamp.
     */
    private long timestamp;

    /**
     * Tags assigned to this artifact.
     */
    private String[] tags;

    /**
     * Possible downloads.
     */
    private String[] ec;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getEc() {
        return ec;
    }

    public void setEc(String[] ec) {
        this.ec = ec;
    }
}
