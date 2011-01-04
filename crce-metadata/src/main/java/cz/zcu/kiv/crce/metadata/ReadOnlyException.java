package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author kalwi
 */
public class ReadOnlyException extends Exception {

    public ReadOnlyException() {
    }
    
    public ReadOnlyException(String msg) {
        super(msg);
    }
}
