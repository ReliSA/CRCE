package cz.zcu.kiv.crce.resolver.optimizer;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.FunctionProvider;

/**
 * Optimizer interface for picking the most suitable set of resources which fullfil the given
 * requirements.
 *
 * Date: 27.5.16
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public interface ResultOptimizer extends FunctionProvider {

    /**
     * Optimize fullSet based on the given cost function and optimization mode.
     *
     * @param requirements all the requirements that must be fulfilled
     * @param fullSet set of resources which fulfill the requirements
     * @param cost resource cost function
     * @param mode optimization mode - min or max
     * @return in general subset with lowest/highest price/score still fulfilling the requirements
     */
    @Nonnull
    List<Resource> optimizeResult(Set<Requirement> requirements, List<Resource> fullSet, CostFunction cost, OptimizationMode mode);

}
