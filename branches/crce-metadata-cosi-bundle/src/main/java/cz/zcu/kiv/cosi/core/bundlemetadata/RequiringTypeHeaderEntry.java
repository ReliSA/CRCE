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
 * This class represents one entry in bundle manifest's Require-Type or
 * Require-Interface header. Since there is no difference in a syntax of these
 * two headers, one class can be used for both of them.For example if we would
 * have following header:<br>
 * 
 * Require-Interface: cz.zcu.somepackage.SomeInterface;<br>
 * cz.zcu.someotherpackage.SomeOtherInterface;<br>
 * versionrange="[0,5]",<br>
 * cz.zcu.thirdpackage.ThirdInterface<br>
 * 
 * then there would be three RequiringTypeHeaderEntries:<br>
 * 1. cz.zcu.somepackage.SomeInterface with versionrange [0,5]<br>
 * 2. cz.zcu.someotherpackage.SomeOtherInterface with versionrange [0,5]<br>
 * 3. cz.zcu.thirdpackage.ThirdInterface with default attributes<br>
 * 
 * Each entry holds class-type of the required type/interface and a set of
 * attributes which helps the container to find the correct exporter for the
 * type required.
 * 
 * This class is used by <code>ManifestRequiringHeader</code> to store entries
 * of the header.
 * 
 * @see ManifestRequiringHeader
 * 
 * @author Bretislav Wajtr
 */
public class RequiringTypeHeaderEntry extends HeaderEntry {

	public RequiringTypeHeaderEntry(String P_value,
			HashMap<String, Object> P_parameters) {
		super(P_value, P_parameters);
	}

	/**
	 * @return Returns required version range of the interface/type which is
	 *         imported.
	 */
	public VersionRange getInterfaceVersionRangeAttribute() {
		return (VersionRange) getParameterValue(ManifestComplexHeader.VERSIONRANGE_PARAMETER);
	}

	/**
	 * @return Returns accepted version range of bundle which exports the
	 *         type/interface
	 */
	public VersionRange getBundleVersionRangeAttribute() {
		return (VersionRange) getParameterValue(ManifestComplexHeader.BUNDLE_VERSIONRANGE_PARAMETER);
	}

	/**
	 * @return Returns the accepted name of the bundle which exports the
	 *         type/interface.
	 */
	public String getBundleNameAttribute() {
		return (String) getParameterValue(ManifestComplexHeader.BUNDLENAME_PARAMETER);
	}

	/**
	 * @return Returns the accepted name of the vendor (provider, developer) of
	 *         the bundle which exports the type/interface.
	 */
	public String getBundleProviderAttribute() {
		return (String) getParameterValue(ManifestComplexHeader.BUNDLEPROVIDER_PARAMETER);
	}

	/**
	 * @return Returns required ExtraFunc parameters of the interface/type which
	 *         is imported.
	 */
	public ExtraFunc getBundleExtraFuncAttribute() {
		return (ExtraFunc) getParameterValue(ManifestComplexHeader.EXTRAFUNC_PARAMETER);
	}
	
	public String getNameAttribute() {
		return (String) getParameterValue(ManifestComplexHeader.NAME_PARAMETER);
	}

	public boolean isAutoWire() {
		return ((String)getParameterValue(ManifestComplexHeader.AUTOWIRE_PARAMETER)).equalsIgnoreCase("true");
	}
	
	/**
	 * @return If true, then this required interface/type doesn't have to
	 *         neccessary be inside the contianer. This is good for situation
	 *         where bundle wants to use some service, but if this service is
	 *         not included inside the container, the bundle can still function
	 *         without it (for example a logging service). It is up to bundle to
	 *         deal with the situation when there is no interface/type provided
	 *         by any bundle.<br>
	 *         If false, then this interface/type is really required and bundle
	 *         resolve will fail if there is no exporter of this interface/type
	 *         found in the container.
	 */
	public boolean isOptional() {
		return (Boolean) getParameterValue(ManifestComplexHeader.OPTIONAL_PARAMETER);
	}

}
