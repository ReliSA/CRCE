package cz.zcu.kiv.crce.repository;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Throwed if artifact put into the Buffer or Store is revoked and can not be stored.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public class RefusedArtifactException extends Exception {

    private static final long serialVersionUID = 3732119005799739933L;

    public enum REASON {
        /**
         * General reason for revoking, no special care needed.
         */
        UNSPECIFIED,
        /**
         * Revoked because there has been another resource of the same symbolic name
         * in the buffer.
         */
        ALREADY_IN_BUFFER
    }

    private REASON reason;

    public RefusedArtifactException(String reason) {
        this(reason, REASON.UNSPECIFIED);
    }

    public RefusedArtifactException(String reasonDesc, REASON reason) {
        super(reasonDesc);
        this.reason = reason;
    }

    public REASON getReason() {
        return reason;
    }

    public void setReason(REASON reason) {
        this.reason = reason;
    }
}
