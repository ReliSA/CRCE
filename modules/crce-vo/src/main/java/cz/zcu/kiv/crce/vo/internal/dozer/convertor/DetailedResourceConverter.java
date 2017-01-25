package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericCapabilityVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;
import cz.zcu.kiv.crce.vo.model.metadata.PropertyVO;

/**
 * Convertor of Resource instances into DetailedResourceVO, which basically copies the
 * generic metadata API model.
 *
 * Date: 1.6.15
 *
 * @author Jakub Danek
 */
public class DetailedResourceConverter extends DozerConverter<Resource, DetailedResourceVO> implements MapperAware {

    private Mapper mapper;
    private MetadataService metadataService;
    private BasicResourceConvertor basicConvertor;

    private DetailedResourceConverter() {
        super(Resource.class, DetailedResourceVO.class);
    }

    public DetailedResourceConverter(MetadataService metadataService) {
        this();

        if(metadataService == null) {
            throw  new UnsupportedOperationException("Converter doesn't work without metadataService!");
        }
        this.metadataService = metadataService;
        this.basicConvertor = new BasicResourceConvertor(this.metadataService);

    }

    @Override
    public DetailedResourceVO convertTo(Resource resource, DetailedResourceVO destination) {
        if (destination == null) {
            destination = new DetailedResourceVO();
        }

        //convert identity
        destination = (DetailedResourceVO) basicConvertor.convertTo(resource, destination);

        mapCapabilities(resource, destination);
        mapRequirements(resource, destination);
        mapProperties(resource, destination);

        return destination;
    }

    private void mapCapabilities(Resource src, DetailedResourceVO dest) {
        boolean filtered = false;
        for (Capability capability : src.getRootCapabilities()) {
            if(filtered || capability.getNamespace().equals(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY)) {
                //identity capability already converted
                filtered = true;
                continue;
            }

            dest.getCapabilities().add(mapper.map(capability, GenericCapabilityVO.class));
        }
    }

    private void mapRequirements(Resource src, DetailedResourceVO dest) {
        for (Requirement req : src.getRequirements()) {
            dest.getRequirements().add(mapper.map(req, GenericRequirementVO.class));
        }
    }

    private void mapProperties(Resource src, DetailedResourceVO dest) {
        for (Property property : src.getProperties()) {
            dest.getProperties().add(mapper.map(property, PropertyVO.class));
        }
    }

    @Override
    public Resource convertFrom(DetailedResourceVO detailedResourceVO, Resource resource) {
        return null;
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
        this.basicConvertor.setMapper(mapper);
    }
}
