package cz.zcu.kiv.crce.mvn.plugin.search;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Interface for classes with changeable configuration.
 *
 * Created by Zdenek Vales on 13.6.2017.
 */
public interface Configurable {

    /**
     * Reload configuration from sourceFileName file.
     * Format of the file is up to the implementation class.
     * All possible configuration should be overwritten by this method.
     *
     * @param sourceFile Source file containing new values to be used. Can be null and it's up to implementation
     *                   class to handle null values.
     */
    void reconfigure(File sourceFile) throws FileNotFoundException;

}
