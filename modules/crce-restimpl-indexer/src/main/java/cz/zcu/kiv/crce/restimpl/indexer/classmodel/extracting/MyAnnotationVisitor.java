package cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotated;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import org.objectweb.asm.AnnotationVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ghessova on 22.01.2018.
 */
public class MyAnnotationVisitor extends AnnotationVisitor {

    private State state;
    private Annotated target;
    private String keyForArrayValue;

    MyAnnotationVisitor(int opcodes, Annotated target) {
        super(opcodes);
        state = State.getInstance();
        this.target = target;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, String s1) {

        return super.visitAnnotation(s, s1);
    }

    @Override
    public AnnotationVisitor visitArray(String key) {
        keyForArrayValue = key;
        return this; // must not return null (null means that we are not interested in array values)
    }



    @Override
    public void visit(String key, Object value) {
        Annotation annotation = state.getAnnotation();
        if (key != null) {
            state.getAnnotation().setValue(key, value);
        }
        else { // key is null when processing array value
            Set<Object> values = (Set<Object>)annotation.getValues().get(keyForArrayValue);
            if (values == null) {
                values = new HashSet<>();
                annotation.getValues().put(keyForArrayValue, values);
            }
            values.add(value);

        }
    }

    @Override
    public void visitEnum(String s, String enumClass, String enumValue) {
        Annotation annotation = state.getAnnotation();

        Set<Object> values = (Set<Object>)annotation.getValues().get(keyForArrayValue);
        if (values == null) {
            values = new HashSet<>();
            annotation.getValues().put(keyForArrayValue, values);
        }
        values.add(enumValue);
    }



    @Override
    public void visitEnd() {
        target.addAnnotation(state.getAnnotation());
        super.visitEnd();

    }


}
