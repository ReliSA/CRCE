package cz.zcu.kiv.crce.vo.internal.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.config.BeanContainer;
import org.osgi.framework.FrameworkUtil;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.vo.internal.dozer.OSGiDozerClassLoader;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.BasicResourceConvertor;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.DetailedResourceConverter;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.IdentityCapabilityConvertor;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.service.MappingService;

/**
 * Dozer implementation of MappingService interface.
 *
 * Date: 15.5.15
 *
 * @author Jakub Danek
 */
public class MappingServiceDozer implements MappingService {

    private DozerBeanMapper mapper;
    private MetadataService metadataService;


    @Override
    public BasicResourceVO mapBasic(Resource resource) {
        if(resource == null) {
            return null;
        }
        return mapper.map(resource, BasicResourceVO.class);
    }

    @Override
    public List<BasicResourceVO> mapBasic(List<Resource> resources) {
        List<BasicResourceVO> vos = new ArrayList<>(resources.size());

        BasicResourceVO v;
        for (Resource resource : resources) {
            v = mapBasic(resource);
            if (v != null) {
                vos.add(v);
            }
        }

        return vos;
    }

    @Nullable
    @Override
    public DetailedResourceVO mapFull(Resource resource) {
        return mapper.map(resource, DetailedResourceVO.class);
    }

    @Nonnull
    @Override
    public List<DetailedResourceVO> mapFull(List<Resource> resources) {
        List<DetailedResourceVO>  list = new ArrayList<>();

        DetailedResourceVO v;
        for (Resource resource : resources) {
            v = mapFull(resource);
            if(v != null) {
                list.add(v);
            }
        }

        return list;
    }

    /**
     * Called by OSGi
     */
    public void init() {
        //workaround for Dozer OSGi unsuitability
        OSGiDozerClassLoader cl = new OSGiDozerClassLoader();
        cl.setContext(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
        BeanContainer.getInstance().setClassLoader(cl);

        List<String> mappings = new LinkedList<>();
        mappings.add("mappings.xml");
        mapper = new DozerBeanMapper(mappings);

        List<CustomConverter> converters = new LinkedList<>();
        converters.add(new BasicResourceConvertor(metadataService));
        converters.add(new DetailedResourceConverter(metadataService));
        converters.add(new IdentityCapabilityConvertor());
        mapper.setCustomConverters(converters);

        // Force loading of the dozer.xml now instead of loading it
        // upon the first mapping call
        mapper.getMappingMetadata();
    }

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }
}
