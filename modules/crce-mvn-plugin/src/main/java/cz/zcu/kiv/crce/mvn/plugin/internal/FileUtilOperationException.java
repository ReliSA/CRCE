package cz.zcu.kiv.crce.mvn.plugin.internal;

/**
 * Thrown when method in FileUtil fails.
 * Created by Zdenek Vales on 19.6.2017.
 */
public class FileUtilOperationException extends Exception {
    public FileUtilOperationException(String message) {
        super(message);
    }
}
