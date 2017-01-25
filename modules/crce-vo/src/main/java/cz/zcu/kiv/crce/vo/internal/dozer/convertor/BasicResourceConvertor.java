package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.IdentityCapabilityVO;

/**
 * Convertor of Resource instances into a value object (BasicResourceVO) with only elementary
 * information about the resource.
 *
 * Date: 10.5.15
 *
 * @author Jakub Danek
 */
public class BasicResourceConvertor extends DozerConverter<Resource, BasicResourceVO> implements MapperAware {

    private MetadataService metadataService;
    private Mapper mapper;

    public BasicResourceConvertor() {
        super(Resource.class, BasicResourceVO.class);
    }

    public BasicResourceConvertor(MetadataService metadataService) {
        this();
        this.metadataService = metadataService;
    }

    @Override
    public BasicResourceVO convertTo(Resource source, BasicResourceVO destination) {
        if(metadataService == null) {
            throw  new UnsupportedOperationException("Converter doesn't work without metadataService!");
        }
        Capability identity = metadataService.getIdentity(source);

        String id = source.getId();

        IdentityCapabilityVO idVO = mapper.map(identity, IdentityCapabilityVO.class);

        if(destination == null) {
            destination = new BasicResourceVO(id, idVO);
        } else {
            destination.setId(id);
            destination.setIdentity(idVO);
        }

        return destination;
    }

    @Override
    public Resource convertFrom(BasicResourceVO source, Resource destination) {
        return null;
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }
}
