package cz.zcu.kiv.crce.handler.metrics.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * Implementation of computing WTCoup metrics published in 
 * 'Measuring Software Component Reusability by Coupling and Cohesion Metrics' 
 * - Gui Gui, Paul D. Scott (2009)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ojs.academypublisher.com/index.php/jcp/article/viewFile/0409797805/579">Measuring Software Component Reusability by Coupling and Cohesion Metrics</a>
 */
public class WTCoupMetrics implements ComponentMetrics {

	private static final Logger logger = LoggerFactory.getLogger(WTCoupMetrics.class);
	
	private List<ClassMetrics> classMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classMetrics List of parsed ClassMetrics.
	 */
	public WTCoupMetrics(List<ClassMetrics> classMetrics) {
		this.classMetrics = classMetrics;
	}
	
	@Override
	public void init() {
		// nothing to do here		
	}

	@Override
	@Nonnull
	public String getName() {
		return "wt-coupling";
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

		List<ClassMetrics> classes = new ArrayList<ClassMetrics>();        
    	for (ClassMetrics classMetric : classMetrics) {
			if (!classMetric.isInterface()) {
				classes.add(classMetric);
			}
    	}
            		
    	double wTCoup = 0;
    	
    	int classCount = classes.size();
    	if (classCount > 1) {
    		
    		double[][] coup = new double[classCount][classCount];
    		            		
    		Set<MethodMetrics> calledMethods = new HashSet<MethodMetrics>();
    		Set<FieldMetrics> usedExternalFields = new HashSet<FieldMetrics>();
    		
    		// CoupD
    		for (int i = 0; i < classCount; i++) {
    			
    			calledMethods.clear();
    			usedExternalFields.clear();
    			
    			ClassMetrics classI = classes.get(i);
    			
    			for (MethodMetrics method : classI.getMethods()) {
    				
    				for (MethodMetrics calledMethod : method.getMethodCalls()) {
    					calledMethods.add(calledMethod);
    				}

    				for (FieldMetrics usedField : method.getUsedOutClassFields()) {
    					usedExternalFields.add(usedField);
    				}
    			}
    			
    			int mVICount = calledMethods.size() + usedExternalFields.size();
    			int mICount = classI.getMethods().size();
    			int vICount = classI.getSimpleTypeFieldCount() + classI.getComplexTypeFieldCount();
    			
    			int denominator = mVICount +  mICount + vICount;
    		
    			for (int j = 0; j < classCount; j++) {
    							
    				double coupD = 0;
    				if (denominator != 0 && i != j) {
    					
    					int mVIJ = 0;
    					
    					ClassMetrics classJ = classes.get(j);
    					String classJName = classJ.getFullClassName();
    					
        				for (MethodMetrics calledMethod : calledMethods) {
        					if (calledMethod.getClassName().equals(classJName)) {
        						mVIJ += 1;
        					}
        				}
        				
        				for (FieldMetrics usedField : usedExternalFields) {
        					if (usedField.getClassName().equals(classJName)) {
        						mVIJ += 1;
        					}
        				}
    					
    					coupD = (double)mVIJ / denominator;
    				}
    				
    				coup[i][j] = coupD;
    			}
    		}
    		
    		// CoupT - modified Floyd–Warshall algorithm
    		for (int k = 0; k < classCount; k++) {
        		for (int i = 0; i < classCount; i++) {
        			for (int j = 0; j < classCount; j++) {
        				
        				if (i == k || j == k || i == j) {
        					continue;
        				}
        				
        				coup[i][j] = Math.max(coup[i][j], coup[i][k] * coup[k][j]);
        			}
        		}
    		}
    		
    		double coupSum = 0;
    		
    		for (int j = 1; j < classCount; j++) {
    			for (int i = 0; i < j; i++) {
    			
    				coupSum += coup[i][j] + coup[j][i];
    			}
    		}
    		
    		wTCoup = coupSum / (classCount * classCount - classCount);
		}
    	       
    	logger.debug("WTCoup {} ", wTCoup);
    	
		return new Double(wTCoup);
	}
}
