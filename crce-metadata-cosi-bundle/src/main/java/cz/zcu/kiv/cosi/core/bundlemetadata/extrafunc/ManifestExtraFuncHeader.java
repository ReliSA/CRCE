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

package cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc;

import cz.zcu.kiv.cosi.core.bundlemetadata.ManifestGenericHeader;

/**
 * Parses ExtraFunc properties from header value.
 * 
 * @author Vojtech Liska
 */
public class ManifestExtraFuncHeader extends ManifestGenericHeader {

	protected ExtraFunc extrafunc;
	
	public ManifestExtraFuncHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
		extrafunc = new ExtraFunc(P_headerValue);
	}

	public ExtraFunc getExtraFuncProperties() {
		return extrafunc;
	}

	@Override
	public Object getHeaderValue() {
		return extrafunc;
	}
	
	public String toString() {
		return extrafunc.toString();
	}
	

}
