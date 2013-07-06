package cz.zcu.kiv.crce.metadata.dao.internal.type;

import cz.zcu.kiv.crce.metadata.Operator;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum DbOperator {

    EQUAL(Operator.EQUAL, (short) 0),
    NOT_EQUAL(Operator.NOT_EQUAL, (short) 1),
    LESS(Operator.LESS, (short) 2),
    LESS_EQUAL(Operator.LESS_EQUAL, (short) 3),
    GREATER(Operator.GREATER, (short) 4),
    GREATER_EQUAL(Operator.GREATER_EQUAL, (short) 5),
    SUBSET(Operator.SUBSET, (short) 6),
    SUPERSET(Operator.SUPERSET, (short) 7),
    PRESENT(Operator.PRESENT, (short) 8),
    APPROX(Operator.APPROX, (short) 9);

    private final Operator operator;
    private final short dbValue;

    private DbOperator(Operator operator, short dbValue) {
        this.operator = operator;
        this.dbValue = dbValue;
    }

    public static short getDbValue(Operator operator) {
        for (DbOperator value : values()) {
            if (value.operator.equals(operator)) {
                return value.dbValue;
            }
        }
        throw new IllegalArgumentException("Invalid operator: " + operator);
    }

    public static Operator getOperatorValue(int dbValue) {
        for (DbOperator value : values()) {
            if (value.dbValue == dbValue) {
                return value.operator;
            }
        }
        throw new IllegalArgumentException("Invalid operator DB value: " + dbValue);
    }
}
