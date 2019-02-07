package cz.zcu.kiv.crce.crce_component_versioning.api;

import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;

import java.util.List;

public interface VersioningServiceApi {
    /**
     * Interface for accessing all records in the list of composite components.
     * <p/>
     * Date: 05.02.19
     *
     * @author Roman Pesek
     */
    List<ComponentBean> getCompositeComponentAll();

    /**
     * Interface fo saving the composite components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean setCompositeComponent(String name, String version, List<String> listId);
}
