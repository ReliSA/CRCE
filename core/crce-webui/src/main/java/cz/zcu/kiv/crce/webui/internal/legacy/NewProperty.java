package cz.zcu.kiv.crce.webui.internal.legacy;

/**
 * A property that can be set to a Resource or a Capability. 
 * 
 * This Property corresponds to <code>cz.zcu.kiv.crce.metadata.Property</code>
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public interface NewProperty extends PropertyProvider<NewProperty> {

	String getName();
}
