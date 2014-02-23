package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;

import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * External MethodMetrics class. Representing methods out of jar or can be used as placeholder.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class ExternalMethodMetrics extends AbstractMethodMetrics {

	private final String className;
	private final String methodName;
	private final Type[] parameters;
	
	/**
	 * New instance.
	 * 
	 * @param className Name of class (full) where method is defined.
	 * @param methodName Name of method.
	 * @param parameters Array of parameters types.
	 */
	public ExternalMethodMetrics(@Nonnull String className, @Nonnull String methodName, @Nonnull Type[] parameters) {

		this.className = className;
		this.methodName = methodName;
		this.parameters = (Type[])parameters.clone();
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
	public FieldMetrics[] getUsedInClassFields() {
		return new FieldMetrics[0];
	}
	
	@Override
	public FieldMetrics[] getUsedOutClassFields() {
		return new FieldMetrics[0];
	}

	@Override
	public MethodMetrics[] getMethodCalls() {
		return new MethodMetrics[0];
	}
	
	@Override
	public void replaceUsedOutClassField(int index, FieldMetrics usedOutClassField) {
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public void replaceMethodCall(int index, MethodMetrics methodCall) {
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public boolean isPublic () {
		return true;
	}

	@Override
	public boolean isInternal() {
		return false;
	}	
	
	@Override
	public boolean isAbstract() {
		return true;
	}	
	
	@Override
	public int getCyclomaticComplexity() {
		return 0;
	}
}
