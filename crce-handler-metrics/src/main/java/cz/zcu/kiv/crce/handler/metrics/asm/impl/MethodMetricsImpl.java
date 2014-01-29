package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * MethodMetrics implementation class. Collecting information from MethodNode.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class MethodMetricsImpl extends AbstractMethodMetrics  {
	
	private String className;
	private String methodName;
	private Type[] parameters;
	
	private boolean isPublic;
	private boolean isAbstract;
	
	private MethodMetrics[] methodCalls;

	/**
	 * New instance.
	 * 
	 * @param className Name of class (full) where method is defined.
	 * @param isPublic Method public modifier.
	 * @param methodNode ASM MethodNode to parse information.
	 */
	public MethodMetricsImpl(@Nonnull String className, boolean isPublic, @Nonnull MethodNode methodNode) {
		
		Type methodType = Type.getType(methodNode.desc);
		
		this.className = className;

		methodName = methodNode.name;
    	parameters = methodType.getArgumentTypes();
    	
    	this.isPublic = isPublic;
		
		Set<MethodMetrics> calls = new HashSet<MethodMetrics>();
		
		// collecting method calls
		@SuppressWarnings("unchecked")
		ListIterator<AbstractInsnNode> instructions = methodNode.instructions.iterator();
		// abstract method has no instruction. Each non-abstract method has at least return statement.
		if (instructions.hasNext()) {
			isAbstract = false;
			
			while (instructions.hasNext()) {
				
				AbstractInsnNode instruction = instructions.next();			
				if (instruction.getType() == AbstractInsnNode.METHOD_INSN) {
	            	
					MethodInsnNode callInstruction = (MethodInsnNode)instruction;
	            	
	            	String owner =  callInstruction.owner.replace('/','.');
	            	String name = callInstruction.name;
	            	String desc = callInstruction.desc;
	
	            	Type callMethodType = Type.getType(desc);
	
	            	// using placeholders for all method calls
	            	MethodMetrics methodCall = new ExternalMethodMetrics(owner, name, callMethodType.getArgumentTypes());
	            	
	            	calls.add(methodCall);
				}
			}			
		} 
		else {
			isAbstract = true;
		}

		methodCalls = new MethodMetrics[0];
		if (calls.size() > 0) {
			methodCalls = calls.toArray(methodCalls);
		}
	}

	@Override
	@Nonnull
	public String getClassName() {
		return className;
	}

	@Override
	@Nonnull
	public String getName() {
		return methodName;
	}

	@Override
	@Nonnull
	public Type[] getParameters() {
		return (Type[])parameters.clone();
	}

	@Override
	@Nonnull
	public MethodMetrics[] getMethodCalls() {
		return (MethodMetrics[])methodCalls.clone();
	}
	
	@Override
	public void replaceMethodCall(int index, @Nonnull MethodMetrics methodCall) {
		
		if (index < 0 || index >= methodCalls.length) {
			throw new IndexOutOfBoundsException();
		}
	
		methodCalls[index] = methodCall;
	}
	
	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public boolean isInternal() {
		return true;
	}
	
	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
}
