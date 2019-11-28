package cz.zcu.kiv.crce.example.indexer.internal;

/**
 * This interface represents constant in constant pool which uses some class.
 * @author vit.mazin@seznam.cz
 */
public interface ClassConstant {

    /**
     * Returns full class name of constant
     *
     * @param constantPool - array which represents constant pool
     * @return full class name (with all packages) as string
     */
    String getFullClassName(Object[] constantPool);
}
