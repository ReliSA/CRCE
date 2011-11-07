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
import java.util.List;

/**
 * This class represents bundle manifest's Require-Attribute header.
 * 
 * This class can handle header which are consisted by a entries delimited by a
 * comma (','). Each of these entries is consisted of a value (name of the
 * Attribute), one mandatory header attribute (type of the attribute) and a set
 * of other header attributes. Full syntax of the 'require attribute' header is
 * following:
 * 
 * Require-Attribute ::= import ( ',' import )*<br>
 * import ::= attributes ';' typeparameter ( ';' parameter )*<br>
 * attributes ::= attribute ( ';' attribute )*<br>
 * type ::= unique-name<br>
 * typeparameter ::= 'type' '=' unique-name<br>
 * 
 * @author Bretislav Wajtr
 */
public class ManifestAttributeRequiringHeader extends
		ManifestComplexHeader<RequiringNamedTypeHeaderEntry> {

	public ManifestAttributeRequiringHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
	}

	@Override
	protected RequiringNamedTypeHeaderEntry getNewHeaderEntryImpl(String P_value,
			HashMap<String, Object> P_parameters) {
		return new RequiringNamedTypeHeaderEntry(P_value, P_parameters);
	}

	public List<RequiringNamedTypeHeaderEntry> getEntries() {
		return (List<RequiringNamedTypeHeaderEntry>) entries;
	}

	@Override
	protected String[] getMandatoryParameters() {
		// type of the attribute must be specified
		return new String[] { ManifestComplexHeader.TYPE_PARAMETER };
	}
}
