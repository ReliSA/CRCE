package cz.zcu.kiv.crce.webui.custom;



import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
/**
 * A class which provides extended functionality
 * for Resource interface to WebPages.
 * Here should be special funcionality for web
 * @author Sandcrew
 *
 */
public class ResourceExt extends ResourceWrap {
	private boolean satisfied;//Whether are requirements satisfied
	/**
	 * Default contructor with class to be wrapped as param
	 * sets satisfied implicitly on true
	 * @param r - A Resource to be wrapped by this class
	 */
	public ResourceExt(Resource r) {
		super(r);
		this.satisfied=true;
	}
	
	public boolean getSatisfied(){
		return satisfied;
	}
	
	
	@Override
	public void addRequirement(Requirement requirement) {
		satisfied=false;
		r.unsetRequirement(requirement);
		RequirementExt rext = new RequirementExt(requirement);
		r.addRequirement(rext);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Resource) return this.getUri().equals(((Resource)obj).getUri());
		return super.equals(obj);
	}

}
