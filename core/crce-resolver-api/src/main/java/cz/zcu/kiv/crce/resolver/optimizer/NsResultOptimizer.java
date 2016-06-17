package cz.zcu.kiv.crce.resolver.optimizer;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public class NsResultOptimizer {

    public static final String NAMESPACE__RESULT_OPTIMIZER = "result.optimize-by";

    public static final AttributeType<String> ATTRIBUTE__FUNCTION_ID = new SimpleAttributeType<>("function-ID", String.class);

    /**
     * Optimization mode directive name
     */
    public static final String DIRECTIVE__MODE = "mode";

}
