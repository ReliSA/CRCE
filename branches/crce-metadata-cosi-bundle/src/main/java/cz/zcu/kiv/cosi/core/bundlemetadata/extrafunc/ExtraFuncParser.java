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
import java.util.HashMap;
import java.util.Set; 

import cz.zcu.kiv.cosi.container.bundles.extrafuncregistryservice.impl.ExtraFuncRegistryEntry;

/**
 * Parses Extra Functional properties.
 * 
 * @author Vojtech Liska
 * 
 */
public class ExtraFuncParser {

	/**
	 * Parses Extra Functional registry from registry lines.
	 */
	public static HashMap<ScopedToken, ExtraFuncRegistryEntry> parseRegistry(
			HashMap<String, String> P_registryLines) {
		HashMap<ScopedToken, ExtraFuncRegistryEntry> registry = new HashMap<ScopedToken, ExtraFuncRegistryEntry>();
		Set<String> keys = P_registryLines.keySet();
		for (String key : keys) {
			String entryValue = P_registryLines.get(key);
			ExtraFuncRegistryEntry parsedEntry = parseEntry(entryValue);
			if (parsedEntry != null)
				registry.put(new ScopedToken(key), parsedEntry);
		}
		return registry;
	}

	/**
	 * Parses extra func
	 * 
	 * @param Ps_parameter
	 * @return parsed extra func.
	 */
	public static HashMap<ScopedToken, ExtraFuncType> parseExtraFunc(
			String Ps_parameter) {
		HashMap<ScopedToken, ExtraFuncType> attributes = new HashMap<ScopedToken, ExtraFuncType>();

		// 1) strip "(" ")"
		String parameter = Ps_parameter.trim();
		if (parameter.startsWith("(") && parameter.endsWith(")"))
			parameter = parameter.substring(1, parameter.length() - 1);

		// 2) split by properties ","
		ArrayList<String> F_attributes = new ArrayList<String>();

		StringBuffer buf = new StringBuffer();

		boolean canDivide = true;
		boolean insideCurlyBrackets = false;
		boolean insideChevrons = false;
		boolean insideQuotes = false;

		for (int i = 0; i < parameter.length(); i++) {

			if (parameter.charAt(i) == '(' || parameter.charAt(i) == '['
					|| parameter.charAt(i) == ')' || parameter.charAt(i) == ']') {
				if (!insideQuotes) {
					insideChevrons = !insideChevrons;
				}
			}

			if (parameter.charAt(i) == '{' || parameter.charAt(i) == '}') {
				if (!insideQuotes) {
					insideCurlyBrackets = !insideCurlyBrackets;
				}
			}

			if (parameter.charAt(i) == '"') {
				insideQuotes = !insideQuotes;
			}

			canDivide = !insideQuotes && !insideChevrons
					&& !insideCurlyBrackets;

			if (parameter.charAt(i) == ',' && canDivide) {
				F_attributes.add(buf.toString());
				buf = new StringBuffer();
				continue;
			}

			buf.append(parameter.charAt(i));

		}

		if (buf.length() > 0)
			F_attributes.add(buf.toString());

		ArrayList<ScopedToken> F_names = new ArrayList<ScopedToken>();
		ArrayList<ExtraFuncType> F_values = new ArrayList<ExtraFuncType>();

		buf = new StringBuffer();
		for (String F_attribute : F_attributes) {
			// split to name and value

			String F_tokenName = "";
			String F_value = "";
			String F_type = "";
			String F_operator = "";

			for (int i = 0; i < F_attribute.length(); i++) {

				if (F_attribute.charAt(i) == '<') {
					String substr = F_attribute.substring(i);
					if (substr.indexOf('>') != -1) {
						F_type = substr.substring(1, substr.indexOf('>'))
								.trim();
						if (F_type.matches("[\\w]+")) {
							// OK - we have typed parameter
							F_tokenName = F_attribute.substring(0, i).trim();
							F_value = F_attribute.substring(
									F_attribute.indexOf('>') + 1).trim();
							break;
						} else {
							F_tokenName = F_attribute.substring(0, i);
							F_value = F_attribute.substring(i, F_attribute
									.length());
							break;
						}
					} else {
						F_tokenName = F_attribute.substring(0, i);
						F_value = F_attribute
								.substring(i, F_attribute.length());
						break;
					}
				}

				if (F_attribute.charAt(i) == '>') {
					F_tokenName = F_attribute.substring(0, i);
					F_value = F_attribute.substring(i, F_attribute.length());
					break;
				}

				if (F_attribute.charAt(i) == '=') {
					String[] array = F_attribute.split("=", 2);
					F_tokenName = array[0].trim();
					F_value = "=" + array[1].trim();
					break;
				}

			}

			// >=, <=, =
			if (F_value.indexOf('=') == 0 || F_value.indexOf('=') == 1) {
				F_operator = F_value.substring(0, F_value.indexOf('=') + 1)
						.trim();
				F_value = F_value.substring(F_value.indexOf('=') + 1,
						F_value.length()).trim();
			} else { // >, <
				F_operator = F_value.substring(0, 1).trim();
				F_value = F_value.substring(1, F_value.length()).trim();
			}

			// if F_operator is >=, <=, >, < we create interval from value
			if (F_operator.compareTo("=") != 0) {
				if (F_operator.compareTo("<=") == 0) {
					F_value = "(," + F_value + "]";
				} else if (F_operator.compareTo(">=") == 0) {
					F_value = "[" + F_value + ",)";
				} else if (F_operator.compareTo("<") == 0) {
					F_value = "(," + F_value + ")";
				} else if (F_operator.compareTo(">") == 0) {
					F_value = "(" + F_value + ",)";
				}
			}

			if (F_tokenName.contains(".")) {
				// scoped token
				F_names.add(new ScopedToken(F_tokenName));
			} else
				F_names.add(new Token(F_tokenName));

			F_values.add(parseValue(F_value, F_type));

			// System.out.println(F_tokenName + " = " + F_value + " " + F_type);

		}

		if (F_names.size() != F_values.size())
			throw new RuntimeException("Error parsing extrafunc parameters.");
		for (int i = 0; i < F_names.size(); i++) {
			attributes.put(F_names.get(i), F_values.get(i));
		}

		return attributes;
	}

	private static ExtraFuncType parseValue(String Ps_value, String Ps_type) {
		if (Ps_value.startsWith("{") || Ps_value.startsWith("[")
				|| Ps_value.startsWith("("))
			return parseComplexValue(Ps_value, Ps_type);
		else
			return parseScalarValue(Ps_value);
	}

	private static ExtraFuncType parseComplexValue(String Ps_value,
			String Ps_type) {
		// interval

		if (Ps_value.startsWith("(") || Ps_value.startsWith("["))
			return parseInterval(Ps_value, Ps_type);

		// map or enum
		if (Ps_value.contains(":")) {
			if (Ps_value.contains("\"")) {
				if (Ps_value.indexOf(":") < Ps_value.indexOf("\"")) {
					return parseMap(Ps_value);
				}
			} else {
				return parseMap(Ps_value);
			}
		}

		return parseEnum(Ps_value, Ps_type);
	}

	protected static ExtraFuncType parseEnum(String Ps_value, String Ps_type) {
		// strip {,}
		if (!Ps_value.endsWith("}"))
			throw new RuntimeException(
					"Incorrect extrafunc format: enum must end with '}'");

		String Fs_value = Ps_value.substring(1, Ps_value.length() - 1);

		boolean canDivide = true;

		ArrayList<ExtraFuncEnumEntry> F_enum = new ArrayList<ExtraFuncEnumEntry>();

		StringBuffer buf = new StringBuffer();

		int F_enumEntryCount = 0;

		for (int i = 0; i < Fs_value.length(); i++) {
			if (Fs_value.charAt(i) == '\"') {
				canDivide = !canDivide;
				buf.append(Fs_value.charAt(i));
			} else if (Fs_value.charAt(i) == ',') {
				if (canDivide) {
					F_enum.add(new ExtraFuncEnumEntry(parseScalarValue(buf
							.toString().trim()), F_enumEntryCount++));
					buf = new StringBuffer();
				} else {
					buf.append(Fs_value.charAt(i));
				}
			} else {
				buf.append(Fs_value.charAt(i));
			}
		}

		if (buf.length() > 0)
			F_enum.add(new ExtraFuncEnumEntry(parseScalarValue(buf.toString()
					.trim()), F_enumEntryCount++));

		return new ExtraFuncEnum(F_enum, Ps_type);
	}

	protected static ExtraFuncType parseMap(String Ps_value) {
		// strip {,}

		if (!Ps_value.endsWith("}"))
			throw new RuntimeException(
					"Incorrect extrafunc format: map must end with '}'");

		String Fs_value = Ps_value.substring(1, Ps_value.length() - 1);

		HashMap<Token, ExtraFuncType> F_map = new HashMap<Token, ExtraFuncType>();

		ArrayList<String> F_list = new ArrayList<String>();

		boolean canDivide = true;

		StringBuffer buf = new StringBuffer();

		// split by ','
		for (int i = 0; i < Fs_value.length(); i++) {
			if (Fs_value.charAt(i) == '\"') {
				canDivide = !canDivide;
				buf.append(Fs_value.charAt(i));
			} else if (Fs_value.charAt(i) == ',') {
				if (canDivide) {
					F_list.add(buf.toString().trim());
					buf = new StringBuffer();
				} else {
					buf.append(Fs_value.charAt(i));
				}
			} else {
				buf.append(Fs_value.charAt(i));
			}
		}
		if (buf.length() > 0)
			F_list.add(buf.toString().trim());

		// split by ':', add to collection
		for (String Fs_entry : F_list) {
			String[] array = Fs_entry.split("\\:", 2);
			F_map.put(new Token(array[0]), parseScalarValue(array[1]));
		}

		return new ExtraFuncMap(F_map);
	}

	// boolean, integer, float, string, token
	protected static ExtraFuncType parseScalarValue(String Ps_value) {
		if (Ps_value.startsWith("\"")) {
			String Fs_value = String.valueOf(Ps_value.substring(1, Ps_value
					.length() - 1));
			return new ExtraFuncSimpleType(Fs_value);
		}

		if (Ps_value.equalsIgnoreCase("true") || Ps_value.equalsIgnoreCase("Y"))
			return new ExtraFuncSimpleType(true);

		if (Ps_value.equalsIgnoreCase("false")
				|| Ps_value.equalsIgnoreCase("N"))
			return new ExtraFuncSimpleType(false);

		Integer Fi_value;

		try {
			Fi_value = Integer.parseInt(Ps_value);
			return new ExtraFuncSimpleType(Fi_value);
		} catch (NumberFormatException e) {
			// not integer
		}

		Double Ff_value;
		try {
			Ff_value = Double.parseDouble(Ps_value);
			return new ExtraFuncSimpleType(Ff_value);
		} catch (NumberFormatException e1) {
			// not float
		}

		Token token;
		token = new Token(Ps_value);

		return token;
	}

	protected static ExtraFuncInterval parseInterval(String Ps_value,
			String Ps_type) {
		if (!Ps_value.endsWith(")") && !Ps_value.endsWith("]"))
			throw new RuntimeException(
					"Incorrect extrafunc format: interval must end with ')' or ']'");

		if (!Ps_value.startsWith("(") && !Ps_value.startsWith("["))
			throw new RuntimeException(
					"Incorrect extrafunc format: interval must start with '(' or '['");

		// save information about included floor/ceiling
		boolean includeFloor = Ps_value.startsWith("[");
		boolean includeCeiling = Ps_value.endsWith("]");

		// strip [ ], ( )
		String Fs_value = Ps_value.substring(1, Ps_value.length() - 1);

		boolean canDivide = true;
		boolean lowerOrEqual = false;
		boolean higherOrEqual = false;

		StringBuffer buf = new StringBuffer();

		ArrayList<ExtraFuncType> F_list = new ArrayList<ExtraFuncType>();

		for (int i = 0; i < Fs_value.length(); i++) {
			if (Fs_value.charAt(i) == '\"') {
				canDivide = !canDivide;
				buf.append(Fs_value.charAt(i));
			} else if (Fs_value.charAt(i) == ',') {
				if (canDivide) {
					if (buf.length() > 0) {
						F_list.add(parseScalarValue(buf.toString()));
						buf = new StringBuffer();
					} else {
						// we have interval with <,X> -> save that information
						lowerOrEqual = true;
					}
				} else {
					buf.append(Fs_value.charAt(i));
				}
			} else
				buf.append(Fs_value.charAt(i));
		}

		if (buf.length() > 0) {
			F_list.add(parseScalarValue(buf.toString()));
		} else {
			// we have only one value - it's higher or equal
			if (F_list.size() == 1)
				higherOrEqual = true;
		}

		ArrayList<ExtraFuncSimpleType> F_simpleList = new ArrayList<ExtraFuncSimpleType>();
		// convert scalar to simpletype
		for (ExtraFuncType F_scalar : F_list) {
			ExtraFuncSimpleType F_simpleType;
			if (F_scalar.getType() == ExtraFuncType.TOKEN) {
				F_simpleType = new ExtraFuncSimpleType(new ExtraFuncEnumEntry(
						F_scalar));
			} else
				F_simpleType = new ExtraFuncSimpleType(F_scalar.getValue());
			F_simpleList.add(F_simpleType);
		}

		if (lowerOrEqual) {
			return new ExtraFuncInterval(null, includeFloor, F_simpleList
					.get(0), includeCeiling, Ps_type);
		}

		if (higherOrEqual) {
			return new ExtraFuncInterval(F_simpleList.get(0), includeFloor,
					null, includeCeiling, Ps_type);
		}

		if (F_list.size() == 2) {
			return new ExtraFuncInterval(F_simpleList.get(0), includeFloor,
					F_simpleList.get(1), includeCeiling, Ps_type);
		} else if (F_list.size() == 3) {
			return new ExtraFuncInterval(F_simpleList.get(0), includeFloor,
					F_simpleList.get(1), F_simpleList.get(2), includeCeiling,
					Ps_type);
		} else {
			throw new RuntimeException(
					"Incorrect extrafunc format: interval must consist of 2 or 3 values.");
		}
	}

	/**
	 * @param Ps_entryValue
	 * @return
	 */
	private static ExtraFuncRegistryEntry parseEntry(String Ps_entryValue) {
		String[] array = Ps_entryValue.split(";", 2);
		ExtraFuncRegistryEntry F_retval = null;
		String type = "";
		String valueType = "";

		int position = array[0].indexOf('<');
		if (position != -1) {
			type = array[0].substring(0, position);
			// value without < >
			valueType = array[0].substring(position + 1, array[0].length() - 1);
		} else {
			type = array[0];
		}

		if (array[1].length() == 0) {
			// type = array[0];
			if (valueType != null && valueType.length() > 0) {
				F_retval = new ExtraFuncRegistryEntry(type, valueType, null);
			} else {
				F_retval = new ExtraFuncRegistryEntry(type, null);
			}
			return F_retval;
		} else {
			// type = array[0];
			F_retval = new ExtraFuncRegistryEntry(type, valueType,
					parseComplexValue(array[1], valueType));
			return F_retval;
		}
	}

	public static void main(String[] args) {
		// parseExtraFunc("reliability<string>>=\"RELIABLE\"");
		// parseExtraFunc("kojot<vilda>=(mamlas,posuk]");
		parseExtraFunc("kojot<  integer  >   >   6");
		parseExtraFunc("kojot = 6");
		parseExtraFunc("kojot < 6");
		parseExtraFunc("kojot <test> <= 6");
		parseExtraFunc("kojot = \"= >6\"");
	}

}
