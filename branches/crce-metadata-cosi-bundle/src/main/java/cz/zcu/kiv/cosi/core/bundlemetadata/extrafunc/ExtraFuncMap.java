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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * Class representing map in Extra Functional properties.
 * 
 * 
 * Map consists of multiple pairs <code>identifier: value</code><br>
 * Identifier is {@link Token}, value is {@link ExtraFuncType}. Values don't
 * have to be the same type.
 * 
 * @author Vojtech Liska
 * 
 */

public class ExtraFuncMap extends ExtraFuncType {

	private java.util.Map<Token, ExtraFuncType> value;

	public ExtraFuncMap() {
		this.value = new HashMap<Token, ExtraFuncType>();
	}

	public ExtraFuncMap(java.util.Map<Token, ExtraFuncType> P_value) {
		if (P_value == null)
			value = new HashMap<Token, ExtraFuncType>();
		else
			this.value = P_value;
	}

	@Override
	public String getType() {
		return MAP;
	}

	@Override
	public java.util.Map<Token, ExtraFuncType> getValue() {
		return value;
	}

	public boolean containsValue(Token P_key, ExtraFuncType P_value) {
		ExtraFuncType F_mapvalue = value.get(P_key);

		if (F_mapvalue != null) {
			if (F_mapvalue.equals(P_value)) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {
		if (P_requiredType.getType() != ExtraFuncType.MAP)
			return false;

		Map<Token, ExtraFuncType> F_requiredMap = ((ExtraFuncMap) P_requiredType)
				.getValue();

		boolean F_ret = true;

		Iterator<Map.Entry<Token, ExtraFuncType>> requiredEntrySetIterator = F_requiredMap
				.entrySet().iterator();
		for (; requiredEntrySetIterator.hasNext();) {
			Map.Entry<Token, ExtraFuncType> F_requiredEntry = requiredEntrySetIterator
					.next();
			Token F_key = F_requiredEntry.getKey();

			ExtraFuncType F_providedValue = value.get(F_key);

			if (F_providedValue != null) {
				ExtraFuncType F_requiredValue = F_requiredEntry.getValue();
				if (!F_providedValue.fulfil(F_requiredValue))
					F_ret = false;
			} else {
				F_ret = false;
			}
		}

		return F_ret;
	}

	public String toString() {
		String F_ret = "{";
		int i = 0;
		for (Map.Entry<Token, ExtraFuncType> F_entrySet : value.entrySet()) {
			F_ret += F_entrySet.getKey().toString() + ":"
					+ F_entrySet.getValue().toString();
			if (++i < value.size())
				F_ret += ",";
		}
		F_ret += "}";

		return F_ret;
	}

}
