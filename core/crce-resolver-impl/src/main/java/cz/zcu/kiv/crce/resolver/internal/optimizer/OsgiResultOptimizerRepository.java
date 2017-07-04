package cz.zcu.kiv.crce.resolver.internal.optimizer;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;

import cz.zcu.kiv.crce.plugin.AbstractOsgiFunctionProviderRepository;
import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizer;
import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizerRepository;

/**
 * Implementation of {@link ResultOptimizerRepository} which displays {@link ResultOptimizer} instances
 * registered as OSGi services.
 *
 * Date: 31.5.16
 *
 * @author Jakub Danek
 */
@Component(provides = ResultOptimizerRepository.class)
@ParametersAreNonnullByDefault
public class OsgiResultOptimizerRepository extends AbstractOsgiFunctionProviderRepository<ResultOptimizer> implements ResultOptimizerRepository {

    public OsgiResultOptimizerRepository() {
        super(ResultOptimizer.class);
    }

}
