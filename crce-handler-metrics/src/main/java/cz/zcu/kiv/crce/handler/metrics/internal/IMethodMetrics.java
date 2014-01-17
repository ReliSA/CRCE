package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Type;

public interface IMethodMetrics {

	String getClassName();
	
	String getName();
	
	Type[] getParameters();
	
	IMethodMetrics[] getMethodCalls();
	
	void replaceMethodCall(int index, IMethodMetrics methodCall);
	
	boolean isPublic();
	
	boolean isInternal();
	
	boolean isAbstract();
	
	boolean equals(IMethodMetrics methodMetrics);
}