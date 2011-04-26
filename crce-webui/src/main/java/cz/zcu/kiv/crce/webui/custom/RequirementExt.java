package cz.zcu.kiv.crce.webui.custom;

import cz.zcu.kiv.crce.metadata.Requirement;

public class RequirementExt extends RequirementsWrap {
	private boolean satisfied;
	public RequirementExt(Requirement r) {
		super(r);
		satisfied=false;
	}
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}
	
	public boolean getSatisfied(){
		return satisfied;
	}
}
