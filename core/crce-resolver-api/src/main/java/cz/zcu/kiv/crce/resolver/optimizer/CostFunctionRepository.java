package cz.zcu.kiv.crce.resolver.optimizer;

import cz.zcu.kiv.crce.plugin.FunctionProviderRepository;

/**
 * Repository interface for listing cost functions available in the system.
 *
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
public interface CostFunctionRepository extends FunctionProviderRepository<CostFunctionFactory> {
}
