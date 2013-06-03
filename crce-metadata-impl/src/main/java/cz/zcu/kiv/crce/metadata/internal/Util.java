package cz.zcu.kiv.crce.metadata.internal;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import cz.zcu.kiv.crce.metadata.EqualityComparable;
import cz.zcu.kiv.crce.metadata.EqualityLevel;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Util {

    public static <T extends EqualityComparable<T>> boolean equalsTo(T a, T b, EqualityLevel level) {
        return (a == b) || (a != null && a.equalsTo(b, level)); // NOPMD better clarity
    }

    public static <T extends EqualityComparable<T>> boolean equalsTo(List<T> a, List<T> b, EqualityLevel level) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        Iterator<T> it1;
        Iterator<T> it2;
        if (level == EqualityLevel.SHALLOW_WITH_KEY || level == EqualityLevel.DEEP_WITH_KEY) {
            TreeSet<T> set1 = new TreeSet<>(a);
            TreeSet<T> set2 = new TreeSet<>(b);
            if (set1.size() != set2.size()) {
                return false;
            }
            it1 = set1.iterator();
            it2 = set2.iterator();
        } else {
            it1 = a.iterator();
            it2 = b.iterator();
        }
        /*
         * FIXME this way of comparison requires the same order of list elements in case of "no key" comparison depth level which is wrong,
         * another algorithm needs to be used.
         */
        while (it1.hasNext() && it2.hasNext()) {
            T o1 = it1.next();
            T o2 = it2.next();
            if (!equalsTo(o1, o2, level)) {
                return false;
            }
        }
        return true;
    }
}
