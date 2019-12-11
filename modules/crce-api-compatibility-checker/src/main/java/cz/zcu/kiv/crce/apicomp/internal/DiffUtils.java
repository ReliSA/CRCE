package cz.zcu.kiv.crce.apicomp.internal;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;

/**
 * Helper class for working with Diffs.
 *
 */
public class DiffUtils {

    public static Diff createDiff(String name, DifferenceLevel level, Difference value) {
        Diff d = new DefaultDiffImpl();

        d.setValue(value);
        d.setName(name);
        d.setLevel(level);

        return d;
    }

    public static Diff createDELDiff(String name, DifferenceLevel level) {
        return createDiff(name, level, Difference.DEL);
    }

    public static Diff createINSDiff(String name, DifferenceLevel level) {
        return createDiff(name, level, Difference.INS);
    }
}
