package cz.zcu.kiv.crce.handler.metrics;

import javax.annotation.Nonnull;

/**
 * Metrics interface for computing metrics of entire component (jar file).
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface ComponentMetrics extends Metrics {

	/**
	 * Compute metrics value.
	 * 
	 * @return Component metrics value.
	 */
	@Nonnull
	Object computeValue();
}
