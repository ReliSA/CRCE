package cz.zcu.kiv.crce.resolver.optimizer;

/**
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public enum OptimizationMode {

    MIN,
    MAX;

    /**
     *  Unlike {@link #valueOf}, trims and converts the string to upper case before
     *  parsing attempt.
     *
     * @param modeString string to parse
     * @return value or null
     */
    public static OptimizationMode parse(String modeString) {
        return modeString != null ? valueOf(modeString.trim().toUpperCase()) : null;
    }
}
