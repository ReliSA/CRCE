package cz.zcu.kiv.crce.optimizer.internal.cost;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Property;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.FunctionProvider;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunction;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;

/**
 * Cost function which can be used to minimize/maximize total number of returned
 * resources. In other words, all resources have the same price/score when using this
 * function.
 *
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
public class EqualCostFunction implements CostFunction {

    protected static final String ID_VALUE = "cf-equal-cost";
    protected static final String DESCRIPTION_VALUE = "Optimizes result set by the total amount of components (usually" +
            " you want to return as few components as possible in this scenario.";


    @Override
    public String getId() {
        return ID_VALUE;
    }

    @Override
    public double getCost(Resource resource) {
        return 1;
    }


    /**
     * Factory for {@link EqualCostFunction} cost function.
     */
    @Component(provides = CostFunctionFactory.class,
            properties = {
                    @Property(name = FunctionProvider.ID, value = EqualCostFunction.ID_VALUE),
                    @Property(name = FunctionProvider.DESCRIPTION, value = EqualCostFunction.DESCRIPTION_VALUE)
            })
    public static class ComponentCountFactory extends CostFunctionFactory {

        public ComponentCountFactory() {
            super(ID_VALUE, DESCRIPTION_VALUE);
        }

        @Override
        public CostFunction createInstance(Requirement requirement) {
            return new EqualCostFunction();
        }

    }

}

