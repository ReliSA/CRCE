package cz.zcu.kiv.crce.apicomp.result;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;

import java.util.List;

/**
 * Helper class for difference aggregation. You can use it for grouping of
 * differences at the same level of comparison.
 *
 * @author Jaroslav Bauml
 */
public class DifferenceAggregation {

    public static Difference calculateFinalDifferenceFor(List<Diff> diffList) {
        DifferenceAggregation aggregation = new DifferenceAggregation();
        diffList.forEach(diff -> aggregation.addDifference(diff.getValue()));
        return aggregation.getResultDifference();
    }

    private int nonCounter;
    private int insCounter;
    private int delCounter;
    private int genCounter;
    private int speCounter;
    private int mutCounter;
    private int unkCounter;

    private Difference result;


    public DifferenceAggregation() {
    }


    /**
     * Add differences.
     *
     * @param diff Difference to accumulate.
     */
    public void addDifference(final Difference diff) {

        if (diff == null) {
            return;
        }

        switch (diff) {
            case UNK:
                unkCounter++;
                break;
            case INS:
                insCounter++;
                break;
            case DEL:
                delCounter++;
                break;
            case GEN:
                genCounter++;
                break;
            case SPE:
                speCounter++;
                break;
            case MUT:
                mutCounter++;
                break;
            case NON:
                nonCounter++;
                break;
            default:
                break;
        }
    }

    /**
     * Clears currently aggregated differencies.
     */
    public void clear() {
        nonCounter = 0;
        mutCounter = 0;
        speCounter = 0;
        genCounter = 0;
        delCounter = 0;
        insCounter = 0;
        unkCounter = 0;
    }

    /**
     * Returns accumulated difference.
     *
     * @return Combined (accumulated) difference on the same layer of
     * comparison.
     */
    public Difference getResultDifference() {
        combine();
        return result;
    }

    private void combine() {
        // unknown has biggest priority
        if (unkCounter > 0) {
            result = Difference.UNK;
        }
        // is mutation?
        else if (mutCounter > 0) {
            result = Difference.MUT;
        }
        // insertion or specialization in same time as deletion or
        // generalization
        else if ((insCounter > 0 || speCounter > 0) && (delCounter > 0 || genCounter > 0)) {
            result = Difference.MUT;
        }
        // insertion or specialization
        else if (insCounter > 0) {
            result = Difference.INS;
        } else if (speCounter > 0) {
            result = Difference.SPE;
        }
        // deletion or generalization
        else if (delCounter > 0) {
            result = Difference.DEL;
        } else if (genCounter > 0) {
            result = Difference.GEN;
        }
        // no change
        else {
            result = Difference.NON;
        }
    }

    public String toString() {
        return getResultDifference().toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + delCounter;
        result = prime * result + genCounter;
        result = prime * result + insCounter;
        result = prime * result + mutCounter;
        result = prime * result + nonCounter;
        result = prime * result + speCounter;
        result = prime * result + unkCounter;
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DifferenceAggregation other = (DifferenceAggregation) obj;
        if (delCounter != other.delCounter) {
            return false;
        }
        if (genCounter != other.genCounter) {
            return false;
        }
        if (insCounter != other.insCounter) {
            return false;
        }
        if (mutCounter != other.mutCounter) {
            return false;
        }
        if (nonCounter != other.nonCounter) {
            return false;
        }
        if (speCounter != other.speCounter) {
            return false;
        }
        if (unkCounter != other.unkCounter) {
            return false;
        }

        return true;
    }
}
