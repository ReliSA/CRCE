package cz.zcu.kiv.crce.handler.metrics;

import javax.annotation.Nonnull;

/**
 * Base interface for all metrics computing.
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface Metrics {

	/**
	 * Initialization of computing class. Should be called before any metrics value computation method. 
	 */
	void init();
	
	/**
	 * Get metrics name for metadata.
	 * 
	 * @return Metrics name.
	 */
	@Nonnull
	String getName();
	
	/**
	 * Get metrics return type. Type of computing value.
	 * 
	 * @return Computing value type.
	 */
	@SuppressWarnings("rawtypes")
	@Nonnull
	Class getType();
}
