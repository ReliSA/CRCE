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
import java.util.Set;

/**
 * This is superclass for entries in complex headers (which are all headers
 * which begin with Require-* or Provide-*). These headers have more complicated
 * structure - they consist of entries delimited by a comma. For each entry type
 * there is a special class representing it. Because all entry types (we mean,
 * for example, that one entry type is in Require-Interface header, another
 * entry type is in Provide-Attribute header etc.) have in common, that they are
 * consisted of a 'value' (full qualified class name, attribute name or event
 * name) and some set of parameters assigned to this value, there is HeaderEntry
 * class which represent this high level view of the entry.
 * 
 * @see ProvidingNamedTypeHeaderEntry
 * @see ProvidingTypeHeaderEntry
 * @see RequiringTypeHeaderEntry
 * @see RequiringNamedTypeHeaderEntry
 * 
 * @author Bretislav Wajtr
 */
public class HeaderEntry {
	private String value;
	private HashMap<String, Object> parameters;

	public HeaderEntry(String P_value, HashMap<String, Object> P_parameters) {
		value = P_value;
		parameters = P_parameters;
	}

	/**
	 * @return Returns 'value' of the entry. In *-Interfaces, *-Types,
	 *         *-Packages headers it is a full qualified class name. In
	 *         *-Attributes it is a name of the attribute and in *-Events it is
	 *         the name of the event. By an asterisk ('*') we mean 'Require' or
	 *         'Provide'.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return Returns parameters map assigned to a value represented by this
	 *         entry.
	 */
	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * @return Returns value of the specified parameter assigned to this entry.
	 *         Note that returning type doesn't have to necessary be a String.
	 *         For example if you are asking for an 'bundleversion' parameter,
	 *         then returning type would be Version.
	 * @param P_parameterName
	 */
	public Object getParameterValue(String P_parameterName) {
		return parameters.get(P_parameterName);
	}

	@Override
	public String toString() {
		String Fs_ret = "Value: " + value + "\n";
		Fs_ret += "Parameters: \n";
		Set<String> F_keys = parameters.keySet();
		for (String string : F_keys) {
			Fs_ret += "Key: " + string + " Value: " + parameters.get(string)
					+ " \n";
		}
		return Fs_ret;
	}

}
