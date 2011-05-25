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
 * This class represents bundle manifest's Consume-Events header.
 * 
 * This class can handle header which are consisted by a entries delimited by a
 * comma (','). Each of these entries is consisted of a value (name of the
 * event), one mandatory attribute (type of the event) and a set of other
 * attributes. Full syntax of the 'consume event' header is following:
 * 
 * Consume-Events ::= import ( ',' import )*<br>
 * import ::= events ';' typeparameter ( ';' parameter )*<br>
 * events ::= event ( ';' event )*<br>
 * type ::= unique-name<br>
 * typeparameter ::= 'type' '=' unique-name<br>
 * 
 * @author Bretislav Wajtr
 */
public class ManifestEventConsumingHeader extends
		ManifestComplexHeader<RequiringNamedTypeHeaderEntry> {

	public ManifestEventConsumingHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
	}

	/**
	 * This is factory method used by a superclass to create instance for a new
	 * entry.
	 * 
	 * @see ManifestComplexHeader#getNewHeaderEntryImpl(String, HashMap)
	 */
	@Override
	protected RequiringNamedTypeHeaderEntry getNewHeaderEntryImpl(String P_value,
			HashMap<String, Object> P_parameters) {
		return new RequiringNamedTypeHeaderEntry(P_value, P_parameters);
	}

	/**
	 * @return Returns list of entries in this manifest header.
	 * 
	 * @see ProvidingNamedTypeHeaderEntry
	 */
	public List<RequiringNamedTypeHeaderEntry> getEntries() {
		return (List<RequiringNamedTypeHeaderEntry>) entries;
	}

	/**
	 * This is a method used by a superclass to obtain list of mandatory
	 * attributes for each entry. For this header there is necessary to specify
	 * type (classtype) of the event.
	 * 
	 * @see ManifestComplexHeader#getMandatoryParameters()
	 */
	@Override
	protected String[] getMandatoryParameters() {
		// type of the attribute must be specified
		return new String[] { ManifestComplexHeader.TYPE_PARAMETER };
	}
}
