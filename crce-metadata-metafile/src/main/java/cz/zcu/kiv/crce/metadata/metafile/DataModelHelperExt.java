package cz.zcu.kiv.crce.metadata.metafile;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.results.Result;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * This helper provides support for storing and reading Metadata entities
 * to/from XML.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface DataModelHelperExt {

    public static final String OBR = "obr";

    Resource readMetadata(String xml) throws Exception;

    Resource readMetadata(Reader reader) throws IOException, Exception;

    Repository readRepository(String xml) throws Exception;

    Repository readRepository(Reader reader) throws IOException, Exception;
    
    Result readResult(String xml) throws Exception;
    
    Result readResult(Reader reader) throws IOException, Exception;
    
    
    String writeMetadata(Resource resource);

    void writeMetadata(Resource resource, Writer writer) throws IOException;

    String writeRepository(Repository repository);

    void writeRepository(Repository repository, Writer writer) throws IOException;

    String writeResult(Result result);

    void writeResult(Result result, Writer writer) throws IOException;
}
