package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Date: 11.4.16
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "requirements")
public class RequirementListVO {

    private List<GenericRequirementVO> requirements = new LinkedList<>();
    private List<DirectiveVO> directives = new LinkedList<>();

    @Nonnull
    @XmlElementRef
    public List<GenericRequirementVO> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<GenericRequirementVO> requirements) {
        this.requirements = requirements;
    }

    @Nonnull
    @XmlElementRef
    public List<DirectiveVO> getDirectives() {
        return directives;
    }

    public void setDirectives(List<DirectiveVO> directives) {
        this.directives = directives;
    }

    public boolean andRequirements() {
        for (DirectiveVO directive : directives) {
            if(directive.getName().equals("operator") && directive.getValue().equals("or")) {
                return false;
            }
        }
        return true;
    }
}
