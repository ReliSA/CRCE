package cz.zcu.kiv.crce.crce_webui_v2.collection.classes;

import java.nio.charset.Charset;
import java.util.Random;

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
