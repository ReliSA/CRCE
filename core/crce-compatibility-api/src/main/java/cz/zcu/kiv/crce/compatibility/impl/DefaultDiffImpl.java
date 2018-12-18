package cz.zcu.kiv.crce.compatibility.impl;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of Diff interface. Serves as a starting point for future
 * implementations.
 */
public class DefaultDiffImpl implements Diff {

    private String name;
    private Difference value;
    private DifferenceLevel level;
    private DifferenceRole role;
    private List<Diff> children;
    private String namespace;
    private String syntax;

    public DefaultDiffImpl() {
        this.children = new ArrayList<>();
        this.name = "";
        this.value = Difference.UNK;
        this.level = DifferenceLevel.UNKNOWN;
    }

    @Nonnull
    @Override
    public DifferenceLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(@Nonnull DifferenceLevel level) {
        this.level = level;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public Difference getValue() {
        return value;
    }

    @Override
    public void setValue(@Nonnull Difference value) {
        this.value = value;
    }

    @Nonnull
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

    @Nullable
    @Override
    public DifferenceRole getRole() {
        return role;
    }

    @Override
    public void setRole(@Nullable DifferenceRole role) {
        this.role = role;
    }

    @Nullable
    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(@Nullable String namespace) {
        this.namespace = namespace;
    }

    @Nullable
    @Override
    public String getSyntax() {
        return syntax;
    }

    @Override
    public void setSyntax(@Nullable String syntax) {
        this.syntax = syntax;
    }
}
