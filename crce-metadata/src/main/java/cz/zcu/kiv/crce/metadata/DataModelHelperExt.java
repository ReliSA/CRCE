package cz.zcu.kiv.crce.metadata;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Resource;

/**
 *
 * @author kalwi
 */
public interface DataModelHelperExt extends DataModelHelper {
    
    public static final String OBR = "obr";
    
    Resource readMetadata(String xml) throws Exception;

    Resource readMetadata(Reader reader) throws Exception;

    String writeMetadata(Metadata metadata);
    
    void writeMetadata(Metadata metadata, Writer writer) throws IOException;

}
