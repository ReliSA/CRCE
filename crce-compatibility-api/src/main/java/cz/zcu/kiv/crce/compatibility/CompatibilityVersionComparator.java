package cz.zcu.kiv.crce.compatibility;

import java.util.Comparator;

import org.osgi.framework.Version;

/**
 * Compares two pieces of Compatibility data by their version.
 *
 * Date: 29.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityVersionComparator implements Comparator<Compatibility> {

    private static final CompatibilityVersionComparator upperInstance = new CompatibilityVersionComparator(VERSION_SELECTOR.UPPER);
    private static final CompatibilityVersionComparator baseInstance = new CompatibilityVersionComparator(VERSION_SELECTOR.BASE);

    /**
     * This instance uses "upper" version for comparison.
     * @return
     */
    public static CompatibilityVersionComparator getUpperComparator() {
        return upperInstance;
    }

    /**
     * This instance uses base version for comparison.
     * @return
     */
    public static CompatibilityVersionComparator getBaseComparator() {
        return baseInstance;
    }

    private enum VERSION_SELECTOR {
        BASE,
        UPPER
    }

    private VERSION_SELECTOR selector;

    private CompatibilityVersionComparator(VERSION_SELECTOR selector) {
        this.selector = selector;
    }


    @Override
    public int compare(Compatibility o1, Compatibility o2) {
        Version v1, v2;
        switch(selector) {
            default:
            case UPPER:
                v1 = o1.getResourceVersion();
                v2 = o2.getResourceVersion();
                break;
            case BASE:
                v1 = o1.getBaseResourceVersion();
                v2 = o2.getBaseResourceVersion();
                break;
        }

        return v1.compareTo(v2);
    }
}
