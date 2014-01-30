package cz.zcu.kiv.crce.handler.metrics.impl;

import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;

/**
 * Implementation of average McCabe's Cyclomatic Complexity for all classes, 
 * where Cyclomatic Complexity of class is computed as average Cyclomatic 
 * Complexity of all non-abstract methods. 
 * 'A Complexity Measure' - McCabe, T.J.  (1976)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ieeexplore.ieee.org/xpl/login.jsp?tp=&arnumber=1702388&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D1702388">A Complexity Measure</a>
 */
public class AverageCyclomaticComplexity implements ComponentMetrics {

	private List<ClassMetrics> classesMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classMetrics List of parsed ClassMetrics.
	 */
	public AverageCyclomaticComplexity(List<ClassMetrics> classesMetrics) {
		
		this.classesMetrics = classesMetrics;
	}
	
	@Override
	public void init() {
		// nothing to do here
	}

	@Override
	@Nonnull
	public String getName() {
		return "design-complexity-average";
	}

	@Override
	@Nonnull
	@SuppressWarnings("rawtypes")
	public Class getType() {
		return Double.class;
	}

	@Override
	@Nonnull
	public Object computeValue() {

        double cyclomaticComplexitySum = 0;
        int nonInterfaceClassCount = 0;
        for (ClassMetrics classMetrics : classesMetrics) {
        	if (!classMetrics.isInterface()) {
        		cyclomaticComplexitySum += classMetrics.getAverageCyclomaticComplexity();
        		nonInterfaceClassCount++;
        	}
        }
        
        double averageCyclomaticComplexity = 0;        
        if (nonInterfaceClassCount != 0) { 
        	averageCyclomaticComplexity = cyclomaticComplexitySum / nonInterfaceClassCount;
        }
		return Double.valueOf(averageCyclomaticComplexity);
	}

}
