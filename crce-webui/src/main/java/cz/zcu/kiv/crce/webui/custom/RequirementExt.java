package cz.zcu.kiv.crce.webui.custom;

import cz.zcu.kiv.crce.metadata.Requirement;
/**
 * A wrapper of Requirement Interface needed for Web.
 * Here should be added functionality needed extra for web.
 * Uses {@link RequirementAdapter}
 * @author Sandcrew
 *
 */
public class RequirementExt extends RequirementsWrap {
	private boolean satisfied; // Whether dependency is sarisfied.
	/**
	 * Constructor for RequirementExt sets satisfied false
	 * Usually when creating ExtRequirement it is unsatisfied.
	 * @param r
	 */
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
