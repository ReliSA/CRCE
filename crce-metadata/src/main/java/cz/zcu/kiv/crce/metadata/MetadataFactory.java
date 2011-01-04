package cz.zcu.kiv.crce.metadata;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author kalwi
 */
public interface MetadataFactory {
    
    public Metadata createMetadata(File obrFile) throws IOException;
    
    public Metadata createMetadataFor(File resourceFile) throws IOException;

}
