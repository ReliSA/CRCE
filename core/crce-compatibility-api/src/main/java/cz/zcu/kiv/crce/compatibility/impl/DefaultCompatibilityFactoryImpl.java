package cz.zcu.kiv.crce.compatibility.impl;

import cz.zcu.kiv.crce.compatibility.*;
import cz.zcu.kiv.crce.metadata.type.Version;
import org.apache.felix.dm.annotation.api.Component;

import java.util.List;

/**
 * Factory which creates default implementations of Compatibility interfaces.
 */
@Component(provides = {CompatibilityFactory.class})
public class DefaultCompatibilityFactoryImpl implements CompatibilityFactory {

    @Override
    public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, String baseName, Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract) {
        return new DefaultCompatibilityImpl(id, resourceName, resourceVersion, baseName, baseVersion, diffValue, diffValues, contract);
    }

    @Override
    public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract) {
        return new DefaultCompatibilityImpl(id, resourceName, resourceVersion, resourceName, baseVersion, diffValue, diffValues, contract);
    }

    @Override
    public Diff createEmptyDiff() {
        return new DefaultDiffImpl();
    }
}
