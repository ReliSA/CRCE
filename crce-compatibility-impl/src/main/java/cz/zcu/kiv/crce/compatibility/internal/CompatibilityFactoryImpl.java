package cz.zcu.kiv.crce.compatibility.internal;

import java.util.List;

import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityFactoryImpl implements CompatibilityFactory {
    @Override
    public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, String baseName, Version baseVersion, Difference diffValue, List<Diff> diffTree) {
        return new CompatibilityImpl(id, resourceName, resourceVersion, baseName, baseVersion, diffValue, diffTree);
    }

    @Override
    public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, Version baseVersion, Difference diffValue, List<Diff> diffTree) {
        return this.createCompatibility(id, resourceName, resourceVersion, resourceName, baseVersion, diffValue, diffTree);
    }

    @Override
    public Diff createEmptyDiff() {
        return new DiffImpl();
    }
}
