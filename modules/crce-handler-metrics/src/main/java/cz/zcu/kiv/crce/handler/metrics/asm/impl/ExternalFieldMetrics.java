package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import javax.annotation.Nonnull;

/**
 * External FieldMetrics class. Representing fields out of jar or can be used as placeholder.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class ExternalFieldMetrics extends AbstractFieldMetrics {

	private String className;
	private String name;
	
	/**
	 * New instance.
	 * 
	 * @param className Name of class (full) where field is defined.
	 * @param name Name of field.
	 */
	public ExternalFieldMetrics(@Nonnull String className, @Nonnull String name) {
		
		this.className = className;
		this.name = name;
	}
	
	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isInternal() {
		return false;
	}
}
