package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * This class represents the response element in the json returned by the
 * maven repo.
 *
 * @author Zdenek Vales
 */
@XmlRootElement
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="EI_EXPOSE_REP",
        justification="Getters/setters to arrays")
public class JsonResponseBody implements Serializable{

    /**
     * Number of records found in the repository.
     */
    @XmlElement
    private int numFound;
    @XmlElement
    private int start;

    /**
     * Found artifacts.
     */
    @XmlElement
    private JsonArtifactDescriptor[] docs;

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public JsonArtifactDescriptor[] getDocs() {
        return docs;
    }

    public void setDocs(JsonArtifactDescriptor[] docs) {
        this.docs = docs;
    }
}
