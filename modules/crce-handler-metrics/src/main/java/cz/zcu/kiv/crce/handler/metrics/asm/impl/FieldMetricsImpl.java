package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import javax.annotation.Nonnull;

/**
 * FieldMetrics implementation class.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class FieldMetricsImpl extends AbstractFieldMetrics
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
	public boolean isInternal() {
		return true;
	}

}

