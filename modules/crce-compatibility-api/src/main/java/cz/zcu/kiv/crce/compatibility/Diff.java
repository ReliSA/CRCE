package cz.zcu.kiv.crce.compatibility;


import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Detailed description of differences between two bundles.
 *
 * Date: 16.11.13
 *
 * @author Jakub Danek
 */
public interface Diff {

    /**
     * DifferenceLevel represents particular part of bundle this
     * diff is related to. E.g. whole package, class or just a method or a field.
     *
     * @return
     */
    @Nonnull
    DifferenceLevel getLevel();

    void setLevel(@Nonnull DifferenceLevel level);

    /**
     * Name of the element this diff is related to. E.g. a class name.
     *
     * @return
     */
    @Nonnull
    String getName();

    void setName(@Nonnull String name);

    /**
     * Difference value represent the type of change made.
     *
     * @return
     */
    @Nonnull
    Difference getValue();

    void setValue(@Nonnull Difference value);

    /**
     * Children represent more detailed difference data.
     * <p/>
     * E.g. for a package, children would list modified classes.
     *
     * @return
     */
    @Nonnull
    List<Diff> getChildren();

    void addChild(@Nonnull Diff child);

    void addChildren(@Nonnull List<Diff> children);

    /**
     * Role represents meaning of the item this diff is about.
     * <p/>
     * Capability (e.g. exported packages) or requirement.
     *
     * @return
     */
    @Nullable
    DifferenceRole getRole();

    void setRole(@Nullable DifferenceRole role);

    /**
     * Namespace value.
     * <p/>
     * E.g. osgi.wiring.package for exported/imported packages of OSGi bundle
     *
     * @return
     */
    @Nullable
    String getNamespace();

    void setNamespace(@Nullable String namespace);

    @Nullable
    String getSyntax();

    void setSyntax(@Nullable String syntax);

}
