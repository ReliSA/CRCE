package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;

/**
 * Abstract FieldMetrics class.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public abstract class AbstractFieldMetrics implements FieldMetrics {

	@Override
	public boolean equals(FieldMetrics fieldMetrics) {
		return fieldMetrics.getClassName().equals(this.getClassName()) && fieldMetrics.getName().equals(this.getName());
	}

	@Override
	public boolean equals(Object other) {
		
		if (other instanceof FieldMetrics) {
			return equals((FieldMetrics)other);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return getClassName().hashCode() * 3 + getName().hashCode();
	}
}
