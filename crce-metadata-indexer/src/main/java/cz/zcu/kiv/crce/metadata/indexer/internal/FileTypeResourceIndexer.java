package cz.zcu.kiv.crce.metadata.indexer.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FileTypeResourceIndexer extends AbstractResourceIndexer {

    private static int BUFFER_LENGTH = 8;

    private static String PNG = new String(new byte[] {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
                                                        (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A});
    private static String JPEG = new String(new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
    private static String ZIP = new String(new byte[] {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});
    
    @Override
    public String[] index(InputStream input, Resource resource) {
        System.out.println("\n--- file type ---");

        byte[] buffer = new byte[BUFFER_LENGTH];
        
        int read = 0;
        try {
            read = input.read(buffer);
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();   // XXX
            return new String[0];
        }
        
        if (read != BUFFER_LENGTH) {
            System.out.println("read: " + read);
            return new String[0];
        }

        String str = new String(buffer);
        System.out.println("str: " + str);

        if (str.startsWith(ZIP)) {
            resource.addCategory("zip");
            return new String[] {"zip"};
        } else if (str.startsWith(JPEG)) {
            resource.addCategory("jpeg");
            return new String[] {"jpeg"};
        } else if (str.startsWith(PNG)) {
            resource.addCategory("png");
            return new String[] {"png"};
        }

        return new String[0];
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    private void zip() {
        
    }

    @Override
    public String[] getProvidedCategories() {
        return new String[] {"zip", "jpeg", "png"};
    }
    
}
