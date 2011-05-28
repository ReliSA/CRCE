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
 * Abstraction of all headers in manifest.mf file. Every header is 
 * cosisted of a name and some value which differs in syntax.
 * Descendants of this class provide a way how to parse header values and
 * offers them in better format than just String.  
 */
public class ManifestGenericHeader {
	protected String headerName;
	protected String headerSimpleValue;
	
	public ManifestGenericHeader(String P_headerName, String P_headerValue) {
		headerName = P_headerName;
		headerSimpleValue = P_headerValue;
	}

	/**
	 * @return Returns name of the header, for example 'Bundle-Classpath'.
	 */
	public String getHeaderName() {
		return headerName;
	}

	/**
	 * @return Returns header value in a form of simple string.
	 */
	public String getHeaderStringValue() {
		return headerSimpleValue;
	}
	
	/**
	 * Descendants should override this method to retrun specific object, which
	 * represents parsed header value. 
	 * @return object that represents parsed header value.
	 */
	public Object getHeaderValue() {
		return headerSimpleValue;
	}
	
}
