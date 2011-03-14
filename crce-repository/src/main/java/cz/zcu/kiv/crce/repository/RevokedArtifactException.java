package cz.zcu.kiv.crce.repository;

/**
 * Throwed if artifact put into the Buffer or Store is revoked and can not be
 * stored.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RevokedArtifactException extends Exception {
    public RevokedArtifactException(String reason) {
        super(reason);
    }
}
