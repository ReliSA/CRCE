package cz.zcu.kiv.crce.handler.metrics.internal;

import java.util.List;

/**
 * Implementation of computing class complexity metrics base on CPC metrics introduces in 
 * 'Component Metrics to Measure Component Quality' - Eun Sook Cho, Min Sun Kim, Soo Dong Kim (2001)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=991509&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D991509">Component metrics to measure component quality</a>
 */
public class CpcMetrics {

	// weights for weighted parametrs - static weighing
	private static final double CLASS_WEIGHT = 1.0;
	private static final double METHOD_WEIGHT = 1.0;
	private static final double COMPLEX_FIELD_WEIGHT = 1.0;
	private static final double COMPLEX_PARAMETER_WEIGHT = 1.0;
	
	private List<ClassMetrics> classMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classMetrics List of parsed ClassMetrics.
	 */
	public CpcMetrics(List<ClassMetrics> classMetrics) {
		
		this.classMetrics = classMetrics;
	}
	
	/**
	 * Compute CPC metrics for specific package.
	 * 
	 * @param packageName Specific package name.
	 * @return Computed CPC (complexity) value.
	 */
	public double computeCpcForPackage(String packageName) {
		
		double cmpC = 0; 
		double sumClassComplexity = 0; 
		double sumMethodComplexity = 0;
		
		int classCount = 0;
		int interfaceCount = 0;
		int methodCount = 0;
		
		int simpleTypeFieldCount = 0;
		int complexTypeFieldCount = 0;
		
		int simpleParametersCount = 0;
		int complexParametersCount = 0;
		
		for (ClassMetrics classMetric : classMetrics) {
			// we are counting only public classes (or interfaces) from specific package
			if (classMetric.isPublic() && classMetric.getPackageName().compareTo(packageName) == 0) {
				
				simpleTypeFieldCount += classMetric.getSimpleTypeFieldCount();
				complexTypeFieldCount += classMetric.getComplexTypeFieldCount();
				
				simpleParametersCount += classMetric.getSimpleParametersCount();
				simpleParametersCount += classMetric.getComplexParametersCount();
				
				methodCount += classMetric.getMethodCount();
				
				if (classMetric.isInterface()) {
					interfaceCount++;
				}
				else {
					classCount++;
				}
			}
		}
		
		cmpC = classCount * CLASS_WEIGHT + interfaceCount + methodCount * METHOD_WEIGHT;
		sumClassComplexity = simpleTypeFieldCount + COMPLEX_FIELD_WEIGHT * complexTypeFieldCount;
		sumMethodComplexity = simpleParametersCount + complexParametersCount * COMPLEX_PARAMETER_WEIGHT;
		
		return cmpC + sumClassComplexity + sumMethodComplexity;
	}
}
