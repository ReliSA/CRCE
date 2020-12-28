package cz.zcu.kiv.crce.apicomp.impl.mov.common;

/**
 * Immutable object that holds info about differences in urls to endpoints between two APIs.
 */
public class MovDetectionResult {

    /**
     * Returns object representing no MOV in API.
     * @return
     */
    public static MovDetectionResult noMov() {
        return new MovDetectionResult(false, false, false);
    }

    public final boolean hostDiff;
    public final boolean pathDiff;
    public final boolean operationDiff;

    public MovDetectionResult(boolean hostDiff, boolean pathDiff, boolean operationDiff) {
        this.hostDiff = hostDiff;
        this.pathDiff = pathDiff;
        this.operationDiff = operationDiff;
    }

    /**
     * Returns true if any difference was found.
     * @return
     */
    public boolean isAnyDiff() {
        return hostDiff || pathDiff || operationDiff;
    }

    /**
     * Returns true whether the result of MOV detection is possible MOV flag.
     *
     * MOV is possible whenever there's no difference in operations and there is
     * difference in host, path or both.
     *
     * @return
     */
    public boolean isPossibleMOV() {
       return !operationDiff &&
               (hostDiff || pathDiff);
    }

    @Override
    public String toString() {
        return "MovDetectionResult{" +
                "hostDiff=" + hostDiff +
                ", pathDiff=" + pathDiff +
                ", operationDiff=" + operationDiff +
                '}';
    }
}
