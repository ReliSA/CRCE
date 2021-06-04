package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

/*
 * import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
 */
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.Collector;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;

public class MyClassVisitor extends ClassVisitor {

    private static final Logger logger = LoggerFactory.getLogger(MyClassVisitor.class);
    private State state = State.getInstance();
    private ClassStruct lastClass = null;

    public MyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
        ClassStruct class_ = new ClassStruct(name, superName, signature, interfaces);
        lastClass = class_;
        state.setClassStruct(class_);
        super.visit(version, access, name, signature, superName, interfaces);

    }

    @Override
    public void visitEnd() {
        Collector.getInstance().addClass(State.getInstance().getClassStruct());
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
            Object value) {
        DataType dataType = BytecodeDescriptorsProcessor.processFieldDescriptor(desc);
        Field field = new Field(dataType);
        field.setName(name);
        field.setSignature(signature);
        return new MyFieldVisitor(field);
    }

    // @source
    // https://stackoverflow.com/questions/47000699/how-to-extract-access-flags-of-a-field-in-asm-visitfield-method
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {

        Method newMethod = new Method(access, name, descriptor, lastClass.getName());
        state.getClassStruct().addMethod(newMethod);
        MethodVisitor mv = new MyMethodVisitor(newMethod);
        logger.info("[" + lastClass.getName() + "] method=" + newMethod.getName());
        return mv;
    }

}
