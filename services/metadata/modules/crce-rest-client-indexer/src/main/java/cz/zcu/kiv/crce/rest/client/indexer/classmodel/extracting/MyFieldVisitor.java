package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;

public class MyFieldVisitor extends FieldVisitor {

    private State state = State.getInstance();
    private Field field;

    /**
     * Init
     * @param field Field
     */
    public MyFieldVisitor(Field field) {
        super(Opcodes.ASM7);
        this.field = field;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        state.getClassStruct().addField(field);
        super.visitEnd();
    }

}
