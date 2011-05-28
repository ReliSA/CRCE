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

package cz.zcu.kiv.cosi.core.bundlemetadata;

import java.util.HashMap;

/**
 * This class represents one entry in bundle manifest's Generate-Events or
 * Provide-Attribute headers. Since there is no difference in a syntax of these
 * two headers, one class can be used for both of them. For example if we would
 * have following header:<br>
 * 
 * Provide-Attribute: myattr; type=cz.zcu.somepackage.SomeType,<br>
 * myattr2; type=cz.zcu.otherpackage.OtherType<br>
 * 
 * then there would be two ProvidingNamedTypeHeaderEntries:<br>
 * 1. provided attribute named myattr with type cz.zcu.somepackage.SomeType<br>
 * 2. provided attribute named myattr2 with type cz.zcu.otherpackage.OtherType<br>
 * 
 * Each entry holds name of the attribute/event together with it's class-type
 * and a set of attributes which helps the container to find the correct
 * exporter for the attribute/event required when doing wiring.
 * 
 * This class is used by <code>{@link ManifestAttributeProvidingHeader}</code>
 * and <code>{@link ManifestEventProvidingHeader}</code> to store entries.
 * 
 * @see ManifestAttributeProvidingHeader
 * @see ManifestEventProvidingHeader
 * 
 * @author Bretislav Wajtr
 */
public class ProvidingNamedTypeHeaderEntry extends HeaderEntry {

	public ProvidingNamedTypeHeaderEntry(String P_value, HashMap<String, Object> P_parameters) {
		super(P_value, P_parameters);
	}
	
	/**
	 * @return Name of the attribute/event provided
	 */
	public String getName() {
		return getValue();
	}

	/**
	 * @return Full qualified name of the class-type of this attribute/event.
	 */
	public String getTypeParameter() {
		return (String)getParameterValue(ManifestComplexHeader.TYPE_PARAMETER);
	}
	
	/**
	 * @return Version of the attribute/event provided. Note that this can be different value
	 * than <u>version of the type</u> of the attribute/event.  
	 */
	public Version getVersionParameter() {
		return (Version)getParameterValue(ManifestComplexHeader.VERSION_PARAMETER);
	}

}
