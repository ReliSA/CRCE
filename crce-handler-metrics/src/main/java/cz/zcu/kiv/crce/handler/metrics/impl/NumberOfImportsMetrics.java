package cz.zcu.kiv.crce.handler.metrics.impl;

import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

public class NumberOfImportsMetrics implements ComponentMetrics {

	private long numOfImports;
	
	public NumberOfImportsMetrics(Resource resource) {
		
		List<Requirement> requirements = resource.getRequirements();
		
		numOfImports = requirements.size();
	}
	
	@Override
	public void init() {
		// nothing to do here
	}

	@Override
	@Nonnull
	public String getName() {
		return "number-of-imports";
	}

	@Override
	@Nonnull
	@SuppressWarnings("rawtypes")
	public Class getType() {
		return Long.class;
	}

	@Override
	@Nonnull
	public Object computeValue() {
		return numOfImports;
	}

}
