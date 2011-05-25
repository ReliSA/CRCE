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
 * This class represents bundle manifest's Require-Type or Require-Interface
 * headers. Since these two headers have same syntax, one class can be used for
 * both headers.
 * 
 * This class can handle headers which are consisted by a entries delimited by a
 * comma (','). Each of these entries is consisted of a value (full qualified
 * name of the type or interface) and a set of attributes, such as versionrange,
 * bundlename and others. Full syntax of the 'requiring type' header is
 * following:
 * 
 * Require-Types ::= import ( ',' import )*<br>
 * import ::= types ( ';' parameter )*<br>
 * types ::= type ( ';' type )*<br>
 * type ::= unique-name<br>
 * 
 * and this syntax is appliable for both Require-Type and Require-Interface
 * header.
 * 
 * @author Bretislav Wajtr
 */
public class ManifestRequiringHeader extends ManifestComplexHeader<RequiringTypeHeaderEntry> {

	public ManifestRequiringHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
	}

	/**
	 * This is factory method used by a superclass to create instance for
	 * a new entry.  
	 * 
	 * @see ManifestComplexHeader#getNewHeaderEntryImpl(String, HashMap)
	 */
	@Override
	protected RequiringTypeHeaderEntry getNewHeaderEntryImpl(String P_value,
			HashMap<String, Object> P_parameters) {
		return new RequiringTypeHeaderEntry(P_value, P_parameters);
	}

	/**
	 * @return Returns list of entries in this manifest.
	 * 
	 * @see RequiringTypeHeaderEntry
	 */
	public List<RequiringTypeHeaderEntry> getEntries() {
		return (List<RequiringTypeHeaderEntry>) entries;
	}

	/**
	 * This is a method used by a superclass to obtain list of mandatory attributes
	 * for each entry. Since there are no mandatory attributes specified for 
	 * entries for required types/interfaces, an empty array is returned.
	 * 
	 * @see ManifestComplexHeader#getMandatoryParameters()
	 */
	@Override
	protected String[] getMandatoryParameters() {
		// no mandatory parameters
		return new String[] {};
	}
}
