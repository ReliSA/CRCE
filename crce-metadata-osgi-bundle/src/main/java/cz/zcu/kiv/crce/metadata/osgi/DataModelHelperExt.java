package cz.zcu.kiv.crce.metadata.osgi;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.felix.bundlerepository.DataModelHelper;

// TODO split to resource creators

/**
 *
 * @author kalwi
 */
public interface DataModelHelperExt extends DataModelHelper {
    
    public static final String OBR = "obr";
    
    Resource readMetadata(String xml) throws Exception;

    Resource readMetadata(Reader reader) throws IOException, Exception;

    String writeMetadata(Resource resource);
    
    void writeMetadata(Resource resource, Writer writer) throws IOException;

}
