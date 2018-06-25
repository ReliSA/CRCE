package cz.zcu.kiv.crce.resolver.internal.optimizer;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;

import cz.zcu.kiv.crce.plugin.AbstractOsgiFunctionProviderRepository;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionRepository;

/**
 * Implementation of {@link CostFunctionRepository} which displays {@link CostFunctionFactory} instances
 * registered as OSGi services.
 *
 * Date: 31.5.16
 *
 * @author Jakub Danek
 */
@Component(provides = CostFunctionRepository.class)
@ParametersAreNonnullByDefault
public class OsgiCostFunctionRepository extends AbstractOsgiFunctionProviderRepository<CostFunctionFactory> implements CostFunctionRepository {

    public OsgiCostFunctionRepository() {
        super(CostFunctionFactory.class);
    }

}
