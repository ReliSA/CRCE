package cz.zcu.kiv.crce.apicomp;

/**
 * Class to store compatibility check results.
 * Might be just is/is not compatible or some kind of diff.
 *
 * todo: connect to the compatibility api
 * todo: diff
 */
public class CompatibilityCheckResult {

    public static CompatibilityCheckResult apiNotComparable() {
        return new CompatibilityCheckResult(false, true);
    }

    /**
     * Whether or not are APIs compatible.
     */
    public final boolean compatible;

    /**
     * Set if the APIs are not comparable.
     */
    public final boolean notComparable;

    public CompatibilityCheckResult(boolean compatible, boolean notComparable) {
        this.compatible = compatible;
        this.notComparable = notComparable;
    }
}
