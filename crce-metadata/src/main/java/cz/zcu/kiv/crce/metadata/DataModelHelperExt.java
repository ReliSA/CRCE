package cz.zcu.kiv.crce.metadata;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.felix.bundlerepository.DataModelHelper;

/**
 *
 * @author kalwi
 */
public interface DataModelHelperExt extends DataModelHelper {
    
    public static final String OBR = "obr";
    
    Resource readMetadata(String xml) throws Exception;

    Resource readMetadata(Reader reader) throws Exception;

    String writeMetadata(Resource resource);
    
    void writeMetadata(Resource resource, Writer writer) throws IOException;

}
