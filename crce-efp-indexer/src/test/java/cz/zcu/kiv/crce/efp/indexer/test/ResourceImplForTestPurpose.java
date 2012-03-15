package cz.zcu.kiv.crce.efp.indexer.test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;

public class ResourceImplForTestPurpose implements Resource {

	String presName;
	ArrayList<Capability> capy=new ArrayList<Capability>();
	ArrayList<Requirement> reqy=new ArrayList<Requirement>();
	
	URI uri;
	
	public ArrayList<Capability> getCapy(){
		return capy;
	}

	@Override
	public Resource unsetProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, String arg1, Type arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, Set arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, URI arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, URL arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, Version arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(Property arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPropertyString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property[] getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unsetWritable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetRequirement(Requirement arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetCategory(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetCapability(Capability arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(String arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(Version arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(Version arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUri(URI arg0) {
		this.uri=arg0;
	}

	@Override
	public void setSymbolicName(String arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSymbolicName(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRepository(WritableRepository arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPresentationName(String arg0) {
		presName=arg0;

	}

	@Override
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVersionStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSymbolicNameStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRequirement(Requirement arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCategory(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCapability(Capability arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Version getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public String getSymbolicName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Requirement[] getRequirements(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement[] getRequirements() {
		Requirement[] pole=new Requirement[reqy.size()];
		int i=0;
		for(Requirement cap : reqy){
		pole[i]=reqy.get(i);
		i++;
		}
		return pole;
	}

	@Override
	public Repository getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getRelativeUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getPropertiesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPresentationName() {
		return presName;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capability[] getCapabilities(String arg0) {
		//return (Capability[]) capy.toArray();
		return null;
	}

	@Override
	public Capability[] getCapabilities() {
		Capability[] pole=new Capability[capy.size()];
		int i=0;
		for(Capability cap : capy){
		pole[i]=capy.get(i);
		i++;
		}
		return pole;
	}

	@Override
	public Requirement createRequirement(String arg0) {
		Requirement req0=new RequirementImpl(arg0);
		reqy.add(req0);
		return req0;
	}

	@Override
	public Capability createCapability(String arg0) {
		Capability cap0=new CapabilityImpl(arg0);
		capy.add(cap0);
		return cap0;
	}

	@Override
	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRequirement(Requirement arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCategory(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCapability(Capability arg0) {
		// TODO Auto-generated method stub

	}

	
}
