package cz.zcu.kiv.crce.plugin;

/**
 * Marker interface for functionality extensions selectable by user.
 * This interface shouldnt be implemented directly, but extended by
 * a new interface, which then defines the "group" of related
 * functions within a particular domain.
 *
 * In reality system may provide multiple implementation of a function
 * e.g. different ways to compute a component's score and user can
 * select which one to use.
 *
 * This interface provides common description properties, so that the
 * functions may be listed and selected.
 *
 * Date: 4.1.17
 *
 * @author Jakub Danek
 */
public interface FunctionProvider {

    //property name constants, used e.g. for OSGi service specificaiton
    String ID = "fpId";
    String DESCRIPTION = "fpDescription";

    /**
     *
     * @return ID of the function provider
     */
    String getId();

    /**
     *
     * @return documentation of the configuration interface for the function provider
     */
    String getDescription();
}
