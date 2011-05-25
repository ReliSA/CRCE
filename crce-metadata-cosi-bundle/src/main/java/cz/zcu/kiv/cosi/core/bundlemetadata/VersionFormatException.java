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
 * Exception which is thrown when parsing Version object from string. If version
 * string doesn't follow right syntax, this Exception is thrown.
 * 
 * @author Bretislav Wajtr
 * 
 */
public class VersionFormatException extends IllegalArgumentException {
	private static final long serialVersionUID = -5253035958840510275L;

	public VersionFormatException(String P_message) {
		super(P_message);
	}
}
