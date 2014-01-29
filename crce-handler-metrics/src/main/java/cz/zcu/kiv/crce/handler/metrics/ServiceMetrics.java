package cz.zcu.kiv.crce.handler.metrics;

import javax.annotation.Nonnull;

/**
 * Metrics interface for computing metrics of specific service.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface ServiceMetrics extends Metrics {

	/**
	 * Compute metrics value for specific service.
	 * 
	 * @param serviceName Name of service.
	 * @return Service metrics value.
	 */
	@Nonnull
	Object computeValueForService(@Nonnull String serviceName);
}
