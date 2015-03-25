package cz.zcu.kiv.crce.compatibility;

/**
 * List of possible difference classes between two resource elements.
 *
 * Date: 25.3.15
 *
 * @author Jakub Danek
 */
public enum Difference {
    NON("None"),
    INS("Insertion"),
    DEL("Deletetion"),
    GEN("Generalization"),
    SPE("Specialization"),
    MUT("Mutation"),
    UNK("Unknown");

    private String name;

    private Difference(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Returns direct opposite of the value.
     *
     * INS to DEL
     * SPE to GEN
     * and vice versa.
     *
     * Returns the value itself if there is no opposite (NON, MUT, UNK)
     *
     * @return
     */
    public Difference flip() {
        switch (this) {
            case DEL:
                return INS;
            case INS:
                return DEL;
            case GEN:
                return SPE;
            case SPE:
                return GEN;
            default:
                return this;
        }
    }

    /**
     * Returns superset value if possible:
     *
     * DEL -> GEN
     * INS -> SPE
     *
     * or returns the value itself.
     * @return
     */
    public Difference formalize() {
        switch (this) {
            case DEL:
                return GEN;
            case INS:
                return SPE;
            default:
                return this;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        return sb.toString();
    }
}