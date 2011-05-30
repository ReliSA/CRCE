package cz.zcu.kiv.crce.webui.custom;

import cz.zcu.kiv.crce.metadata.Requirement;
/**
 * A wrapper class for {@link Requirement} implementation
 * @author Sandcrew
 *
 */
public class RequirementsWrap extends RequirementAdapter {
	protected Requirement r;
	
	/**
	 * Constructor for wrap
	 * @param r - Requirement to be wrapped by this class
	 */
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
