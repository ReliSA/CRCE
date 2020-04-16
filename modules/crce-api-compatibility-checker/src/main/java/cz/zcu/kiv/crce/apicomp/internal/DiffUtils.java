package cz.zcu.kiv.crce.apicomp.internal;

import cz.zcu.kiv.crce.apicomp.impl.mov.MovDiff;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;

import java.util.Arrays;
import java.util.List;

/**
 * Helper class for working with Diffs.
 *
 */
public class DiffUtils {

    public static final List<Difference> dangerousDifferences = Arrays.asList(
            Difference.DEL,
            Difference.SPE,
            Difference.MUT,
            Difference.UNK
    );

    public static final List<Difference> safeMovDifferences = Arrays.asList(
            Difference.NON,
            Difference.SPE,
            Difference.GEN
    );

    /**
     * Checks if the given difference value is compatible with MOV flag.
     *
     * @param diff
     * @return
     */
    public static boolean isDiffSafeForMov(Difference diff) {
        return safeMovDifferences.contains(diff);
    }

    public static boolean isDangerous(Diff diff) {
        return dangerousDifferences.contains(diff.getValue());
    }

    public static Diff createDiff(String name, DifferenceLevel level, List<Diff> childDiffs) {
        Difference value = DifferenceAggregation.calculateFinalDifferenceFor(childDiffs);
        Diff d = createDiff(name, level, value);
        d.addChildren(childDiffs);

        return d;
    }

    /**
     * If any of childDiffs is MovDiff, returned object will also be a MovDiff
     * @param name
     * @param level
     * @param childDiffs
     * @return
     */
    public static Diff createDiffMov(String name, DifferenceLevel level, List<Diff> childDiffs ) {
        boolean isMov = childDiffs.stream().anyMatch(MovDiff::isMovDiff);
        Diff d;
        if (isMov) {
            d = new MovDiff();
        } else {
            d = new DefaultDiffImpl();
        }

        Difference value = DifferenceAggregation.calculateFinalDifferenceFor(childDiffs);
        d.addChildren(childDiffs);
        d.setLevel(level);
        d.setName(name);
        d.setValue(value);

        return d;
    }

    public static Diff createDiff(String name, DifferenceLevel level, Difference value) {
        return DiffUtils.createDiff(name, level, value, false);
    }

    public static Diff createDiff(String name, DifferenceLevel level, Difference value, boolean mov) {
        Diff d;
        if (mov) {
            d = new MovDiff(true);
        } else {
            d = new DefaultDiffImpl();
        }

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
