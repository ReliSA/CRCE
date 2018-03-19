package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.impl.GenericAttributeType;
import cz.zcu.kiv.crce.vo.model.metadata.AttributeVO;
import cz.zcu.kiv.crce.vo.model.metadata.DirectiveVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;

/**
 *  Convertor responsible for mapping of {@link cz.zcu.kiv.crce.metadata.Resource} to
 *  {@link GenericRequirementVO} and vice versa.
 *
 * Date: 11.4.16
 *
 * @author Jakub Danek
 */
public class RequirementConvertor extends DozerConverter<Requirement, GenericRequirementVO> implements MapperAware {

    private MetadataFactory metadataFactory;
    private Mapper mapper;

    public RequirementConvertor(MetadataFactory metadataFactory) {
        super(Requirement.class, GenericRequirementVO.class);
        this.metadataFactory = metadataFactory;
    }

    @Override
    public GenericRequirementVO convertTo(Requirement requirement, GenericRequirementVO vo) {
        if(requirement == null) {
            return null;
        }
        if(vo == null) {
            vo = new GenericRequirementVO();
        }

        vo.setId(requirement.getId());
        vo.setNamespace(requirement.getNamespace());

        for (Attribute<?> attribute : requirement.getAttributes()) {
            vo.addAttribute(mapper.map(attribute, AttributeVO.class));
        }

        for (Requirement child : requirement.getChildren()) {
            vo.addChild(mapper.map(child, GenericRequirementVO.class));
        }

        for (Map.Entry<String, String> dir : requirement.getDirectives().entrySet()) {
            vo.addDirective(new DirectiveVO(dir.getKey(), dir.getValue()));
        }

        return vo;
    }

    @Override
    public Requirement convertFrom(GenericRequirementVO vo, Requirement requirement) {
        if (requirement == null) {
            requirement = metadataFactory.createRequirement(vo.getNamespace(), vo.getId());
        }

        for (AttributeVO attribute : vo.getAttributes()) {
            AttributeType type = new GenericAttributeType(attribute.getName(), attribute.getType());
            Object value = retype(attribute.getType(), attribute.getValue());

            requirement.addAttribute(metadataFactory.createAttribute(type, value));
        }

        for (GenericRequirementVO child : vo.getChildren()) {
            requirement.addChild(mapper.map(child, Requirement.class));
        }

        for (DirectiveVO dir : vo.getDirectives()) {
            requirement.setDirective(dir.getName(), dir.getValue());
        }

        return requirement;
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Retypes string to a new object according to the type passed as argument.
     * @param type Target variable type.
     * @param value String value.
     * @return Retyped object.
     */
    private Object retype(String type, String value) {
        switch (type) {
            case "Boolean":
            case "java.lang.Boolean":
                return Boolean.valueOf(value);

            case "List":
            case "java.util.List":
                if (value.equals("")) {
                    return Collections.emptyList();
                } else {
                    return Arrays.asList(value.split(","));
                }

            default:
                return value;
        }
    }

}
