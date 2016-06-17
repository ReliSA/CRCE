package cz.zcu.kiv.crce.resolver.optimizer;

import java.util.List;

/**
 * Repository interface for listing cost functions available in the system.
 *
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public interface CostFunctionRepository {

    /**
     *
     * @return all available cost function factories
     */
    List<CostFunctionFactory> list();

    /**
     *
     * @param id desired cost function id
     * @return cost function factory for the function with the given id
     */
    CostFunctionFactory findOne(String id);
}
