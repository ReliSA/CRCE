package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;

import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * Abstract MethodMetrics class.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public abstract class AbstractMethodMetrics implements MethodMetrics {
	
	@Override
	public boolean equals(@Nonnull MethodMetrics methodMetrics) {
		
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
	
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof MethodMetrics) {
			return equals((MethodMetrics)other);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getClassName().hashCode() * 3 + this.getName().hashCode() * 11 + this.getParameters().length;
	}
}
