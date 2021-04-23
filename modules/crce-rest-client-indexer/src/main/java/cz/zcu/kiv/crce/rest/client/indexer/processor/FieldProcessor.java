package cz.zcu.kiv.crce.rest.client.indexer.processor;

import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

public class FieldProcessor extends MethodProcessor {

    public static final String INIT_STATIC = "<clinit>";
    public static final String INIT = "<init>";

    public FieldProcessor(ClassMap classes) {
        super(classes);
    }

    /**
     * Processes fields of given class
     * 
     * @param class_ Class which will be processed
     */
    public void process(ClassWrapper class_) {

        MethodWrapper cInit = class_.getMethod(INIT_STATIC);
        MethodWrapper init = class_.getMethod(INIT);
        if (cInit != null) {
            super.process(cInit);
            class_.removeMethod(INIT_STATIC);
        }

        if (init != null) {
            super.process(init);
            class_.removeMethod(INIT);
        }
    }
}
