package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class Loader {
    /**
     * Loads classes from Jar
     * @param jarFile
     * @throws IOException
     */
    public static void loadClasses(File jarFile) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Stream<JarEntry> str = jar.stream();
        str.forEach(z -> readJar(jar, z));
        jar.close();
    }

    /**
     * Loads classes from ZipInputStream
     * @param jis
     * @throws IOException
     */
    public static void loadClasses(ZipInputStream jis) throws IOException {
        for (ZipEntry e = jis.getNextEntry(); e != null; e = jis.getNextEntry()) {
            if (e.getName().endsWith(".class")) {
                processJARInputStream(jis);
            }
        }
    }

    /**
     * Visits all classes
     * @param jis
     * @throws IOException
     */
    public static void processJARInputStream(InputStream jis) throws IOException {
        MyClassVisitor classVisitor = new MyClassVisitor(Opcodes.ASM7, null);
        ClassReader classReader = new ClassReader(getEntryInputStream(jis));
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
    }

    /**
     * Reads jar and its classes
     * @param jar Jar
     * @param entry
     */
    static void readJar(JarFile jar, JarEntry entry) {
        String name = entry.getName();
        try (InputStream jis = jar.getInputStream(entry)) {
            if (name.endsWith(".class")) {
                processJARInputStream(jis);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Process Input stream
     * @param jis
     * @return
     * @throws IOException
     */
    private static InputStream getEntryInputStream(InputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = jis.read(buf, 0, buf.length)) > 0) {
            baos.write(buf, 0, n);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
