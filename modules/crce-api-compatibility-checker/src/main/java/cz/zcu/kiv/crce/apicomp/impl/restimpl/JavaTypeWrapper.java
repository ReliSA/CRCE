package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import java.util.Objects;

/**
 * Class that wraps box or primitive type.
 *
 * Expected primitive type names:
 *
 *  - char: C
 *  - byte: B
 *  - short: S
 *  - int: I
 *  - long: J
 *  - float: F
 *  - double: D
 *
 */
// todo: test
public class JavaTypeWrapper {

    private final static String[] primitiveTypeNames = new String[]{
            "C", "B", "S", "I", "J", "F", "D"
    };

    private final static String[] boxTypeNames = new String[]{
            "java/lang/Character", "java/lang/Byte", "java/lang/Short", "java/lang/Integer", "java/lang/Long", "java/lang/Float", "java/lang/Double"
    };

    private final String typeName;

    public JavaTypeWrapper(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Checks whether this type fits into the other type.
     * Examples:
     * byte fits into short
     * int fits into long
     * float fits into double
     *
     * @param otherType Other type.
     * @return True if this type fits into the other one.
     */
    public boolean fitsInto(JavaTypeWrapper otherType) {
        // todo
        return false;
    }

    /**
     * Checks if this type is comparable = either primitive or java-lang.*type.
     *
     * @return True if the type is comparable.
     */
    public boolean isComparableType() {
        for (int i = 0; i < primitiveTypeNames.length; i++) {
            if (primitiveTypeNames[i].equals(typeName) || boxTypeNames[i].equals(typeName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // todo

        JavaTypeWrapper that = (JavaTypeWrapper) o;
        return Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        // todo
        return Objects.hash(typeName);
    }
}
