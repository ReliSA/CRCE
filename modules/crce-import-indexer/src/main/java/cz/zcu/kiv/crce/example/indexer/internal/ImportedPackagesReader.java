package cz.zcu.kiv.crce.example.indexer.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents constant pool reader which can read some types of constants from constant pool
 * and give set of used packages in some .class file.
 *
 * @author vit.mazin@seznam.cz
 */
public class ImportedPackagesReader {

    private static Logger logger = LoggerFactory.getLogger(ImportedPackagesReader.class);
    private DataInput s;
    private Object[] constantPool;
    private File sourceFile;

    public ImportedPackagesReader(File file) throws IOException {
        sourceFile = file;
        s = new DataInputStream(new ByteArrayInputStream(Files.readAllBytes(file.toPath())));
    }

    /**
     * Reads constant pool into array of objects.
     */
    public void readConstantPool() {
        try {
            s.skipBytes(8); //Magic number, minor_version, major_version

            //number of constants in pool
            int size = s.readUnsignedShort() - 1;

            constantPool = new Object[size];

            //reading all constants from constanf pool
            for (int i = 0; i < size; i++) {
                byte tag = s.readByte();

                switch (tag) {
                    case 1: // UTF constant
                        constantPool[i] = s.readUTF();
                        break;
                    case 3: //integer
                    case 4: //float
                    case 9: //fieldref
                    case 10: //Methodref
                    case 11: //InterfaceMethodref
                    case 18:// CONSTANT_INVOKE_DYNAMIC
                        s.skipBytes(4);
                        break;
                    case 5: //long
                    case 6: //double
                        s.skipBytes(8);
                        i++; //from specification - after 8 byte const. there is one unused index
                        break;
                    case 7: //class
                        constantPool[i] = new Class_constant(readIndex());
                        break;
                    case 8: //string
                    case 16://MethodType
                        s.skipBytes(2);
                        break;
                    case 12: //NameAndType
                        constantPool[i] = new Name_and_type_constant(readIndex(), readIndex());
                        break;
                    case 15:// MethodHandle
                        s.skipBytes(3);
                        break;
                    default:
                        logger.warn("Unrecognized tag " + tag + " at index " + i);
                        break;
                }
            }

        } catch (Exception e) {
            logger.warn("Corrupted DataInput from file " + sourceFile.getAbsolutePath());
        }
    }

    /**
     * Returns set of imported packages from all constants in constant pool.
     *
     * @return set of imported packages
     */
    public Set<String> getImportedPackages() {
        Set<String> result = new HashSet<>();
        for (Object o : constantPool) {
            if (o instanceof ClassConstant) {
                ClassConstant cc = (ClassConstant)o;
                String str = cc.getFullClassName(constantPool);
                if (str != null) {
                    String clazz = getPackageFromClass(str);
                    if (clazz != null) {
                        result.add(clazz);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Return package string of current .class file.
     *
     * @return package string of current file
     */
    public String getPackageOfCurrentFile() {
        try {
            s.skipBytes(2);
            Class_constant cc = (Class_constant) constantPool[readIndex()];
            String str = cc.getFullClassName(constantPool);

            if (str != null) {
                return getPackageFromClass(str);
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.warn("Corrupted DataInput from file " + sourceFile.getAbsolutePath());
            return null;
        }
    }

    /**
     * Create string with only package from class string.
     *
     * @param clazz - class string
     * @return package string
     */
    private String getPackageFromClass(String clazz) {
        int idx = clazz.lastIndexOf('.');
        if (idx == -1)
            return null;

        return clazz.substring(0, idx);
    }

    /**
     * Reads index which represents pointer in constant pool to another constant (usually pointer to UTF string)
     *
     * @return index in integer representation
     * @throws IOException - if given DataInput is corrupted
     */
    private int readIndex() throws IOException {
        return s.readUnsignedShort() - 1; //idxs in constant pool are from 1 but we works with idxs from 0
    }

    private static class Class_constant implements ClassConstant {
        int string_index; //pointer to UTF constant
        Class_constant(int i) {
            string_index = i;
        }

        /**
         * Returns full class name of constant
         *
         * @param constantPool - array which represents constant pool
         * @return full class name (with all packages) as string
         */
        @Override
        public String getFullClassName(Object[] constantPool) {
            Object o = constantPool[string_index];
            if (!(o instanceof String)) {
                return null;
            }

            String str = (String)o;

            //array of objects
            while (str.charAt(0) == '[')
                str = str.substring(1);

            //primitive
            if (str.length() == 1)
                return null;

            if (str.charAt(0) == 'L' && str.charAt(str.length() - 1) == ';')
                str = str.substring(1, str.length() - 1);

            str = str.replace('/', '.');
            return str;
        }
    }

    private static class Name_and_type_constant implements ClassConstant {
        int name;
        int descriptor; //class as type
        Name_and_type_constant(int name_i, int descriptor_i) {
            name = name_i;
            descriptor = descriptor_i;
        }

        /**
         * Returns full class name of constant
         *
         * @param constantPool - array which represents constant pool
         * @return full class name (with all packages) as string
         */
        @Override
        public String getFullClassName(Object[] constantPool) {
            Object o = constantPool[descriptor];
            if (!(o instanceof String)) {
                return null;
            }

            String str = (String)o;

            if (str.contains(")V") || str.contains(")Z"))
                return null;

            int idx = 0;
            while ((idx = str.indexOf('L', idx)) != -1) {
                int semi = str.indexOf(';', idx);
                if (semi == -1) {
                    return null;
                }
                str = str.substring(idx + 1, semi);
                idx = semi;
            }

            str = str.replace('/', '.');
            return str;
        }
    }
}
