package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Type;

public class ExternalMethodMetrics extends AbstractMethodMetrics {

	private final String className;
	private final String methodName;
	private final Type[] parameters;
	
	public ExternalMethodMetrics(String className, String methodName, Type[] parameters) {

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
	public IMethodMetrics[] getMethodCalls() {
		return new IMethodMetrics[0];
	}
	
	@Override
	public void replaceMethodCall(int index, IMethodMetrics methodCall) {
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
}
