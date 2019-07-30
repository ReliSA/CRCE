package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum Operator {

    EQUAL("equal"),
    NOT_EQUAL("not-equal"),
    LESS("less"),
    LESS_EQUAL("less-equal"),
    GREATER("greater"),
    GREATER_EQUAL("greater-equal"),
    SUBSET("subset"),
    SUPERSET("superset"),
    PRESENT("present"),
    APPROX("approx");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(Operator operator) {
        return operator.value;
    }

    public static Operator fromValue(String value) {
        for (Operator operator : values()) {
            if (operator.value.equals(value)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Invalid operator value: " + value);
    }
}
