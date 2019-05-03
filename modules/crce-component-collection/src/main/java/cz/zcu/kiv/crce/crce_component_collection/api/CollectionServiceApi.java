package cz.zcu.kiv.crce.crce_component_collection.api;

import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;

import java.util.List;

/**
 * The interface provides methods for managing artifact sets.
 * <p/>
 * Date: 05.02.19
 *
 * @author Roman Pesek
 */
public interface CollectionServiceApi {
    /**
     * Interface for accessing all records in the list of collection components.
     * <p/>
     * Date: 05.02.19
     *
     * @author Roman Pesek
     */
    List<CollectionBean> getCollectionComponentAll();

    /**
     * Interface for component access by id. If it is not found, it returns null.
     * <p/>
     * Date: 05.02.19
     *
     * @author Roman Pesek
     */
    CollectionDetailBean getCollectionComponentDetail(String id);

    /**
     * Interface for saving the collection components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean setCollectionComponent(String name, String version, List<String> specificArtifacts,
                                   List<String> parameters, List<String> rangeArtifacts);

    /**
     * Interface for remove the collection components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean removeCollectionComponent(String id);

    /**
     * Interface for update the collection components.
     * <p/>
     * Date: 07.02.19
     *
     * @author Roman Pesek
     */
    boolean updateCollectionComponent(String id, String name, String version,  List<String> specificArtifacts,
                                      List<String> parameters, List<String> rangeArtifacts);
}
