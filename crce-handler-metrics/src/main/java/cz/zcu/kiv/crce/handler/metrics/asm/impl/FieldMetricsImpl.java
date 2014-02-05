package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;

/**
 * FieldMetrics implementation class.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class FieldMetricsImpl implements FieldMetrics
{
	private String className;
	private String name;
	
	/**
	 * New instance.
	 * 
	 * @param className Name of class (full) where field is defined.
	 * @param name Name of field.
	 */
	public FieldMetricsImpl(String className, String name) {
		
		this.className = className;
		this.name = name;
	}
	
	@Override
	@Nonnull
	public String getClassName()
	{
		return className;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(FieldMetrics fieldMetrics)
	{
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

