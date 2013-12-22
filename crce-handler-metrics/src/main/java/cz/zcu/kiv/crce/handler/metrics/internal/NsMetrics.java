package cz.zcu.kiv.crce.handler.metrics.internal;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Namespace and attributes specification for <code>Capability</code> metadata api.
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface NsMetrics {
	
    String NAMESPACE__METRICS = "crce.metrics";

    AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);

    AttributeType<Long> ATTRIBUTE__LONG__VALUE = new SimpleAttributeType<>("value", Long.class);
    
    AttributeType<Double> ATTRIBUTE__DOUBLE__VALUE = new SimpleAttributeType<>("value", Double.class);
            
}
