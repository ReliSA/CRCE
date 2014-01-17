package cz.zcu.kiv.crce.handler.metrics.internal;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodMetrics extends AbstractMethodMetrics  {
	
	private String className;
	private String methodName;
	private Type[] parameters;
	
	private boolean isPublic;
	private boolean isAbstract;
	
	private IMethodMetrics[] methodCalls;

	public MethodMetrics(String className, boolean isPublic, MethodNode methodNode) {
		
		Type methodType = Type.getType(methodNode.desc);
		
		this.className = className;

		methodName = methodNode.name;
    	parameters = methodType.getArgumentTypes();
    	
    	this.isPublic = isPublic;
		
		Set<IMethodMetrics> calls = new HashSet<IMethodMetrics>();
		
		@SuppressWarnings("unchecked")
		ListIterator<AbstractInsnNode> instructions = methodNode.instructions.iterator();
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
	
	            	IMethodMetrics methodCall = new ExternalMethodMetrics(owner, name, callMethodType.getArgumentTypes());
	            	
	            	calls.add(methodCall);
				}
			}			
		} 
		else {
			isAbstract = true;
		}

		methodCalls = new IMethodMetrics[0];
		if (calls.size() > 0) {
			methodCalls = calls.toArray(methodCalls);
		}
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getName() {
		return methodName;
	}

	@Override
	public Type[] getParameters() {
		return (Type[])parameters.clone();
	}

	@Override
	public IMethodMetrics[] getMethodCalls() {
		return (IMethodMetrics[])methodCalls.clone();
	}
	
	@Override
	public void replaceMethodCall(int index, IMethodMetrics methodCall) {
		
		if (index < 0 || index >= methodCalls.length) {
			throw new IndexOutOfBoundsException();
		}
		
		if (methodCall == null) {
			throw new NullPointerException();
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
