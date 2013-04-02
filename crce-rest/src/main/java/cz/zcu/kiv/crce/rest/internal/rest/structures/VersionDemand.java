package cz.zcu.kiv.crce.rest.internal.rest.structures;

import org.osgi.framework.Version;

public class VersionDemand {
	
	public static final String GREATER_THAN = "greater-than";
	public static final String LESS_THAN = "less-than";
	public static final String EQUEAL = "equal";
	public static final String LESS_EQUEAL = "less-equal";
	public static final String GREATER_EQUEAL = "greater-equal";
	public static final String NOT_EQUEAL = "not-equal";
	
	private Version version;
	private String operation;
	public Version getVersion() {
		return version;
	}
	public void setVersion(Version version) {
		this.version = version;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
	
	
}
