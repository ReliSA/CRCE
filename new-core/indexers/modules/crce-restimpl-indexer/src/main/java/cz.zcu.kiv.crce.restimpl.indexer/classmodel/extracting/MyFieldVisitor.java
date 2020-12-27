package cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Field;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by ghessova on 31.03.2018.
 */
public class MyFieldVisitor extends FieldVisitor {

    private State state = State.getInstance();

    private Field field;

    MyFieldVisitor(Field field) {
        super(Opcodes.ASM5);
        this.field = field;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        desc = BytecodeDescriptorsProcessor.getFullClassName(desc);
        state.setAnnotation(new Annotation(desc));
        return new MyAnnotationVisitor(Opcodes.ASM5, field);
    }

    @Override
    public void visitEnd() {
        state.getClassType().addField(field);
        super.visitEnd();
    }
}