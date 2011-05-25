/**
 * This is CoSi Framework and Toolkit – an obsessively black-box component model
 * in OSGi style.
 *
 * Copyright @ 2007-2009 Department of Computer Science and Engineering,
 * University of West Bohemia, Pilsen, CZ
 *
 * This software and this file is available under the Creative Commons
 * Attribution-Noncommercial-Share Alike license. You may obtain a copy
 * of the License at http://creativecommons.org/licenses/ .
 *
 * This software is provided on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations.
 */

package cz.zcu.kiv.cosi.container.bundles.extrafuncregistryservice.impl;

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncType;

/**
 * Class representing one constraint entry in Extra Functional registry.
 * 
 * Type represents desired type of Extra-Functinal property.
 * 
 * Constraints represents constraints of Extra-Functional property (e.g.
 * accepted values, interval, etc.).
 * 
 * @author Vojtech Liska
 *
 */

public class ExtraFuncRegistryEntry {
	private String type;
	private String valueType;
	private ExtraFuncType constraints;
	
	public ExtraFuncRegistryEntry(String Ps_type, ExtraFuncType P_constraints) {
		this.type = Ps_type;
		this.constraints = P_constraints;
		this.valueType = "";
		
	}

	
	public ExtraFuncRegistryEntry(String Ps_type, String Ps_valueType, ExtraFuncType P_constraints) {
		this.type = Ps_type;
		this.valueType = Ps_valueType;
		this.constraints = P_constraints;
		
	}

	/**
	 * @return Returns type (simple or comples) of Extra-Functional registry entry. 
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Returns constraints of Extra-Functional registry entry. 
	 */
	public ExtraFuncType getConstraints() {
		return constraints;
	}


	/**
	 * @return Returns allowed value type of Extra-Functional registry entry. 
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 *  Sets allowed value type of Extra-Functional registry entry. 
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	
}
