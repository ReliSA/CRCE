package cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Annotation;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Method;
import org.objectweb.asm.*;

/**
 * Created by ghessova on 22.01.2018.
 */
public class MyClassVisitor extends ClassVisitor {

    private State state = State.getInstance();

    public MyClassVisitor(int i) {
        super(i);
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        ClassStruct clazz = new ClassStruct(name, superName);
        clazz.setInterfaces(interfaces);
        state.setClassType(clazz);
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc,
                                             boolean visible) {

        desc = BytecodeDescriptorsProcessor.getFullClassName(desc);
        state.setAnnotation(new Annotation(desc));

        return new MyAnnotationVisitor(Opcodes.ASM5, state.getPathPart());
    }


    @Override
    public FieldVisitor visitField(int access, String name,
                                   String desc, String signature, Object value) {

        DataType dataType = BytecodeDescriptorsProcessor.processFieldDescriptor(desc);
        Field field = new Field(dataType);
        field.setName(name);
        field.setAccess(access);
        return new MyFieldVisitor(field);
    }

    @Override
    public void visitEnd() {
        ResultCollector.getInstance().addClass(State.getInstance().getClassType());
        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name,
                                     String desc, String signature, String[] exceptions) {
        Method method = new Method(access, name, desc);
        method.setExceptions(exceptions);
        String descriptor = signature == null ? desc : signature;
        BytecodeDescriptorsProcessor.processMethodDescriptor(descriptor, method);
        state.getClassType().addMethod(method);
        state.setPathPart(method);
        return new MyMethodVisitor(method);
    }

}