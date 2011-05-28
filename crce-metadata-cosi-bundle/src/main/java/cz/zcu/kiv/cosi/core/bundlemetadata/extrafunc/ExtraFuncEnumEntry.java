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
 * Class representing one entry in enumeration.
 * 
 * Entry consists of
 * 1) position in enumeration
 * 2) value
 * 
 * Position in enumeration is used to compare two enumeration values (value1 < value2 <=> position1 < position2)
 * 
 * @author Vojtech Liska
 *
 */

public class ExtraFuncEnumEntry extends ExtraFuncType implements Comparable<ExtraFuncEnumEntry> {

	// value
	private ExtraFuncType value;
	
	// position in enumertaion
	private int position;
	
	public ExtraFuncEnumEntry(ExtraFuncType P_value) {
		this.value = P_value;
		position = 0;
	}

	public ExtraFuncEnumEntry(ExtraFuncType P_value, int Pi_position) {
		this.value = P_value;
		this.position = Pi_position;
	}
	
	public int getPosition() {
		return position;
	}
	
	
	public int compareTo(ExtraFuncEnumEntry P_entry) {
		return this.position - P_entry.getPosition();
	}
	
	public boolean equals(Object P_object) {
		if (P_object == null)
			return false;
		
		if (P_object instanceof ExtraFuncEnumEntry) {
			ExtraFuncEnumEntry F_enumEntry = (ExtraFuncEnumEntry) P_object;
			if (value.equals(F_enumEntry.getValue()))
				return true;
			else
				return false;
		}
		else
			return false;
	}

	public int hashCode() {
		return value.hashCode();
	}

	public void setPosition(int Pi_position) {
		this.position = Pi_position;
	}

	@Override
	public String getType() {
		return value.getType();
	}

	@Override
	public ExtraFuncType getValue() {
		return value;
	}
	
	public ExtraFuncEnumEntry clone() {
		ExtraFuncEnumEntry retval = new ExtraFuncEnumEntry(this.value,this.position);
		return retval;
	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {
		if (!P_requiredType.getType().equals(value.getType())) {
			return false;
		}
		else
			return true;
	}
	
}
