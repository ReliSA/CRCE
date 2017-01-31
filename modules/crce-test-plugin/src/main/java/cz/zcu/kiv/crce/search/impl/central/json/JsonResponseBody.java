package cz.zcu.kiv.crce.search.impl.central.json;

/**
 * This class represents the response element in the json returned by the
 * maven repo.
 *
 * @author Zdenek Vales
 */
public class JsonResponseBody {

    /**
     * Number of records found in the repository.
     */
    private int numFound;
    private int start;

    /**
     * Found artifacts.
     */
    private JsonArtifactDescriptor[] docs;



}
