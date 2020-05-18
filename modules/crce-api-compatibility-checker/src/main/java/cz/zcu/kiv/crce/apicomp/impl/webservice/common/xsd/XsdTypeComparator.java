package cz.zcu.kiv.crce.apicomp.impl.webservice.common.xsd;

import cz.zcu.kiv.crce.compatibility.Difference;

public class XsdTypeComparator {

    public static final String XSD_PREFIX = "xs:";

    private static XsdDataType[] intLikeTypes;

    private static XsdDataType[] unsignedIntLikeTypes;

    private static XsdDataType[] floatLikeTypes;

    static {
        intLikeTypes = new XsdDataType[] {
                new XsdDataType("byte", 0),
                new XsdDataType("short", 1),
                new XsdDataType("int", 2),
                new XsdDataType("long", 3),
        };

        unsignedIntLikeTypes = new XsdDataType[] {
                new XsdDataType("unsignedByte", 0),
                new XsdDataType("unsignedShort", 1),
                new XsdDataType("unsignedInt", 2),
                new XsdDataType("unsignedLong", 3),
        };

        floatLikeTypes = new XsdDataType[] {
                new XsdDataType("float", 0),
                new XsdDataType("double", 1)
        };
    }

    public static boolean isXsdDataType(String typeName) {
        return !typeName.isEmpty() && typeName.toLowerCase().startsWith(XSD_PREFIX);
    }

    /**
     * Compares two data types. It is assumed that both are xsd data types, if not, null is returned.
     *
     * @param typeName1
     * @param typeName2
     * @return
     */
    public static Difference compareTypes(String typeName1, String typeName2) {
        if (!isXsdDataType(typeName1) || !isXsdDataType(typeName2)) {
            return null;
        }

        if (typeName1.equals(typeName2)) {
            return Difference.NON;
        }

        String strippedT1 = typeName1.toLowerCase().substring(XSD_PREFIX.length());
        String strippedT2 = typeName2.toLowerCase().substring(XSD_PREFIX.length());

        Difference result = tryCompareTypesFromCategory(strippedT1, strippedT2, intLikeTypes);
        if (result != null) {
            return result;
        }

        result = tryCompareTypesFromCategory(strippedT1, strippedT2, unsignedIntLikeTypes);
        if (result != null) {
            return result;
        }

        result = tryCompareTypesFromCategory(strippedT1, strippedT2, floatLikeTypes);
        if (result != null) {
            return result;
        }

        // types are not from the same comparable category and are not equal
        return Difference.UNK;
    }

    private static Difference tryCompareTypesFromCategory(String strippedT1, String strippedT2, XsdDataType[] typeCategory) {

        XsdDataType t1 = findTypeInCategory(strippedT1, typeCategory);
        XsdDataType t2 = findTypeInCategory(strippedT2, typeCategory);

        if (t1 == null || t2 == null) {
            return null;
        }

        if (t1.order == t2.order) {
            return Difference.NON;
        } else if (t1.order < t2.order) {
            return Difference.GEN;
        } else {
            return Difference.SPE;
        }
    }

    private static XsdDataType findTypeInCategory(String typeName, XsdDataType[] typeCategory) {
        for (XsdDataType intLikeType: typeCategory) {
            if (intLikeType.name.equals(typeName)) {
                return intLikeType;
            }
        }
        return null;
    }

}
