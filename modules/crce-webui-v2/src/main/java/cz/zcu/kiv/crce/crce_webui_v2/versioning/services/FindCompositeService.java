package cz.zcu.kiv.crce.crce_webui_v2.versioning.services;

import cz.zcu.kiv.crce.crce_component_versioning.api.bean.ComponentBean;
import cz.zcu.kiv.crce.crce_component_versioning.api.impl.VersioningService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindCompositeService {
    public List<ComponentBean> getFindComponentBean(String stringFilter){
        boolean passesFilter = false;
        VersioningService versioningService = new VersioningService();
        ArrayList<ComponentBean> arrayList = new ArrayList<>();
        for (ComponentBean componentBean : versioningService.getCompositeComponentAll()){
            if(stringFilter == null || stringFilter.isEmpty()){
                passesFilter = true;
            }
            else{
                passesFilter = componentBean.toString().toLowerCase().contains(stringFilter.toLowerCase());
            }
            if (passesFilter) {
                arrayList.add(componentBean);
            }
        }
        Collections.sort(arrayList, new Comparator<ComponentBean>() {
            @Override
            public int compare(ComponentBean o1, ComponentBean o2) {
                return (int) (o2.hashCode() - o1.hashCode());
            }
        });
        return arrayList;
    }
}
