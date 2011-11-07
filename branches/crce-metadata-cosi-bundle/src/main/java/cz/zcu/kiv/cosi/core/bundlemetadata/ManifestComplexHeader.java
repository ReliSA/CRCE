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

import java.util.ArrayList;
import java.util.HashMap;

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;

/**
 * This is a superclass for all headers which have a slightly complicated
 * syntax. To this group belong following headers:
 * 
 * {@link ManifestAttributeProvidingHeader}<br>
 * {@link ManifestAttributeRequiringHeader}<br>
 * {@link ManifestEventConsumingHeader}<br>
 * {@link ManifestEventProvidingHeader}<br>
 * {@link ManifestProvidingHeader}<br>
 * {@link ManifestRequiringHeader}<br>
 * 
 * All these manifest headers share very similar syntax, which can be parsed
 * using one class. This class can handle headers which are consisted by a
 * parts, which we will call 'entries', delimited by a comma (','). Each of
 * these entries is consisted of a 'value' (for example full qualified name of
 * the type/interface or a name of the attribute/event) and a set of parameters,
 * such as version, bundlename, versionrange and others. Detailed syntax can be
 * written down this way:
 * 
 * ComplexHeader ::= entry ( ',' entry )*<br>
 * entry ::= values ( ';' parameter )*<br>
 * values ::= value ( ';' value )*<br>
 * value ::= unique-name<br>
 * parameter ::= parameter-name '=' parameter-value | parameter-name '=' '(' parameter-value ')'<br>
 * parameter-name ::= can be any string <br>
 * parameter-value ::= can be any string, but if contains comma or semicolon, it
 * has to be quoted <br>
 * 
 * This is the abstract class and descendants have to specify class type of the
 * entry and provide new instances of these entries through method
 * {@link ManifestComplexHeader#getNewHeaderEntryImpl(String, HashMap)}.
 * 
 * Parsing of the header can be further customized by specifying mandatory
 * parameters which must be present in header for each value. Descendants do
 * this by implementing abstract method getMandatoryParameters(). If there is a
 * entry in a header, which doesn't include a mandatory parameter, parsing fails.
 * 
 * @param <E>
 *            Type of the entry.
 * 
 * @author Bretislav Wajtr
 */
public abstract class ManifestComplexHeader<E extends HeaderEntry> extends ManifestGenericHeader {

	/**
	 * Predefined parameters of the entries.
	 */
	public static final String BUNDLE_VERSIONRANGE_PARAMETER = "bundle-versionrange";
	public static final String VERSIONRANGE_PARAMETER = "versionrange";
	public static final String VERSION_PARAMETER = "version";
	public static final String BUNDLENAME_PARAMETER = "bundle-name";
	public static final String BUNDLEPROVIDER_PARAMETER = "bundle-provider";
	public static final String TYPE_PARAMETER = "type";
	public static final String OPTIONAL_PARAMETER = "optional";
	public static final String EXTRAFUNC_PARAMETER = "extrafunc";
	public static final String NAME_PARAMETER = "name";
	public static final String AUTOWIRE_PARAMETER = "autowire";
	
	/**
	 * Here are all entries stored. Concrete type is defined by a descendant.
	 */
	protected ArrayList<E> entries = new ArrayList<E>();

	public ManifestComplexHeader(String P_headerName, String P_headerValue) {
		super(P_headerName, P_headerValue);
		parseComplexHeader(P_headerValue);
	}

	private void parseComplexHeader(String P_clause) {
		String clause = P_clause.trim();

		// split string by ',' (not when surrounded by '"')
		ArrayList<String> F_entries = new ArrayList<String>(3);
		StringBuffer buf = new StringBuffer(100);
		boolean canDivide = true;
		for (int i = 0; i < clause.length(); i++) {
			// parameter-name '=' '(' parameter-value ')'
			if (canDivide && clause.charAt(i) == '(') {
				buf.append(clause.charAt(i));
				while ((clause.charAt(++i) != ')') && (i < clause.length())) 
					buf.append(clause.charAt(i));
			}
			if (clause.charAt(i) == '"') {
				canDivide = !canDivide;
				buf.append(clause.charAt(i));
			} else if (clause.charAt(i) == ',') {
				if (canDivide) {
					F_entries.add(buf.toString());
					buf = new StringBuffer(100);
				} else {
					buf.append(clause.charAt(i));
				}
			} else {
				buf.append(clause.charAt(i));
			}
		}
		if (buf.length() > 0) {
			F_entries.add(buf.toString());
		}

		// take each sub-clause and create entries
		for (String F_entry : F_entries) {
			F_entry = F_entry.trim();
			String[] array = F_entry.split(";");

			// make difference between paths and parameters
			// TODO Think of better name of this paths variable - it's for
			// example interface name
			ArrayList<String> F_values = new ArrayList<String>();
			ArrayList<String> F_parameters = new ArrayList<String>(2);

			for (String str : array) {
				if (str.contains("=")) { // it's parameter
					F_parameters.add(str.trim());
				} else {
					F_values.add(str.trim());
				}
			}

			// for each path create entry and fill it with all parameters
			for (String F_value : F_values) {

				// create parameter hashmap
				HashMap<String, Object> F_parsedParams = new HashMap<String, Object>();

				// fill it with default values
				fillWithDefaultParams(F_parsedParams);

				for (String F_parameter : F_parameters) {
					// check regex validity of parameter
					F_parameter = F_parameter.trim();
					String[] F_dividedParam = F_parameter.split("=",2);
					String Fs_paramName = F_dividedParam[0].trim();
					String Fs_paramStringValue = F_dividedParam[1].trim();
					Object F_paramObjectValue = returnObjectForParameter(Fs_paramName,
							Fs_paramStringValue);
					F_parsedParams.put(Fs_paramName, F_paramObjectValue);
				}

				// check if parameters contain all mandatory parameters
				String[] F_mandatoryParams = getMandatoryParameters();
				for (String F_mandatoryParam : F_mandatoryParams) {
					if (F_parsedParams.get(F_mandatoryParam) == null) {
						throw new RuntimeException("Mandatory paramter " + F_mandatoryParam
								+ " for header " + headerName + " is missing!");
					}
				}

				E newEntry = (E) getNewHeaderEntryImpl(F_value, F_parsedParams);
				entries.add(newEntry);
			}

		}
	}

	/**
	 * Creates a default set of parameters for each entry. If entry doesn't
	 * specify any parameters, only a default values are used for entry.
	 * 
	 * @param P_map
	 *            A map of parameters to be filled.
	 */
	private void fillWithDefaultParams(HashMap<String, Object> P_map) {
		P_map
				.put(VERSIONRANGE_PARAMETER, returnObjectForParameter(VERSIONRANGE_PARAMETER,
						"0.0.0"));
		P_map.put(BUNDLE_VERSIONRANGE_PARAMETER, returnObjectForParameter(
				BUNDLE_VERSIONRANGE_PARAMETER, "0.0.0"));
		P_map.put(VERSION_PARAMETER, returnObjectForParameter(VERSION_PARAMETER, "0.0.0"));
		P_map.put(NAME_PARAMETER, returnObjectForParameter(NAME_PARAMETER, ""));
		P_map.put(OPTIONAL_PARAMETER, returnObjectForParameter(OPTIONAL_PARAMETER, "false"));
		P_map.put(EXTRAFUNC_PARAMETER, returnObjectForParameter(EXTRAFUNC_PARAMETER, ""));
		P_map.put(AUTOWIRE_PARAMETER, returnObjectForParameter(AUTOWIRE_PARAMETER, "false"));
	}

	private Object returnObjectForParameter(String P_parameter, String P_value) {
		String value = P_value;

		// chop off leading and tailing '"' if present
		if (value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
		if (P_parameter.equalsIgnoreCase(VERSION_PARAMETER)) {
			return new Version(value);
		}
		if (P_parameter.equalsIgnoreCase(VERSIONRANGE_PARAMETER)) {
			return new VersionRange(value);
		}
		if (P_parameter.equalsIgnoreCase(BUNDLE_VERSIONRANGE_PARAMETER)) {
			return new VersionRange(value);
		}
		if (P_parameter.equalsIgnoreCase(OPTIONAL_PARAMETER)) {
			return Boolean.valueOf(value);
		}
		if (P_parameter.equalsIgnoreCase(EXTRAFUNC_PARAMETER)) {
			return new ExtraFunc(value);
		}
		if (P_parameter.equalsIgnoreCase(NAME_PARAMETER)) {
			return String.valueOf(value);
		}
		if (P_parameter.equalsIgnoreCase(AUTOWIRE_PARAMETER)) {
			return String.valueOf(value);
		}
		
		return P_value;
	}

	/**
	 * Value of this header is list of entries. Type of the entry is specified
	 * by a descendant.
	 */
	@Override
	public Object getHeaderValue() {
		return entries;
	}

	/**
	 * Parsing of the header can be further customized by specifying mandatory
	 * parameters which must be present in header for each value. Descendants do
	 * this by implementing abstract method getMandatoryParameters(). If there
	 * is a entry in a header, which doesn't include a mandatory parameter,
	 * parsing fails.
	 * 
	 * @return Return String array of parameter names, which must be included in
	 *         each entry.
	 */
	protected abstract String[] getMandatoryParameters();

	/**
	 * Returns implementation of an entry. List of entries is stored in this
	 * class and the type of the entry is generic and so fully customized by a
	 * descendant.
	 * 
	 * @param P_value
	 *            Value of the entry as described in documentation for this
	 *            class.
	 * @param P_parameters
	 *            Set of parameters for one concrete entry.
	 * @return Should return at least a holder for above two parameters.
	 */
	protected abstract E getNewHeaderEntryImpl(String P_value, HashMap<String, Object> P_parameters);

}
