package cz.zcu.kiv.crce.webui.internal.custom;



import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

public class ResourceExt extends ResourceWrap {
	private boolean satisfied;
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
