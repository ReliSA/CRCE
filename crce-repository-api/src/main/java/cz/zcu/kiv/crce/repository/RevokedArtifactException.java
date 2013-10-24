package cz.zcu.kiv.crce.repository;

/**
 * Throwed if artifact put into the Buffer or Store is revoked and can not be
 * stored.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class RevokedArtifactException extends Exception {

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

    public RevokedArtifactException(String reason) {
        this(reason, REASON.UNSPECIFIED);
    }

    public RevokedArtifactException(String reasonDesc, REASON reason) {
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
