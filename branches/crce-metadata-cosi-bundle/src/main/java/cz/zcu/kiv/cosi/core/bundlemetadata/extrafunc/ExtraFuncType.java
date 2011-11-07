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

/**
 * 
 * Abstract Class representing all values in Extra Functional properties. All
 * values are descended from this, so they can provide getValue, getType and
 * fulfil methods.
 * 
 * * @author Vojtech Liska
 * 
 */
public abstract class ExtraFuncType {

	// types
	public static final String BOOLEAN = "boolean";
	public static final String INTEGER = "integer";
	public static final String FLOAT = "float";
	public static final String STRING = "string";
	public static final String TOKEN = "token";
	public static final String ENUM = "enum";
	public static final String MAP = "map";
	public static final String INTERVAL = "interval";
	public static final String UNKNOWN = "unknown";

	/**
	 * @return Extra Functional property value
	 */
	public abstract Object getValue();

	/**
	 * @return Extra Functional property type
	 */
	public abstract String getType();

	/**
	 * Checks if this instance fulfills requirements of P_requiredType instance.
	 * 
	 * @return if this enumeration fulfills P_requiredType requirements
	 */
	public abstract boolean fulfil(ExtraFuncType P_requiredType);

	public String toString() {
		return getValue().toString();
	}

}
