package cz.zcu.kiv.crce.vo.model.compatibility;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 * Date: 4.9.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "diff")
public class DiffVO extends ValueObject {
    /**
     * DifferenceLevel represents particular part of bundle this
     * diff is related to. E.g. whole package, class or just a method or a field.
     *
     */
    private DifferenceLevel level;
    /**
     *  Name of the element this diff is related to. E.g. a class name.
     */
    private String name;
    /**
     *  Difference value represent the type of change made.
     */
    private Difference value;
    /**
     * Role represents meaning of the item this diff is about.
     * <p/>
     * Capability (e.g. exported packages) or requirement.
     */
    private DifferenceRole role;
    /**
     * Language syntax this diff represents. E.g. java.
     */
    private String syntax;
    /**
     * Children represent more detailed difference data.
     * <p/>
     * E.g. for a package, children would list modified classes.
     */
    private List<DiffVO> children;


    public DiffVO() {
        super(null);
    }

    @XmlAttribute(name = "level")
    public DifferenceLevel getLevel() {
        return level;
    }

    public void setLevel(DifferenceLevel level) {
        this.level = level;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "diff")
    public Difference getValue() {
        return value;
    }

    public void setValue(Difference value) {
        this.value = value;
    }

    @XmlAttribute(name = "role")
    public DifferenceRole getRole() {
        return role;
    }

    public void setRole(DifferenceRole role) {
        this.role = role;
    }

    @XmlAttribute(name = "syntax")
    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    @XmlElementRef
    public List<DiffVO> getChildren() {
        return children;
    }

    public void setChildren(List<DiffVO> children) {
        this.children = children;
    }
}
