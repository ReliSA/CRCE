package cz.zcu.kiv.crce.handler.metrics.asm.impl;

import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassesMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * Implementation of <code>ClassesMetrics</code>. ClassMetrics list wrapper.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class ClassesMetricsImpl implements ClassesMetrics {

	private List<ClassMetrics> classMetricsList;
	
	private boolean classesMetricsListInUse;

	private boolean connectedUsedOutClassFields;
	private boolean connectedCalledMethods;	
	
	/**
	 * New instance.
	 * 
	 * @param classMetricsList ClassMetrics list.
	 */
	public ClassesMetricsImpl(List<ClassMetrics> classMetricsList) {
		
		this.classMetricsList = classMetricsList;
		
		classesMetricsListInUse = false;
		
		connectedUsedOutClassFields = false;
		connectedCalledMethods = false;
	}
	
	@Override
	public void connectUsedOutClassFields() {

		if (classesMetricsListInUse || connectedUsedOutClassFields) {
			return;
		}
		
        for (ClassMetrics classMetric : classMetricsList) {        	
        	for (MethodMetrics methodMetrics : classMetric.getMethods()) {
        		
        		FieldMetrics[] usedOutClassFields = methodMetrics.getUsedOutClassFields();        		
        		for (int i = 0; i < usedOutClassFields.length; i++) {
        			
        			for (ClassMetrics intermoduleClass : classMetricsList) {        				
        				if (intermoduleClass.getFullClassName().equals(usedOutClassFields[i].getClassName())) {
        					
        					for (FieldMetrics intermoduleClassField : classMetric.getFields()) {
        						if (intermoduleClassField.equals(usedOutClassFields[i])) {
        							methodMetrics.replaceUsedOutClassField(i, intermoduleClassField);
        							
        							break;
        						}
        					}
        					
        					break;
        				}
        			}
        		}
        	}
        }
		
        connectedUsedOutClassFields = true;		
	}

	@Override
	public void connectCalledMethods() {

		if (classesMetricsListInUse || connectedCalledMethods) {
			return;
		}
		
        for (ClassMetrics classMetric : classMetricsList) {        	
        	for (MethodMetrics methodMetrics : classMetric.getMethods()) {
        		
        		MethodMetrics[] methodCalls = methodMetrics.getMethodCalls();        		
        		for (int i = 0; i < methodCalls.length; i++) {
        			
        			for (ClassMetrics intermoduleClass : classMetricsList) {        				
        				if (intermoduleClass.getFullClassName().equals(methodCalls[i].getClassName())) {
        					
        					for (MethodMetrics intermoduleClassMethod : classMetric.getMethods()) {
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
        
        connectedCalledMethods = true;		
	}

	@Override
	@Nonnull
	public List<ClassMetrics> getClassMetricsList() {

		classesMetricsListInUse = true;
		
		return classMetricsList;
	}

}
