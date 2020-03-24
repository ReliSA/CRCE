package cz.zcu.kiv.crce.apicomp.impl.mov;

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
     * @return
     */
    public boolean isPossibleMOV() {
       return
           (hostDiff && !pathDiff && !operationDiff)
           || (!hostDiff && pathDiff && !operationDiff)
           || (!hostDiff && !pathDiff && operationDiff)
           || (hostDiff && pathDiff && !operationDiff)
           ;
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
