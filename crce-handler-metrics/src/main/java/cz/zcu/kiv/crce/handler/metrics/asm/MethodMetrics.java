package cz.zcu.kiv.crce.handler.metrics.asm;

import javax.annotation.Nonnull;

import org.objectweb.asm.Type;

/**
 * Interface collected metrics information of single classes method.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface MethodMetrics {

	/**
	 * Name of class (full) declaring this method.
	 * 
	 * @return Name of class.
	 */
	@Nonnull
	String getClassName();
	
	/**
	 * Name of this method.
	 * 
	 * @return Name of method.
	 */
	@Nonnull
	String getName();
	
	/**
	 * Array of parameters types (short version without generic).
	 * 
	 * @return Array of parameters types.
	 */
	@Nonnull
	Type[] getParameters();
	
	/**
	 * Array of methods (set - each method once) called by this method.
	 * 
	 * @return Array of called method.
	 */
	@Nonnull
	MethodMetrics[] getMethodCalls();
	
	/**
	 * Replace one method call by another. This should be used only for replacing 
	 * placeholders (build internal references)
	 * 
	 * @param index Index of method to be replaced.
	 * @param methodCall New method.
	 */
	void replaceMethodCall(int index, @Nonnull MethodMetrics methodCall);
	
	/**
	 * Indicated, if method is public.
	 * 
	 * @return True, if method is public.
	 */
	boolean isPublic();
	
	/**
	 * Indicates, if method is in investigated jar file.
	 * 
	 * @return True, if method is in jar file.
	 */
	boolean isInternal();
	
	/**
	 * Indicates, if method is abstract or belong to interface.
	 * 
	 * @return True, if method is abstract or method of interface.
	 */
	boolean isAbstract();
	
	/**
	 * Indicate, if two method are equal. Method is equal, if belongs to same class, 
	 * has same name and same parameters signature (ignore generic).
	 * 
	 * @param methodMetrics MethodMetrics to compare to.
	 * @return True, if method is equal.
	 */
	boolean equals(@Nonnull MethodMetrics methodMetrics);
}