package cz.zcu.kiv.crce.compatibility.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;

/**
 * Date: 13.3.14
 *
 * @author Jakub Danek
 */
public class DiffImpl implements Diff {

    private String name;
    private Difference value;
    private DifferenceLevel level;
    private DifferenceRole role;
    private List<Diff> children;
    private String namespace;

    public DiffImpl() {
        this.children = new ArrayList<>();
        this.name = "";
        this.value = Difference.UNK;
        this.level = DifferenceLevel.UNKNOWN;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public List<Diff> getChildren() {
        return children;
    }

    @Override
    public void addChild(@Nonnull Diff child) {
        this.children.add(child);
    }

    @Override
    public void addChildren(@Nonnull List<Diff> children) {
        this.children.addAll(children);
    }

    @Override
    public DifferenceRole getRole() {
        return role;
    }

    @Override
    public void setRole(DifferenceRole role) {
        this.role = role;
    }

    @Override
    public DifferenceLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(DifferenceLevel level) {
        this.level = level;
    }

    @Override
    public Difference getValue() {
        return value;
    }

    @Override
    public void setValue(Difference value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiffImpl diff = (DiffImpl) o;

        if (children != null ? !children.equals(diff.children) : diff.children != null) return false;
        if (level != diff.level) return false;
        if (name != null ? !name.equals(diff.name) : diff.name != null) return false;
        if (namespace != null ? !namespace.equals(diff.namespace) : diff.namespace != null) return false;
        if (role != diff.role) return false;
        if (value != diff.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }
}
