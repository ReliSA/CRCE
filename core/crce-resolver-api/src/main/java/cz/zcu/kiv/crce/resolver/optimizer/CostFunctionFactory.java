package cz.zcu.kiv.crce.resolver.optimizer;

import cz.zcu.kiv.crce.metadata.Requirement;

/**
 * <p>
 * Base class for {@link CostFunction} factories. Implementations should be able to parse configuration attributes
 * required for creating a {@link CostFunction} instance set for the current request.
 *</p>
 *
 * <p>
 *     Factories are meant to exist as singletons in the system, so that they can serve as list of available
 *     cost functions. For that they provide {@link #getId()}  field, which is equal
 *     to the {@link CostFunction#getId()} value of the cost function instance the factory creates.
 * </p>
 *
 * <p>
 *     Additionally the factory provides and {@link #getDescription()} method which contains documentation
 *     of the cost function configuration interface.
 * </p>
 *
 *
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public abstract class CostFunctionFactory {
    //property name constants, used e.g. for OSGi service specificaiton
    public static final String ID = "cfId";
    public static final String DESCRIPTION = "cfDescription";

    private final String id;
    private final String description;

    public CostFunctionFactory(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     *
     * @return ID of the cost function
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return documentation of the configuration interface
     */
    public String getDescription() {
        return description;
    }

    /**
     * The actual factory method. This one configures the cost function instance based
     * on the #requirement attributes.
     *
     * @param requirement requirement to be used as cost function specification
     * @return the cost function instance
     */
    public abstract CostFunction createInstance(Requirement requirement);

}
