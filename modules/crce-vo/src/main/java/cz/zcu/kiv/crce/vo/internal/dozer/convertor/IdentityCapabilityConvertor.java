package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import java.util.List;

import org.dozer.DozerConverter;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceMetadata;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.metadata.IdentityCapabilityVO;

/**
 * Date: 25.5.15
 *
 * @author Jakub Danek
 */
public class IdentityCapabilityConvertor extends DozerConverter<Capability, IdentityCapabilityVO> {

    public IdentityCapabilityConvertor() {
        super(Capability.class, IdentityCapabilityVO.class);
    }

    @Override
    public IdentityCapabilityVO convertTo(Capability identity, IdentityCapabilityVO destination) {
        String id = identity.getId();

        String name = identity.getAttributeValue(NsCrceMetadata.ATTRIBUTE__NAME);
        if(name == null) {
            name = "-";
        }

        Version v = identity.getAttributeValue(NsCrceMetadata.Identity.ATTRIBUTE__VERSION);
        String version = v != null ? v.toString() : "-";

        IdentityCapabilityVO idVO = new IdentityCapabilityVO(id, name, version);
        Long size = identity.getAttributeValue(NsCrceMetadata.ATTRIBUTE__SIZE);
        if(size != null) {
            idVO.setSize(size);
        }

        List<String> cats = identity.getAttributeValue(NsCrceMetadata.Identity.ATTRIBUTE__CATEGORIES);
        if(cats != null) {
            for (String c : cats) {
                idVO.getCategories().add(c);
            }
        }

        return idVO;
    }

    @Override
    public Capability convertFrom(IdentityCapabilityVO identityCapabilityVO, Capability capability) {
        return null;
    }
}
