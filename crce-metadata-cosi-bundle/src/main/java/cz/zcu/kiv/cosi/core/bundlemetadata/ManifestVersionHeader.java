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
 * This class represents manifest's header which consist only from a simple
 * version. This can be for example a Bundle-Version manifest header.
 * 
 * @author Bretislav Wajtr
 */
public class ManifestVersionHeader extends ManifestGenericHeader {
	protected Version version;

	public ManifestVersionHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
		version = new Version(P_headerValue);
	}

	/**
	 * @return Returns parsed version represented by this header.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * A value object of this header is Version class.
	 */
	@Override
	public Object getHeaderValue() {
		return version;
	}

}
