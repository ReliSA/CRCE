package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import java.util.List;

import org.dozer.DozerConverter;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.metadata.IdentityCapabilityVO;

/**
 * Convertor of a CRCE identity Capability into special VO which is easier
 * to use within clients (UI, web services) than the generic Capability API.
 *
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

        String name = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME);
        if(name == null) {
            name = "-";
        }

        Version v = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__VERSION);
        String version = v != null ? v.toString() : "-";

        IdentityCapabilityVO idVO = new IdentityCapabilityVO(id, name, version);
        Long size = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__SIZE);
        if(size != null) {
            idVO.setSize(size);
        }

        List<String> cats = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
        if(cats != null) {
            for (String c : cats) {
                idVO.addCategory(c);
            }
        }

        return idVO;
    }

    @Override
    public Capability convertFrom(IdentityCapabilityVO identityCapabilityVO, Capability capability) {
        return null;
    }
}
