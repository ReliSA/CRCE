package cz.zcu.kiv.crce.crce_webui_v2.repository.classes;

public class CapabilityAttributeBean {	
	private String designation;
	private String nameSpace;
	private String type;
	private String value;
	public String getDesignation() {
		if (designation == null){
			return "unknown";
		}
		else{
			return designation;
		}
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getNameSpace() {
		if (nameSpace == null){
			return "unknown";
		}
		else{
			return nameSpace;
		}
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getType() {
		if (type == null){
			return "unknown";
		}
		else{
			return type;
		}
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		if (value == null){
			return "unknown";
		}
		else{
			return value;
		}
	}
	public void setValue(String value) {
		this.value = value;
	}
}
