package cz.zcu.kiv.crce.repository;

import javax.annotation.Nonnull;

/**
 * Throwed if artifact put into the Buffer or Store is revoked and can not be stored.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RefusedArtifactException extends Exception {

    private static final long serialVersionUID = 3732119005799739933L;

    public RefusedArtifactException(@Nonnull String reason) {
        super(reason);
    }
}
