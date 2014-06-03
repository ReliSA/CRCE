package cz.zcu.kiv.crce.handler.metrics.impl;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassesMetrics;

/**
 * Implementation of maximum of McCabe's Cyclomatic Complexity for all classes, 
 * where Cyclomatic Complexity of class is computed as average Cyclomatic 
 * Complexity of all non-abstract methods. 
 * 'A Complexity Measure' - McCabe, T.J.  (1976)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ieeexplore.ieee.org/xpl/login.jsp?tp=&arnumber=1702388&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs_all.jsp%3Farnumber%3D1702388">A Complexity Measure</a>
 */
public class MaximumCyclomaticComplexity implements ComponentMetrics {

	private ClassesMetrics classesMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classesMetrics Wrapper of parsed ClassMetrics list.
	 */
	public MaximumCyclomaticComplexity(ClassesMetrics classesMetrics) {
		
		this.classesMetrics = classesMetrics;
	}
	
	@Override
	public void init() {
		// nothing to do here
	}

	@Override
	@Nonnull
	public String getName() {
		return "design-complexity-max";
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

        double maximumCyclomaticComplexity = 0;
        for (ClassMetrics classMetrics : classesMetrics.getClassMetricsList()) {
        	
        	double averageCyclomaticComplexity = classMetrics.getAverageCyclomaticComplexity();
        	if (!Double.isNaN(averageCyclomaticComplexity) && maximumCyclomaticComplexity < averageCyclomaticComplexity) {
        			
        		maximumCyclomaticComplexity = averageCyclomaticComplexity;
        	}
        }

        // we didn't find max value, defined Cyclomatic Complexity have to be more than 0
        if (maximumCyclomaticComplexity == 0) {
        	
        	maximumCyclomaticComplexity = Double.NaN;
        }
        
		return Double.valueOf(maximumCyclomaticComplexity);
	}
}
