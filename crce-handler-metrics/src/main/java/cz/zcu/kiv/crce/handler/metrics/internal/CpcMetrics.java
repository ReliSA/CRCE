package cz.zcu.kiv.crce.handler.metrics.internal;

import java.util.List;

public class CpcMetrics {

	private static final double CLASS_WEIGHT = 1.0;
	private static final double METHOD_WEIGHT = 1.0;
	private static final double COMPLEX_FIELD_WEIGHT = 1.0;
	private static final double COMPLEX_PARAMETER_WEIGHT = 1.0;
	
	private List<ClassMetrics> classMetrics;
	
	public CpcMetrics(List<ClassMetrics> classMetrics) {
		
		this.classMetrics = classMetrics;
	}
	
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
