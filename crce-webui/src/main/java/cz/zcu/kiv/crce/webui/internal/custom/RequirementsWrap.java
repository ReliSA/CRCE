package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Requirement;

public class RequirementsWrap extends RequirementAdapter {
	protected Requirement r;
	
	
	public RequirementsWrap(Requirement r){
		this.r=r;
	}
	
	
		
	@Override
	public String getName() {		
		return r.getName();
	}	
	@Override
	public String getFilter() {
		return r.getFilter();
	}
	@Override
	public boolean isExtend() {		
		return r.isExtend();
	}
	@Override
	public boolean isMultiple() {		
		return r.isMultiple();
	}
	@Override
	public boolean isOptional() {	
		return r.isOptional();
	}
	@Override
	public boolean isWritable() {	
		return r.isWritable();
	}
}
