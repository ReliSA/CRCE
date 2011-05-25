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

/**
 * This class can represent a manifest header which is consisted of list of
 * strings delimited by a ';' symbol. Value of this header is then a String
 * array containing delimited and trimmed strings.
 * 
 * This class is used for Bundle-Classpath header for example.
 * 
 * @author Bretislav Wajtr
 */
public class ManifestStringListHeader extends ManifestGenericHeader {

	String[] stringList;

	public ManifestStringListHeader(String Ps_headerName, String Ps_headerValue) {
		super(Ps_headerName, Ps_headerValue);

		// parse list
		stringList = Ps_headerValue.split("\\;");
		for (int i = 0; i < stringList.length; i++) {
			stringList[i] = stringList[i].trim();
		}
	}

	/**
	 * Value object type for this header is String[].
	 */
	@Override
	public Object getHeaderValue() {
		return stringList;
	}
}
