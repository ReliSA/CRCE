package cz.zcu.kiv.crce.handler.metrics.impl;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.PackageMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassesMetrics;

/**
 * Implementation of computing class complexity metrics base on CPC metrics introduces in 
 * 'Component Metrics to Measure Component Quality' - Eun Sook Cho, Min Sun Kim, Soo Dong Kim (2001)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?tp=&arnumber=991509&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D991509">Component metrics to measure component quality</a>
 */
public class CpcMetrics implements PackageMetrics {

	// weights for weighted parametrs - static weighing
	private static final double CLASS_WEIGHT = 1.0;
	private static final double METHOD_WEIGHT = 1.0;
	private static final double COMPLEX_FIELD_WEIGHT = 1.0;
	private static final double COMPLEX_PARAMETER_WEIGHT = 1.0;
	
	private ClassesMetrics classesMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classesMetrics Wrapper of parsed ClassMetrics list.
	 */
	public CpcMetrics(ClassesMetrics classesMetrics) {
		
		this.classesMetrics = classesMetrics;
	}
	
	@Override
	public void init() {
		// nothing to do here
	}
	
	@Override
	@Nonnull
	public String getName() {
		return "api-complexity";
	}
	
	@Override
	@Nonnull
	@SuppressWarnings("rawtypes")
	public Class getType() {
		return Double.class;
	}
	
	@Override
	@Nonnull
	public Object computeValueForPackage(String packageName) {
		
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
		
		for (ClassMetrics classMetric : classesMetrics.getClassMetricsList()) {

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
		
		double cpc = cmpC + sumClassComplexity + sumMethodComplexity;
		
		return new Double(cpc);
	}
}
