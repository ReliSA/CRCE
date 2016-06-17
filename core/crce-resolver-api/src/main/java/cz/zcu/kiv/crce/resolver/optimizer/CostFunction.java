package cz.zcu.kiv.crce.resolver.optimizer;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * <p>
 * Implementation of a function F: Resource -> Double, which represents the
 * overall price (if minimizing) or score (if maximizing) the given resource adds
 * to the solution - returned set of resources.
 * </p>
 *
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public interface CostFunction {

    /**
     *
     * @return unique ID of the function in the system
     */
    String getId();

    /**
     * The actual implementation of the function
     *
     * @param resource resource for which price/score is computed
     * @return the computed score/price
     */
    double getCost(Resource resource);

}
