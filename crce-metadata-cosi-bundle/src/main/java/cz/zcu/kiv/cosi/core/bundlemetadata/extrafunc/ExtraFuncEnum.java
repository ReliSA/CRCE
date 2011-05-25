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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class representing enumeration in Extra-functional properties. Values of the
 * enumeration must be of the same type.
 * 
 * @author Vojtech Liska
 * 
 */
public class ExtraFuncEnum extends ExtraFuncType {

	private List<ExtraFuncEnumEntry> value;

	private int counter;

	private String valueType;

	public ExtraFuncEnum(String Ps_valueType) {
		counter = 0;
		this.valueType = Ps_valueType;
		this.value = new ArrayList<ExtraFuncEnumEntry>();
	}

	public ExtraFuncEnum(ArrayList<ExtraFuncEnumEntry> P_value,
			String Ps_valueType) {

		boolean F_valueNotSet = false;

		if (P_value == null || P_value.isEmpty()) {
			F_valueNotSet = true;
		}

		if (F_valueNotSet && Ps_valueType.equals("")) {
			throw new RuntimeException("Error: can't determine enum type!");
		} else {
			if (F_valueNotSet && !Ps_valueType.equals("")) {
				this.valueType = Ps_valueType;
				counter = 0;
				this.value = new ArrayList<ExtraFuncEnumEntry>();
			} else if (!Ps_valueType.equals("")) {
				this.valueType = Ps_valueType;
				this.value = P_value;
				counter = P_value.size();
			} else {
				ExtraFuncEnumEntry F_firstEntry = P_value.get(0);
				this.valueType = F_firstEntry.getValue().getType();
				counter = P_value.size();
				this.value = P_value;
			}
		}
	}

	@Override
	public String getType() {
		return ENUM;
	}

	@Override
	public List<ExtraFuncEnumEntry> getValue() {
		return value;
	}

	public String getValueType() {
		return valueType;
	}

	public void addEntry(ExtraFuncType P_entry) {
		if (P_entry.getType() != valueType)
			throw new RuntimeException(
					"Error adding entry to Enum: expected type " + valueType
							+ " but encountered " + P_entry.getType());
		value.add(new ExtraFuncEnumEntry(P_entry, counter++));
	}

	public boolean contains(ExtraFuncType P_value) {
		for (ExtraFuncEnumEntry F_entry : value) {
			if (F_entry.getValue().equals(P_value)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(ExtraFuncEnumEntry P_value) {
		for (ExtraFuncEnumEntry F_entry : value) {
			if (F_entry.equals(P_value)) {
				return true;
			}
		}
		return false;
	}

	public ExtraFuncEnumEntry get(ExtraFuncEnumEntry P_entry) {
		for (ExtraFuncEnumEntry F_entry : value) {
			if (P_entry.getValue().equals(F_entry.getValue()))
				return F_entry;
		}
		return null;

	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {

		// not matching type (enum<type> or type)
		if (!P_requiredType.getType().equals(ExtraFuncType.ENUM)
				&& !P_requiredType.getType().equals(this.getValueType()))
			return false;

		// enum<type>
		if (P_requiredType.getType().equals(ExtraFuncType.ENUM)) {

			ExtraFuncEnum F_requiredEnum = (ExtraFuncEnum) P_requiredType;

			boolean F_ret = true;

			for (ExtraFuncEnumEntry F_entry : F_requiredEnum.getValue()) {
				if (!this.contains(F_entry)) {
					F_ret = false;
				}
			}
			return F_ret;

		}
		// type
		else {
			if (this.contains(P_requiredType))
				return true;
			else
				return false;
		}

	}

}
