package cz.zcu.kiv.crce.vo.internal.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.config.BeanContainer;
import org.osgi.framework.FrameworkUtil;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;
import cz.zcu.kiv.crce.vo.internal.dozer.OSGiDozerClassLoader;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.BasicResourceConvertor;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.DetailedResourceConverter;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.IdentityCapabilityConvertor;
import cz.zcu.kiv.crce.vo.internal.dozer.convertor.RequirementConvertor;
import cz.zcu.kiv.crce.vo.model.compatibility.CompatibilityVO;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;
import cz.zcu.kiv.crce.vo.model.optimizer.CostFunctionDescriptorVO;
import cz.zcu.kiv.crce.vo.service.MappingService;

/**
 * Dozer implementation of MappingService interface.
 *
 * Date: 15.5.15
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
public class MappingServiceDozer implements MappingService {

    private DozerBeanMapper mapper;
    private MetadataService metadataService;
    private MetadataFactory metadataFactory;


    @Override
    public BasicResourceVO mapBasic(@Nullable Resource resource) {
        if(resource == null) {
            return null;
        }
        return mapper.map(resource, BasicResourceVO.class);
    }

    @Nonnull
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
    public DetailedResourceVO mapFull(@Nullable Resource resource) {
        if(resource == null) {
            return null;
        }
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

    @Nonnull
    @Override
    public List<CompatibilityVO> mapCompatibility(List<Compatibility> diffs) {
        List<CompatibilityVO> list = new LinkedList<>();

        CompatibilityVO vo;
        for (Compatibility diff : diffs) {
            vo = mapCompatibility(diff);
            if(vo != null) {
                list.add(vo);
            }
        }

        return list;
    }

    @Nullable
    @Override
    public CompatibilityVO mapCompatibility(@Nullable Compatibility diff) {
        if(diff == null) {
            return null;
        }
        return mapper.map(diff, CompatibilityVO.class);
    }

    @Nonnull
    @Override
    public List<Requirement> map(List<GenericRequirementVO> requirements) {
        List<Requirement> req = new LinkedList<>();

        Requirement r;
        for (GenericRequirementVO requirement : requirements) {
            r = mapper.map(requirement, Requirement.class);
            if(r != null) {
                req.add(r);
            }
        }

        return req;
    }

    @Nonnull
    @Override
    public List<CostFunctionDescriptorVO> mapCostFunction(List<CostFunctionFactory> descriptors) {
        List<CostFunctionDescriptorVO> vos = new LinkedList<>();

        CostFunctionDescriptorVO vo;
        for (CostFunctionFactory descriptor : descriptors) {
            vo = mapCostFunction(descriptor);
            if(vo != null) {
                vos.add(vo);
            }
        }

        return vos;
    }

    @Nullable
    @Override
    public CostFunctionDescriptorVO mapCostFunction(@Nullable CostFunctionFactory descriptor) {
        return descriptor != null ? mapper.map(descriptor, CostFunctionDescriptorVO.class) : null;
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
        converters.add(new RequirementConvertor(metadataFactory));
        mapper.setCustomConverters(converters);

        // Force loading of the dozer.xml now instead of loading it
        // upon the first mapping call
        mapper.getMappingMetadata();
    }

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public void setMetadataFactory(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }
}
