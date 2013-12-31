package cz.zcu.kiv.crce.handler.metrics.internal;

import org.objectweb.asm.Type;

public abstract class AbstractMethodMetrics implements IMethodMetrics {
	
	public boolean equals(IMethodMetrics methodMetrics) {
		
		if (!this.getClassName().equals(methodMetrics.getClassName())) {
			return false;
		}

		if (!this.getName().equals(methodMetrics.getName())) {
			return false;
		}
		
		Type[] thisParameters = this.getParameters();
		Type[] methodMetricsParameters = methodMetrics.getParameters();
		
		if (thisParameters.length != methodMetricsParameters.length) {
			return false;
		}
		
		for (int i = 0; i < thisParameters.length; i++) {
			if (!thisParameters[i].equals(methodMetricsParameters[i])) {
				return false;
			}
		}

		return true;
	}
}
