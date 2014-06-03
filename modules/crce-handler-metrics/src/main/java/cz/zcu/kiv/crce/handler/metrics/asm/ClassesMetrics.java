package cz.zcu.kiv.crce.handler.metrics.asm;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Interface wrapping <code>ClassMetrics</code> list. 
 * If you need some preprocessing method like <code>connectUsedOutClassFields</code> 
 * or <code>connectCalledMethods</code>, this method have to be called before first call
 * <code>getClassMetricsList</code>.
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface ClassesMetrics {

	/**
	 * Replace all in-jar usedOutClassFields placeholders in methods 
	 * with coresponding fields in ClassMetrics.
	 */
	void connectUsedOutClassFields();
	
	/**
	 * Replace all in-jar calledMethods placeholders in methods 
	 * with coresponding methods in ClassMetrics.
	 */
	void connectCalledMethods();
	
	/**
	 * Get list of collected ClassMetrics. 
	 * 
	 * After calling this method, other methods are disabled.
	 * 
	 * @return List of ClassMetrics.
	 */
	@Nonnull
	List<ClassMetrics> getClassMetricsList();
}
