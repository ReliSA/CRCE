package cz.zcu.kiv.crce.crce_webui_v2.collection.classes;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * Random text string generator. Used to distinguish collection items with the same name in a Vaadin Tree object.
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class RandomStringGenerator {
    private Random random;
    private byte[] array;

    public RandomStringGenerator() {
        random = new Random();
        array = new byte[7]; // length is bounded by 7
    }

    public String getRandomString() {
        random.nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }
}
