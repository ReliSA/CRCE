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
 * Class representing scalar values in Extra Functional properties: integer,
 * boolean, float, string and ExtraFuncEnumEntry
 * 
 * 
 * @author Vojtech Liska
 * 
 */

public class ExtraFuncSimpleType extends ExtraFuncType implements
		Comparable<ExtraFuncSimpleType> {

	private Object value;

	private String type;

	public ExtraFuncSimpleType(Object P_value) {
		if (P_value instanceof Boolean) {
			type = BOOLEAN;
			value = Boolean.valueOf((Boolean) P_value);
		} else if (P_value instanceof Integer) {
			type = INTEGER;
			value = Integer.valueOf((Integer) P_value);
		} else if (P_value instanceof Double) {
			type = FLOAT;
			value = Double.valueOf((Double) P_value);
		} else if (P_value instanceof String) {
			type = STRING;
			value = String.valueOf((String) P_value);
		} else if (P_value instanceof ExtraFuncEnumEntry) {
			type = ((ExtraFuncType) P_value).getType();
			value = ((ExtraFuncEnumEntry) P_value).clone();
		} else

			throw new RuntimeException("Incorrect ExtraFuncSimpleType value.");
	}

	@SuppressWarnings("unchecked")
	public int compareTo(ExtraFuncSimpleType P_simpleType) {
		return ((Comparable) value).compareTo(P_simpleType.getValue());
	}

	public boolean equals(Object P_simpleType) {
		if (P_simpleType == null
				|| !(P_simpleType instanceof ExtraFuncSimpleType))
			return false;
		else
			return ((ExtraFuncSimpleType) P_simpleType).value.equals(value);
	}

	public int hashCode() {
		if (value instanceof Double)
			return ((Double) value).hashCode();
		if (value instanceof Boolean)
			return ((Boolean) value).hashCode();
		if (value instanceof Integer)
			return ((Integer) value).hashCode();
		if (value instanceof String)
			return ((String) value).hashCode();
		if (value instanceof ExtraFuncEnumEntry)
			return ((ExtraFuncEnumEntry) value).getValue().hashCode();
		;
		return value.hashCode();
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {
		if (P_requiredType.getType().equals(ExtraFuncType.INTERVAL)) {
			ExtraFuncInterval F_interval = (ExtraFuncInterval) P_requiredType;
			return F_interval.fitsWithinRange(this);

		}

		if (!P_requiredType.getType().equals(type))
			return false;
		else {
			return this.equals(P_requiredType);
		}
	}

	public String toString() {
		String F_ret = "";

		if (value instanceof String)
			F_ret = "\"" + value.toString() + "\"";
		else
			F_ret = value.toString();

		return F_ret;
	}

}
