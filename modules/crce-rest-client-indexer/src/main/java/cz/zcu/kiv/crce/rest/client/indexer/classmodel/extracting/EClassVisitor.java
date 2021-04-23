package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.Collector;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;

public class EClassVisitor extends ClassVisitor {

    //static Logger log = LogManager.getLogger("extractor");

    private State state = State.getInstance();

    public EClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
/*        log.debug("============Class-Visitor[name=" + name + " extends=" + superName + " signature="
                + signature + "]===");*/
        ClassStruct class_ = new ClassStruct(name, superName, signature);
        state.setClassType(class_);
        super.visit(version, access, name, signature, superName, interfaces);

    }

    @Override
    public void visitEnd() {
/*        log.debug("==========" + "END-Class-Visitor[" + State.getInstance().getClassType().getName()
                + "]" + "==================\n\n\n");*/
        Collector.getInstance().addClass(State.getInstance().getClassType());
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
            Object value) {
/*        log.debug("    Field-Visitor[access=" + access + ", name=" + name + ", desc=" + desc
                + ", signature=" + signature + ", value=" + value + "]");*/

        DataType dataType = BytecodeDescriptorsProcessor.processFieldDescriptor(desc);
        // TODO: be able to process List<Object> and List<List<Object>> ??
        Field field = new Field(dataType);
        field.setName(name);
        field.setAccess(access);
        field.setSignature(signature);
        return new MyFieldVisitor(field);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        // @source
        // https://stackoverflow.com/questions/47000699/how-to-extract-access-flags-of-a-field-in-asm-visitfield-method
/*        log.debug("\n    ==========Method-Visitor[name=" + name + " CLINIT="
                + (name.equals("<clinit>") ? "TRUE" : "FALSE") + "]===");*/
        Method newMethod = new Method(access, name, descriptor);
        state.getClassType().addMethod(newMethod);
        MethodVisitor mv = new MyMethodVisitor(newMethod);

        return mv;
        // return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}
