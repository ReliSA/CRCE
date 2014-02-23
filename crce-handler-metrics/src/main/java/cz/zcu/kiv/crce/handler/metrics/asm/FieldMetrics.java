package cz.zcu.kiv.crce.handler.metrics.asm;

import javax.annotation.Nonnull;

/**
 * Interface collected metrics information of single classes field.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface FieldMetrics {

	/**
	 * Name of class (full) declaring this field.
	 * 
	 * @return Name of class.
	 */
	@Nonnull
	String getClassName();
	
	/**
	 * Name of this field.
	 * 
	 * @return Name of field.
	 */
	@Nonnull
	String getName();
	
	/**
	 * Indicates, if field is in investigated jar file.
	 * 
	 * @return True, if field is in jar file.
	 */	
	boolean isInternal();
	
	/**
	 * Indicate, if two fields are equal. Field is equal, if belongs to same class, 
	 * has same name.
	 * 
	 * @param fieldMetrics FieldMetrics to compare to.
	 * @return True, if field is equal.
	 */
	boolean equals(FieldMetrics fieldMetrics);
}
