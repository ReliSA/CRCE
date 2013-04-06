package cz.zcu.kiv.crce.webui.internal.custom;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Version;

import cz.zcu.kiv.crce.webui.internal.legacy.Capability;
import cz.zcu.kiv.crce.webui.internal.legacy.Property;
import cz.zcu.kiv.crce.webui.internal.legacy.Requirement;
import cz.zcu.kiv.crce.webui.internal.legacy.Resource;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

public class ResourceAdapter implements Resource {

	@Override
	public Property[] getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPropertyString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, String value, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, Version version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, long llong) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, double ddouble) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource setProperty(String name, Set values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource unsetProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSymbolicName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getRelativeUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capability[] getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capability[] getCapabilities(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement[] getRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement[] getRequirements(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getPropertiesMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCategory(String category) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCapability(Capability capability) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRequirement(Requirement requirement) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSymbolicName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSymbolicName(String name, boolean isStatic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPresentationName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(Version version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(Version version, boolean isStatic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(String version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion(String version, boolean isStatic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCategory(String category) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCapability(Capability capability) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRequirement(Requirement requirement) {
		// TODO Auto-generated method stub

	}

	@Override
	public Capability createCapability(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requirement createRequirement(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unsetCategory(String category) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetCapability(Capability capability) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetRequirement(Requirement requirement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(long size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUri(URI uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unsetWritable() {
		// TODO Auto-generated method stub

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
	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}

//    @Override
//    public Repository getRepository() {
//		// TODO Auto-generated method stub
//		return null;
//    }
//
//    @Override
//    public void setRepository(WritableRepository repository) {
//		// TODO Auto-generated method stub
//    }
}
