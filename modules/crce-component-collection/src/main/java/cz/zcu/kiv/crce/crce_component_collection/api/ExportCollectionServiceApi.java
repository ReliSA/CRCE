package cz.zcu.kiv.crce.crce_component_collection.api;

import cz.zcu.kiv.crce.crce_component_collection.api.impl.CollectionService;
import cz.zcu.kiv.crce.crce_component_collection.api.impl.LimitRange;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * The interface provides methods for exporting artifacts and their metadata to the zip package.
 * <p/>
 * Date: 05.02.19
 *
 * @author Roman Pesek
 */
public interface ExportCollectionServiceApi {
    /**
     * Interface for collecting collections component (including metadata)to the file system.
     * <p/>
     * Date: 29.03.19
     *
     * @author Roman Pesek
     */
    boolean exportCollection(String idCollection, File path, String idSession, LimitRange range,
                             CollectionService collectionService, boolean details);

    /**
     * Interfaces for archiving folder in the zip file.
     * <p/>
     * Date: 29.03.19
     *
     * @author Roman Pesek
     */
    void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException;
}
