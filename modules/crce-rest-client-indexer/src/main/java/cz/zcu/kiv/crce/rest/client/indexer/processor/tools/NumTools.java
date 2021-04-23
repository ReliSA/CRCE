package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.regex.Pattern;

public class NumTools {
    final private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    // @source:https://www.baeldung.com/java-check-string-number
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
