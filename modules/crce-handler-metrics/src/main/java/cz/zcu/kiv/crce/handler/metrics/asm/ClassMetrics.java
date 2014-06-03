package cz.zcu.kiv.crce.handler.metrics.asm;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Interface collected metrics information of single class.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface ClassMetrics {

	/**
	 * Name of class (full). Dot notation (packageName.ClassName). 
	 * 
	 * @return Name of class.
	 */
	@Nonnull
	String getFullClassName();

	/**
	 * Name of class (short).
	 * 
	 * @return Name of class.
	 */
	@Nonnull
	String getClassName();

	/**
	 * Name of classes package.
	 * 
	 * @return Name of classes package.
	 */
	@Nonnull
	String getPackageName();

	/**
	 * Indicator, if class (or interface) is public. 
	 * 
	 * @return True, if class(interface) is public.
	 */
	boolean isPublic();

	/**
	 * Indicator, if this is interface instead of class.
	 * 
	 * @return True, if interface.
	 */
	boolean isInterface();

	/**
	 * Count of all public methods.
	 * 
	 * @return Count of public methods.
	 */
	double getMethodCount();

	/**
	 * Count of fields of simple type. 
	 * 
	 * @return Count of fields of simple type. 
	 */
	int getSimpleTypeFieldCount();

	/**
	 * Count of fields of complex type. 
	 * 
	 * @return Count of fields of complex type. 
	 */
	int getComplexTypeFieldCount();

	/**
	 * Count of methods parameters of simple type.
	 * 
	 * @return Count of methods parameters of simple type.
	 */
	int getSimpleParametersCount();

	/**
	 * Count of methods parameters of complex type.
	 * 
	 * @return Count of methods parameters of complex type.
	 */
	int getComplexParametersCount();

	/**
	 * List of classes FieldMetrics.
	 * 
	 * @return List of classes FieldMetrics.
	 */
	@Nonnull
	List<FieldMetrics> getFields();
	
	/**
	 * List of classes MethodMetrics.
	 * 
	 * @return List of classes MethodMetrics.
	 */
	@Nonnull
	List<MethodMetrics> getMethods();
	
	/**
	 * Average value of McCabe's Cyclomatic Complexity of method.
	 * May be NaN, if class has no implemented method.
	 * 
	 * @return Cyclomatic complexity of class.
	 */
	double getAverageCyclomaticComplexity();
}