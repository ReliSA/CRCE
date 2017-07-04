package cz.zcu.kiv.crce.plugin;

import java.util.List;

/**
 * Listing interface for {@link FunctionProvider} implementations.
 *
 * Different implementations should be provided based on "domain"
 * as multiple implementations of {@link FunctionProvider} may
 * be unrelated.
 *
 * Date: 4.1.17
 *
 * @author Jakub Danek
 */
public interface FunctionProviderRepository<T extends FunctionProvider> {


    /**
     *
     * @return all available function providers of the given type
     */
    List<T> list();

    /**
     *
     * @param id desired function provider id
     * @return function provider with the given id and type
     */
    T findOne(String id);

}
