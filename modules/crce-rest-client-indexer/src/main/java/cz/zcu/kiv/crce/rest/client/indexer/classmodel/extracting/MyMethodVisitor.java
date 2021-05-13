package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;

/**
 * Inspired by ghessova on 22.01.2018.
 *
 * Method visitor.
 *
 * Extracts local variable names (formal parameters) and method body instructions.
 *
 * see: http://asm.ow2.org/doc/tutorial-asm-2.0.html
 * https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings
 * http://asm.ow2.org/asm50/javadoc/user/org/objectweb/asm/MethodVisitor.html
 */
public class MyMethodVisitor extends MethodVisitor {

    private final int minICONST = -1;
    private State state = State.getInstance();
    private Method method;
    private List<String> log = new ArrayList<>();

    MyMethodVisitor(Method method) {

        super(Opcodes.ASM5);
        this.method = method;
    }

    /*
     * Visits a local variable declaration.
     *
     * @param name the name of a local variable
     * 
     * @param desc the type descriptor of this local variable
     * 
     * @param signature the type signature of this local variable. May be null if the local variable
     * type does not use generic types.
     * 
     * @param start the first instruction corresponding to the scope of this local variable
     * (inclusive).
     * 
     * @param end the last instruction corresponding to the scope of this local variable
     * (exclusive).
     * 
     * @param index the local variable's index.
     */
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start,
            Label end, int index) {
        if ("this".equals(name)) {
            state.setNumParametersProcessed(0);
            return;
        }
        List<Variable> parameters = method.getParameters();

        if (parameters != null) {
            int paramIndex = state.getNumParametersProcessed();
            if (paramIndex < parameters.size()) {
                parameters.get(paramIndex).setName(name);
                state.setNumParametersProcessed(paramIndex + 1);
            } else if (paramIndex == parameters.size()) {
                state.setNumParametersProcessed(0);
            }

            super.visitLocalVariable(name, desc, signature, start, end, index);
        }
    }

    /**
     * {@inheritDoc}
     * 178 (0xB2) - getstatic
     * 180 (0xB4) - getfield
     */
    @Override // status value
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {

        Operation operation = new Operation(Operation.OperationType.FIELD);
        operation.setOwner(owner);
        operation.setFieldName(name);
        operation.setDesc(desc);
        operation.setOpcode(opcode);
        method.addOperation(operation);


        super.visitFieldInsn(opcode, owner, name, desc);
    }


    /*
     * Visits a method instruction. A method instruction is an instruction that invokes a method.
     *
     * @param opcode - the opcode of the type instruction to be visited. This opcode is either
     * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     * 
     * @param owner - the internal name of the method's owner class
     * 
     * @param name - the method's name
     * 
     * @param desc - the method's descriptor
     * 
     * @param itf - if the method's owner class is an interface
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {


        Operation operation = new Operation(opcode, Operation.OperationType.CALL);
        operation.setOwner(owner);
        operation.setMethodName(name);
        operation.setDesc(desc);

        method.addOperation(operation);
        operation.setDescription(owner + "." + name + "-" + desc + "-" + itf);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that takes the internal name
     * of a class as parameter.
     * 
     * @param opcode - the opcode of the type instruction to be visited. This opcode is either NEW,
     *               ANEWARRAY, CHECKCAST or INSTANCEOF
     * @param type   - the operand of the instruction to be visited. This operand must be the
     *               internal name of an object or array class
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {

        if (opcode == Opcodes.ANEWARRAY) {
            Operation operation = new Operation(opcode, Operation.OperationType.ANEWARRAY);
            operation.setDesc(type);
            method.addOperation(operation);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationVisitor visitParameterAnnotation(int paramIndex, String desc, boolean b) {
        return super.visitParameterAnnotation(paramIndex, desc, b);
    }

    /**
     * LDC - push a constant #index from a constant pool (String, int, float, Class,
     * java.lang.invoke.MethodType, or java.lang.invoke.MethodHandle) onto the stack
     * 
     * @param o constant from pool
     */
    @Override
    public void visitLdcInsn(Object o) {
        Operation operation = new Operation(Operation.OperationType.STRING_CONSTANT);
        operation.setValue("" + o);
        operation.setDescription("constant from pool: " + String.valueOf(o));
        method.addOperation(operation);

        super.visitLdcInsn(o);
    }


    /*
     * Visits an instruction with a single int operand: .
     *
     * 16 (0x10) - bipush - push a byte onto the stack as an integer value 17 (0x11) - sipush - push
     * a short onto the stack as an integer value
     * 
     * @param opcode BIPUSH, SIPUSH, or NEWARRAY
     * 
     * @param operand operand
     */
    @Override
    public void visitIntInsn(int opcode, int operand) {
        Operation operation = new Operation(opcode, Operation.OperationType.INT_CONSTANT);
        operation.setDataType("I");
        operation.setValue("" + operand);
        operation.setDescription("int: " + String.valueOf(operand));
        method.addOperation(operation);
        super.visitIntInsn(opcode, operand);
    }

    /**
     * Visits a local variable instruction: ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE,
     * FSTORE, DSTORE, ASTORE, or RET.
     *
     * 21 (0x15) - iload - oad an int value from a local variable #index 25 (0x19) - aload - load a
     * reference onto the stack from a local variable #index 58 (0x3A) - astore - store a reference
     * into a local variable #index
     *
     * 21 - 53 .. load 54 - 86 .. store
     *
     * see the org.objectweb.asm.Opcode interface for constants definitions
     *
     * @param opcode instruction code (decimal)
     * @param var    operand
     */
    @Override
    public void visitVarInsn(int opcode, int var) {
        Operation operation;
        if (opcode >= Opcodes.ILOAD && opcode <= Opcodes.SALOAD) { // load
            log.add("load(" + opcode + "): " + var);
            operation = new Operation(opcode, Operation.OperationType.LOAD);
            operation.setDescription("load(" + opcode + "): " + var);

            operation.setIndex(var);
        } else if (Opcodes.ISTORE >= 54 && opcode <= Opcodes.SASTORE) { // store
            operation = new Operation(opcode, Operation.OperationType.STORE);
            operation.setIndex(var);
            operation.setDescription("store(" + opcode + "): " + var);

            log.add("store(" + opcode + "): " + var);
        } else {
            operation = new Operation(opcode, Operation.OperationType.OTHER);
            operation.setIndex(var);
            operation.setDescription("operation(" + opcode + "): " + var);
            log.add("operation(" + opcode + "): " + var);
        }
        method.addOperation(operation);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        // log.add("jump(" + opcode + "): " + label);


        /*        logger.debug(
                "        Jump-Instruction[" + "opcode=" + opcode + ", " + "label=" + label + "]");*/

        method.addOperation(new Operation(opcode, Operation.OperationType.JUMP));

        super.visitJumpInsn(opcode, label);

    }

    /*
     * 172 (0xAC) - ireturn 176 (0xB0) - areturn 177 (0xB1) - return (return void from method)
     *
     * Visits a zero operand instruction.
     *
     * @param instrCode
     */
    @Override
    public void visitInsn(int opcode) { // opcode 176 is return
        String type = "unknown";
        OperationType operationType = null;
        String value = null;
        switch (opcode) {
            case Opcodes.IRETURN:
                type = "ireturn";
                operationType = OperationType.RETURN;
                break;
            case Opcodes.ARETURN:
                type = "areturn";
                operationType = OperationType.RETURN;
                break;
            case Opcodes.RETURN:
                type = "return";
                operationType = OperationType.RETURN;
            case Opcodes.DUP:
                type = "dup";
                operationType = OperationType.DUP;
                break;
            case Opcodes.AASTORE:
                type = "aastore";
                operationType = OperationType.STORE;
                break;
            case Opcodes.ACONST_NULL:
                operationType = OperationType.STRING_CONSTANT;
                break;
            default:
                if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
                    operationType = OperationType.INT_CONSTANT;
                    value = (opcode - Opcodes.ICONST_M1 + minICONST) + "";
                } else if (opcode >= Opcodes.LCONST_0 && opcode <= Opcodes.LCONST_1) {
                    operationType = OperationType.INT_CONSTANT;
                    value = (opcode - Opcodes.LCONST_0) + "";
                } else if (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2) {
                    operationType = OperationType.INT_CONSTANT;
                    value = (opcode - Opcodes.FCONST_0) + "";
                } else if (opcode >= Opcodes.DCONST_0 && opcode <= Opcodes.DCONST_1) {
                    operationType = OperationType.INT_CONSTANT;
                    value = (opcode - Opcodes.DCONST_0) + "";
                }
        }
        if (operationType != null) {
            Operation op = new Operation(opcode, operationType);
            if (value != null) {
                op.setValue(opcode - Opcodes.ICONST_M1 + minICONST);
            }
            method.addOperation(op);
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

}
