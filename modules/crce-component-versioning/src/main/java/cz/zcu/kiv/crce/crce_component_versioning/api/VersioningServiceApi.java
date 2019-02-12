package cz.zcu.kiv.crce.crce_component_versioning.api;

import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;
import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentDetailBean;

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
     * Interface for component access by id. If it is not found, it returns null.
     * <p/>
     * Date: 05.02.19
     *
     * @author Roman Pesek
     */
    ComponentDetailBean getCompositeComponentDetail(String id);

    /**
     * Interface for saving the composite components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean setCompositeComponent(String name, String version, List<String> listId);

    /**
     * Interface for remove the composite components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean removeCompositeComponent(String id);
}
