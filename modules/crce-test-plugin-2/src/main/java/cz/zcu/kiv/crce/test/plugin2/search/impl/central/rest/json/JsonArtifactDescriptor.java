package cz.zcu.kiv.crce.test.plugin2.search.impl.central.rest.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * This class represents an object which describes the artifact.
 * In the json returned by the maven repo, this object is used in
 * response.doc array.
 *
 * @author Zdenek Vales
 */
@XmlRootElement
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="EI_EXPOSE_REP",
        justification="Getters/setters to arrays")
public class JsonArtifactDescriptor implements Serializable{

    /**
     * Use these values to fill the template:
     * group id with '/' as separator
     * artifact id
     * version
     * artifact id
     * version
     * extension - either jar or pom
     */
    public static final String DOWNLOAD_URL_TEMPLATE = "http://search.maven.org/remotecontent?filepath=%s/%s/%s/%s-%s.%s";

    /**
     * Artifact id in format:
     * GroupId:ArtifactId:Version
     */
    @XmlElement
    private String id;

    /**
     * Group id.
     */
    @XmlElement
    private String g;

    /**
     * Artifact id.
     */
    @XmlElement
    private String a;

    /**
     * Version.
     */
    @XmlElement
    private String v;

    /**
     * Packaging.
     */
    @XmlElement
    private String p;

    /**
     * Artifact timestamp.
     */
    @XmlElement
    private long timestamp;

    /**
     * Tags assigned to this artifact.
     */
    @XmlElement
    private String[] tags;

    /**
     * Possible downloads.
     */
    @XmlElement
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

    // todo: check the download link availability / validity
    public String jarDownloadLink() {
        return String.format(DOWNLOAD_URL_TEMPLATE, getG().replace('.','/'), getA(), getV(), getA(), getV(), "jar");
    }

    public String pomDownloadLink() {
        return String.format(DOWNLOAD_URL_TEMPLATE, getG().replace('.','/'), getA(), getV(), getA(), getV(), "pom");
    }
}
