package cz.zcu.kiv.crce.metadata.combined;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static org.junit.Assert.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class Util {
    
    public static File prepareFile(File dir, String file) {
        File resource = new File("src/test/resources/" + file);
        assert resource.exists() : "Resource file not exists : " + resource.getAbsolutePath();

        File temp = new File(dir, file);

        copyfile(resource, temp);
        
        return temp;
    }

    public static File createTempDir() {
        final String baseTempPath = System.getProperty("java.io.tmpdir");

        File tempDir;

        do {
            tempDir = new File(baseTempPath, "crcetest" + System.nanoTime());
        } while (tempDir.exists());

        tempDir.mkdir();
        tempDir.deleteOnExit();

        return tempDir;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void copyfile(File f1, File f2) {
        try {
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            fail("File not found: " + f1.getAbsolutePath());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
