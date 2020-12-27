package cz.zcu.kiv.crce.crce_webui_v2.collection.services;

import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionBean;
import cz.zcu.kiv.crce.crce_component_collection.api.impl.CollectionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindCollectionService {
    /**
     * The full-text filter of the collection list.
     * <p/>
     * Date: 02.05.19
     * @param stringFilter search string in collection name
     *
     * @author Roman Pesek
     */
    public List<CollectionBean> getFindCollectionBean(String stringFilter){
        boolean passesFilter = false;
        CollectionService collectionService = new CollectionService();
        ArrayList<CollectionBean> arrayList = new ArrayList<>();
        for (CollectionBean collectionBean : collectionService.getCollectionComponentAll()){
            if(stringFilter == null || stringFilter.isEmpty()){
                passesFilter = true;
            }
            else{
                passesFilter = collectionBean.toString().toLowerCase().contains(stringFilter.toLowerCase());
            }
            if (passesFilter) {
                arrayList.add(collectionBean);
            }
        }
        Collections.sort(arrayList, new Comparator<CollectionBean>() {
            @Override
            public int compare(CollectionBean o1, CollectionBean o2) {
                return (int) (o2.hashCode() - o1.hashCode());
            }
        });
        return arrayList;
    }
}
