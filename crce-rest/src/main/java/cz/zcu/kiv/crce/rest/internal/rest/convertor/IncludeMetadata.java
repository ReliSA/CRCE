package cz.zcu.kiv.crce.rest.internal.rest.convertor;


/**
 * Filter criteria for GET Metadata operation.
 * 
 * Filter criteria determines, which parts of XML with metedata should be generated. 
 *  
 * @author Jan Reznicek
 *
 */
public class IncludeMetadata {
	private boolean includeCore;
	
	private boolean includeCaps;
	private String includeCapseByName;
	
	private boolean includeReqs;
	private String includeReqsByName;
	
	private boolean includeProps;
	private String includePropsByName;
	
	public IncludeMetadata() {
		includeCore = false;
		
		includeCaps = false;
		includeCapseByName = null;
		
		includeReqs = false;
		includeReqsByName = null;
		
		includeProps = false;
		includeReqsByName = null;
	}
	
	
	public boolean isIncludeCore() {
		return includeCore;
	}
	public void setIncludeCore(boolean includeCore) {
		this.includeCore = includeCore;
	}
	public boolean isIncludeCaps() {
		return includeCaps;
	}
	public void setIncludeCaps(boolean includeCaps) {
		this.includeCaps = includeCaps;
	}
	public String getIncludeCapseByName() {
		return includeCapseByName;
	}
	public void setIncludeCapseByName(String includeCapseByName) {
		this.includeCapseByName = includeCapseByName;
	}
	public boolean isIncludeReqs() {
		return includeReqs;
	}
	public void setIncludeReqs(boolean includeReqs) {
		this.includeReqs = includeReqs;
	}
	public String getIncludeReqsByName() {
		return includeReqsByName;
	}
	public void setIncludeReqsByName(String includeReqsByName) {
		this.includeReqsByName = includeReqsByName;
	}
	public boolean isIncludeProps() {
		return includeProps;
	}
	public void setIncludeProps(boolean includeProps) {
		this.includeProps = includeProps;
	}
	public String getIncludePropsByName() {
		return includePropsByName;
	}
	public void setIncludePropsByName(String includePropsByName) {
		this.includePropsByName = includePropsByName;
	}
	
	
}
