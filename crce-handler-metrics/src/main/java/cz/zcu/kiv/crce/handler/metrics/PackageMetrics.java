package cz.zcu.kiv.crce.handler.metrics;

import javax.annotation.Nonnull;

/**
 * Metrics interface for computing metrics of specific package.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface PackageMetrics extends Metrics {

	/**
	 * Compute metrics value for specific package.
	 * 
	 * @param packageName Name of package.
	 * @return Package metrics value.
	 */
	@Nonnull
	Object computeValueForPackage(@Nonnull String packageName);
}
