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

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;

/**
 * This class represents one entry in bundle manifest's Provide-Type or
 * Provide-Interface header. Since there is no difference in a syntax of these
 * two headers, one class can be used for both of them. For example if we would
 * have following header:<br>
 * 
 * Provide-Interface: cz.zcu.somepackage.SomeInterface;<br>
 * cz.zcu.someotherpackage.SomeOtherInterface;<br>
 * version=1.2.1,<br>
 * cz.zcu.thirdpackage.ThirdInterface<br>
 * 
 * then there would be three ProvidingTypeHeaderEntries:<br>
 * 1. cz.zcu.somepackage.SomeInterface with version 1.2.1 and other attributes
 * default<br>
 * 2. cz.zcu.someotherpackage.SomeOtherInterface with version 1.2.1 and other
 * attributes default<br>
 * 3. cz.zcu.thirdpackage.ThirdInterface with default attributes<br>
 * 
 * Each entry holds class-type of the provided type/interface and a set of
 * attributes which helps the container to further identify this exporter. This is 
 * useful when wiring exporters with importers.
 * 
 * This class is used by <code>ManifestProvidingHeader</code> to store entries
 * of the header.
 * 
 * @see ManifestProvidingHeader
 * 
 * @author Bretislav Wajtr, Vojtech Liska
 */
public class ProvidingTypeHeaderEntry extends HeaderEntry {

	public ProvidingTypeHeaderEntry(String P_value, HashMap<String, Object> P_parameters) {
		super(P_value, P_parameters);
	}

	/**
	 * @return Version of the type/interface provided.
	 */
	public Version getVersionAttribute() {
		return (Version) getParameterValue(ManifestComplexHeader.VERSION_PARAMETER);
	}
	
	/**
	 * @return Name of the service provided (if present).
	 */
	public String getNameAttribute() {
		return (String) getParameterValue(ManifestComplexHeader.NAME_PARAMETER);
	}
	
	/**
	 * @return ExtraFunc attributes of the type/interface provided.
	 */
	public ExtraFunc getExtraFuncAttribute() {
		return (ExtraFunc) getParameterValue(ManifestComplexHeader.EXTRAFUNC_PARAMETER);
	}

}
