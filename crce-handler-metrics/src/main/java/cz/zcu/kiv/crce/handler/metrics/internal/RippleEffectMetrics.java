package cz.zcu.kiv.crce.handler.metrics.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RippleEffectMetrics {

	private List<ClassMetrics> classMetrics;
	
	public RippleEffectMetrics(List<ClassMetrics> classMetrics) {
		
		this.classMetrics = classMetrics;
	}
	
	public void init() {
		
        for (ClassMetrics classMetric : classMetrics) {        	
        	for (IMethodMetrics methodMetrics : classMetric.getMethods()) {
        		
        		IMethodMetrics[] methodCalls = methodMetrics.getMethodCalls();        		
        		for (int i = 0; i < methodCalls.length; i++) {
        			
        			for (ClassMetrics intermoduleClass : classMetrics) {        				
        				if (intermoduleClass.getFullClassName().equals(methodCalls[i].getClassName())) {
        					
        					for (IMethodMetrics intermoduleClassMethod : classMetric.getMethods()) {
        						if (intermoduleClassMethod.equals(methodCalls[i])) {
        							methodMetrics.replaceMethodCall(i, intermoduleClassMethod);
        							
        							break;
        						}
        					}
        					
        					break;
        				}
        			}
        		}
        	}
        }
	}
	
	public RippleEffect getRippleEffectForPackage(String packageName) {
		
		Set<IMethodMetrics> collectedMethods = new HashSet<IMethodMetrics>();
		List<IMethodMetrics> methodsToVisit = new ArrayList<IMethodMetrics>();
		
		// first collect public method of public classes from package
        for (ClassMetrics classMetric : classMetrics) {
        	if (classMetric.isPublic() && classMetric.getPackageName().equals(packageName)) {
        		for (IMethodMetrics method : classMetric.getMethods()) {
        			if (method.isPublic()) {
        				collectedMethods.add(method);
        				methodsToVisit.add(method);
        			}
        		}	        		
        	}
        }
        
        // expand all internal methods (internal calls)
        while (!methodsToVisit.isEmpty()) {
        	
        	IMethodMetrics investigatedMethod = methodsToVisit.remove(0);        	
        	for (IMethodMetrics methodCall : investigatedMethod.getMethodCalls()) {
        		
        		// if method was not jet collected
        		if (!collectedMethods.contains(methodCall)) {
        			// collect method
        			collectedMethods.add(methodCall);
        			// and inter-jar methods collect for expanding 
        			if (methodCall.isInternal()) {
        				// not discovered (not in collected) and internal (expandable) 
        				methodsToVisit.add(methodCall);
        			}
        		}	        			        		
        	}
        }
        
        int internalNonAbstract = 0;
        int internalAbstract = 0;
        int external = 0;
        
        // count internal and external methods
        for (IMethodMetrics methodCall : collectedMethods) {
        	if (methodCall.isInternal()) {
        		if (methodCall.isAbstract()) {
        			internalAbstract++;
        		} 
        		else {
        			internalNonAbstract++;
        		}
        	}
        	else {
        		external++;
        	}
        }
        
        return new RippleEffect(internalNonAbstract, internalAbstract, external);
	}
}
