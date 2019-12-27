package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import javax.validation.constraints.NotNull;

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

    /**
     * Index into the type arrays. Used to compare types between each other.
     * -1 if type is not in any array.
     */
    private int typeIndex;

    public JavaTypeWrapper(@NotNull String typeName) {
        this.typeName = typeName;

        typeIndex = -1;
        for (int i = 0; i < primitiveTypeNames.length; i++) {
            if (primitiveTypeNames[i].equals(typeName) || boxTypeNames[i].equals(typeName)) {
                typeIndex = i;
                break;
            }
        }
    }

    /**
     * Checks whether this type fits into the other type.
     * Examples:
     * byte fits into short
     * int fits into long
     * float fits into double
     *
     * @param otherType Other type. Must not be null.
     * @return True if this type fits into the other one.
     */
    public boolean fitsInto(@NotNull JavaTypeWrapper otherType) {
        if (!isComparableType() || !otherType.isComparableType()) {
            return false;
        }

        // same types fits into each other
        if (typeIndex == otherType.typeIndex) {
            return true;
        }

        // both types are byte, short, int or long
        // or both types are float, double
        // then type fits if its index is < than the other
        if ((typeIndex >= 1 && otherType.typeIndex <= 4 )
                || (typeIndex >= 5 && otherType.typeIndex <= 6)
        ) {
            return typeIndex < otherType.typeIndex;
        }

        // any other case
        return false;
    }

    /**
     * Checks if this type is comparable = either primitive or java-lang.*type.
     *
     * @return True if the type is comparable.
     */
    public boolean isComparableType() {
        return typeIndex != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return typeIndex == ((JavaTypeWrapper)o).typeIndex;
    }

    @Override
    public int hashCode() {
        return typeIndex;
    }
}
