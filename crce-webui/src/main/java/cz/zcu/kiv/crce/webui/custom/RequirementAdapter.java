package cz.zcu.kiv.crce.webui.custom;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;

/**
 * Adapter for RequirementClass -> Creates an adapter for wrapper {@link RequirementWrap}
 * 
 * @author Sandcrew
 * 
 */
public class RequirementAdapter implements Requirement {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMultiple() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOptional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExtend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSatisfied(Capability capability) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Requirement setFilter(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement setMultiple(boolean multiple) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement setOptional(boolean optional) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement setExtend(boolean extend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement setComment(String comment) {
		// TODO Auto-generated method stub
		return null;
	}

}
