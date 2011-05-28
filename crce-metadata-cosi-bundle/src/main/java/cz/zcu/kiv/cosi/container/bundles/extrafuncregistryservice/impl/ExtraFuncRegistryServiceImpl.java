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

package cz.zcu.kiv.cosi.container.bundles.extrafuncregistryservice.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncEnum;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncEnumEntry;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncInterval;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncMap;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncParser;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncSimpleType;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFuncType;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ScopedToken;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.Token;
import cz.zcu.kiv.cosi.core.extrafuncregistryservice.ExtrafuncRegistryService;

/**
 * Implementation of <code>cz.zcu.kiv.cosi.core.extrafuncregistryservice.ExtraFuncRegistryService</code>.
 * 
 * Registry consist of entries. Entry consists of:<BR>
 * 
 * property-name: property-type; property-constraints<BR>
 * 
 * property-name is ExtraFunc property qualified name (Token or Scoped-Token type)<BR>
 * 
 * property-type is desired property type (for ex. integer, string, etc.) - see {@link ExtraFuncType}<BR>  
 * 
 * property-constraints are constraints on particular property-type
 * 
 * constraints can be for:<br>
 * 
 * <table>
 * <tr>
 * <th>Type</th><th>Allowed constraint type</th>
 * <tr>
 * <td>integer, string, float</td><td>interval, enum</td>
 * </tr>
 * <tr>
 * <td>enum</td><td>enum</td>
 * </tr>
 * <tr>
 * <td>interval</td><td>enum of tokens</td>
 * </tr>
 * <tr>
 * <td>map</td><td>enum of allowed identifier names</td>
 * </tr>
 * <tr>
 * <td>token</td><td>enum of allowed token values</td>
 * </tr>
 * 
 * </table>
 * <br>
 * Token and Map needs not-null constraints (for identifier/token names).
 *  
 * @author Vojtech Liska
 */

public class ExtraFuncRegistryServiceImpl implements ExtrafuncRegistryService {

	/*
	 * Extrafunc registry.
	 */
	private HashMap<ScopedToken, ExtraFuncRegistryEntry> registry;
	/*
	 * File lines.
	 */
	private HashMap<String, String> registryLines;

	public ExtraFuncRegistryServiceImpl() throws Exception {
		loadRegistry();
		registry = ExtraFuncParser.parseRegistry(registryLines);
		checkRegistry();
	}

	/**
	 * Loads Extra Functional registry from P_url.
	 */
	public void loadRegistryFromURL(URL P_url) throws IOException {
		_fillRegistryLines(P_url.openStream());
	}
	
	/**
	 * Checks, if P_lshExtraFunc provides all <code>ExtraFunc</code> properties required by
	 * P_rhsExtraFunc.
	 * 
	 * @param P_lhsExtraFunc ExtraFunc properties required by bundle
	 * @param P_rhsExtraFunc ExtraFunc properties provided by bundle
	 * @return <code>true</code> if P_providedExtraFunc provides all <code>ExtraFunc</code> properties required
	 * by P_requiredExtraFunc
	 * 
	 * We want lhs := rhs  =>  lhs \subsetOrEqual rhs
	 * 
	 * @see ExtrafuncRegistryService#checkExtraFuncMatch(cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc, cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc) 
	 * 
	 */
	public boolean checkExtraFuncMatch(ExtraFunc P_lhsExtraFunc, ExtraFunc P_rhsExtraFunc) {
		// no required attributes - nothing to check
		if (P_lhsExtraFunc.isEmpty()) 
			return true;
		
		// no provided attributes but we have required - error
		if (P_rhsExtraFunc.isEmpty() && !P_lhsExtraFunc.isEmpty())
			return false;
		
		HashMap<ScopedToken,ExtraFuncType> F_providedExtraFuncAttributes = P_rhsExtraFunc.getAttributes();
		HashMap<ScopedToken,ExtraFuncType> F_requiredExtraFuncAttributes = P_lhsExtraFunc.getAttributes();
		
		boolean F_ret = true;
		
		for (ScopedToken F_requiredKey: F_requiredExtraFuncAttributes.keySet()) {
			ExtraFuncType F_providedAttribute = F_providedExtraFuncAttributes.get(F_requiredKey);
			if (F_providedAttribute != null) {
				ExtraFuncType F_requiredAttribute = F_requiredExtraFuncAttributes.get(F_requiredKey);
				if (!F_providedAttribute.fulfil(F_requiredAttribute)) {
					log("Error: Required and provided extra-functional attributes don't match.");
					F_ret = false;
				}
			}
			else {
				F_ret = false;
			}
		}
		
		return F_ret;
	}

	/**
	 * Checks if P_value type matches ExtraFunc registry required type.
	 * @param Ps_type required type
	 * @param Ps_valueType required value type (if enum or interval)
	 * @return if P_value type matches registry
	 */
	private boolean checkTypeMatches(String Ps_type, String Ps_valueType, ExtraFuncType P_value) {
		if (!P_value.getType().equals(Ps_type) && !P_value.getType().equals(Ps_valueType)) {
			log("Error: value type "+P_value.getType() + " doesn't match registry type "+Ps_type + " " + (!Ps_valueType.equals("") ? "or " +Ps_valueType : "")  + ".");
			return false;
		}
		else {
			if (P_value.getType().equals(ExtraFuncType.ENUM)) {
				// check valueType
				ExtraFuncEnum F_value = (ExtraFuncEnum) P_value;
				if (!F_value.getValueType().equals(Ps_valueType)) {
					log("Error: enum items type "+F_value.getValueType()+" doesn't match registry type "+Ps_valueType);
				}
				
			}
			else if (P_value.getType().equals(ExtraFuncType.INTERVAL)) {
				ExtraFuncInterval F_value = (ExtraFuncInterval) P_value;
				if (!F_value.getValueType().equals(Ps_valueType)) {
					log("Error: enum items type "+F_value.getValueType()+" doesn't match registry type "+Ps_valueType);
				}
			}
		}
		return true;
		
	}
	
	
	/**
	 * @see ExtrafuncRegistryService#checkExtraFuncValid(cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc) 
	 */
	public boolean checkExtraFuncValid(ExtraFunc P_extrafunc) {
		HashMap<ScopedToken, ExtraFuncType> F_extrafunc = P_extrafunc
				.getAttributes();
	
		boolean F_retval = true;
		// for each scoped token
		Set<ScopedToken> keys = F_extrafunc.keySet();
	
		for (ScopedToken token : keys) {
			ExtraFuncRegistryEntry F_registryValue = registry.get(token);
			ExtraFuncType F_value = P_extrafunc.getAttributes().get(token);
			
			// check if value is in registry
			if (F_registryValue == null) {
				log("Error: ExtraFunc property not found:" + token.getValue());
				F_retval = false;
				continue;
			} else { // it is in registry
				ExtraFuncType F_constraints = F_registryValue.getConstraints();
	
				String type = F_registryValue.getType();
				String valuetype = F_registryValue.getValueType();

				// check if it has correct type
				if (!checkTypeMatches(type,valuetype,F_value)) {
					F_retval = false;
					continue;
				}
				
				// no constrains and value has tokens
				if (F_constraints == null) {
					if (type.equals(ExtraFuncType.INTERVAL)) {
						if (((ExtraFuncInterval) F_value).getValueType()
								.equals(ExtraFuncType.TOKEN)) {
							log("Error: Interval needs constraints if contains tokens.");
							F_retval = false;
							continue;
						}
					}
	
					if (type.equals(ExtraFuncType.ENUM)) {
						if (F_value.getType().equals(valuetype)) {
							continue;
						}
						
						if (!F_value.getType().equals(type)) {
							log("Error: value must be Enum or "+valuetype + " to match registry.");
							F_retval = false;
							continue;
						}
						else
						
						if (((ExtraFuncEnum) F_value).getValueType().equals(
								ExtraFuncType.TOKEN)) {
							log("Error: Enum needs constraints if contains tokens.");
							F_retval = false;
							continue;
						}
					}
	
					continue;
				}

				if (type.equals(ExtraFuncType.TOKEN)) {
					if (!_checkToken((Token) F_value, F_constraints))
						F_retval = false;
					continue;
				}
	
				if (type.equals(ExtraFuncType.INTEGER)
						|| type.equals(ExtraFuncType.FLOAT)) {
					if (!_checkNumber((ExtraFuncSimpleType) F_value,
							F_constraints))
						F_retval = false;
					continue;
				}
	
				if (type.equals(ExtraFuncType.STRING)) {
					if (!_checkString((ExtraFuncSimpleType) F_value,
							F_constraints)) {
						F_retval = false;
						continue;
					}
				}
	
				if (type.equals(ExtraFuncType.MAP)) {
					if (!_checkMap((ExtraFuncMap) F_value, F_constraints)) {
						F_retval = false;
						continue;
					}
				}
	
				if (type.equals(ExtraFuncType.ENUM)) {
					if (F_value.getType().equals(ExtraFuncType.TOKEN)) {
						if (!_checkToken((Token)F_value, F_constraints)) {
							F_retval = false;
							continue;
						}
					} else
					if (!_checkEnum((ExtraFuncEnum) F_value, F_constraints)) {
						F_retval = false;
						continue;
					}
				}
	
				if (type.equals(ExtraFuncType.INTERVAL)) {
					if (!_checkInterval((ExtraFuncInterval) F_value.getValue(),
							F_constraints)) {
						F_retval = false;
						continue;
					}
				}
	
			}
	
		}
	
		return F_retval;
	}



	/*
	 * Checks parsed registry for bad values.
	 */
	private void checkRegistry() throws Exception {
		// not set
		if (registry == null)
			throw new RuntimeException("ExtraFunc Registry is not set.");
	
		// for all registry lines
		for (ScopedToken token : registry.keySet()) {
			ExtraFuncRegistryEntry F_registryValue = registry.get(token);
			ExtraFuncType F_constraints = F_registryValue.getConstraints();
	
			String F_type = F_registryValue.getType();
	
			// check if all constraints are availables
			if (F_constraints == null) {
				if (F_type.equals(ExtraFuncType.TOKEN)
						|| F_type.equals(ExtraFuncType.MAP)) {
					throw new RuntimeException("Type " + F_type
							+ " needs constraints.");
				} else
					continue;
			}
	
			// check constraint type
			if (F_type.equals(ExtraFuncType.TOKEN)) {
				if (!F_constraints.getType().equals(ExtraFuncType.ENUM)) {
					throw new RuntimeException("Error: Bad constraint type: "
							+ F_constraints.getType());
				} else
					continue;
			}
	
			// check constraint type for INTEGER, FLOAT and STRING
			// can be only interval and enum
			if (F_type.equals(ExtraFuncType.INTEGER)
					|| F_type.equals(ExtraFuncType.FLOAT)
					|| F_type.equals(ExtraFuncType.STRING)) {
				if (!F_constraints.getType().equals(ExtraFuncType.INTERVAL)
						&& !F_constraints.getType().equals(ExtraFuncType.ENUM)) {
					throw new RuntimeException(
							"Error: Bad constraint type for "+F_type+": "
									+ F_constraints.getType()
									+ ". Only interval and enum allowed.");
				} else
					continue;
			}
	
			// check constraint type for MAP
			// can be only MAP
			if (F_type.equals(ExtraFuncType.MAP)) {
				if (!F_constraints.getType().equals(ExtraFuncType.MAP)) {
					throw new RuntimeException(
							"Error: Bad constraint type for map: "
									+ F_constraints.getType()
									+ ". Only map allowed.");
				} else
					continue;
			}
	
			// check constraint type for enum
			// can be only enum
			if (F_type.equals(ExtraFuncType.ENUM)) {
				if (!F_constraints.getType().equals(ExtraFuncType.ENUM)) {
					throw new RuntimeException(
							"Error: Bad constraint type for enum: "
									+ F_constraints.getType()
									+ ". Only enum allowed.");
				} else
					continue;
			}
	
			// check constraint type for interval
			// can be only interval
			if (F_type.equals(ExtraFuncType.INTERVAL)) {
				if (!F_constraints.getType().equals(ExtraFuncType.ENUM)) {
					throw new RuntimeException(
							"Error: Bad constraint type for interval: "
									+ F_constraints.getType()
									+ ". Only enum allowed.");
				} else
					continue;
			}
		}
	}

	/**
	 * Loads registry from extrafuncregistry file.
	 * @throws IOException if error occurs
	 */
	private final void loadRegistry() throws IOException {
		InputStream F_input = this.getClass().getResourceAsStream(
				"extrafuncregistry");
		_fillRegistryLines(F_input);
	}


	/*
	 * Prints message to stdout.
	 * TODO logging for container
	 */
	private static void log(String logmessage) {
//		System.out.println(logmessage);
	}

	/*
	 * Checks if String value fulfills the constraints.   
	 */
	private boolean _checkString(ExtraFuncSimpleType P_value,
			ExtraFuncType P_constraints) {
		boolean F_retval = true;
		if (P_constraints.getType().equals(ExtraFuncType.ENUM)) {
			if (!((ExtraFuncEnum) P_constraints).contains(P_value)) {
				F_retval = false;
				log("Error: Value not allowed: "
						+ P_value.getValue().toString());
			}

		} else if (P_constraints.getType().equals(ExtraFuncType.INTERVAL)) {
			if (!((ExtraFuncInterval) P_constraints).fitsWithinRange(P_value)) {
				F_retval = false;
				log("Error: Value does not fit in interval: "
						+ P_value.getValue().toString());
			}
		}

		return F_retval;
	}

	/*
	 * Checks if Map value fulfills the constraints.   
	 */
	private boolean _checkMap(ExtraFuncMap P_value, ExtraFuncType P_constraints) {
		boolean F_retval = true;
		ExtraFuncMap F_map = (ExtraFuncMap) P_constraints;
		for (Map.Entry<Token,ExtraFuncType> entry: P_value.getValue().entrySet()) {
			Token token = entry.getKey();
			String type = entry.getValue().getType();
			ExtraFuncType F_value = F_map.getValue().get(token);
			if (F_value == null) {
				F_retval = false;
			}
			else
			{
			  if (((Token)F_value).toString().compareTo(type) != 0) {
				  F_retval = false;
			  }
			}
			
		}
		return F_retval;
	}

	/*
	 * Checks if Enum value fulfills the constraints.   
	 */
	private boolean _checkEnum(ExtraFuncEnum P_value,
			ExtraFuncType P_constraints) {
		boolean F_retval = true;
		ExtraFuncEnum F_enum = (ExtraFuncEnum) P_constraints;
		for (ExtraFuncEnumEntry enumEntry : P_value.getValue()) {
			if (!F_enum.contains(enumEntry.getValue())) {
				F_retval = false;
				log("Error: Unregistered enum value: " + enumEntry.getValue());
			}
		}

		return F_retval;
	}

	/*
	 * Checks if Interval value fulfills the constraints.   
	 */
	private boolean _checkInterval(ExtraFuncInterval P_value,
			ExtraFuncType P_constraints) {
		boolean F_retval = true;
		ExtraFuncEnum F_enum = (ExtraFuncEnum) P_constraints;
		ExtraFuncEnumEntry F_floorValue = null;
		ExtraFuncEnumEntry F_ceilingValue = null;
		ExtraFuncEnumEntry F_averageValue = null;
		
		if (P_value.getFloorValue() != null)
			F_floorValue = (ExtraFuncEnumEntry) P_value
				.getFloorValue().getValue();
		
		if (P_value.getCeilingValue() != null)
			F_ceilingValue = (ExtraFuncEnumEntry) P_value
				.getCeilingValue().getValue();
		
		if (P_value.getAverageValue() != null)
			F_averageValue = (ExtraFuncEnumEntry) P_value.getAverageValue()
					.getValue();

		// check if token values are registered
		if (F_floorValue != null) {
			if (!F_enum.contains(F_floorValue.getValue())) {
				F_retval = false;
				log("Error: Unregistered interval value: "
						+ P_value.getFloorValue().getValue().toString());
			} else {
				F_floorValue
						.setPosition(F_enum.get(F_floorValue).getPosition());
			}
		}

		if (F_ceilingValue != null) {
			if (!F_enum.contains(F_ceilingValue.getValue())) {
				F_retval = false;
				log("Error: Unregistered interval value: "
						+ P_value.getCeilingValue().getValue().toString());
			} else {
				F_ceilingValue.setPosition(F_enum.get(F_ceilingValue)
						.getPosition());
			}
		}

		if ((P_value.getAverageValue() != null)
				&& !F_enum.contains(F_averageValue.getValue())) {
			log("Error: Unregistered interval value: "
					+ P_value.getAverageValue().getValue().toString());
			F_retval = false;
		} else if (P_value.getAverageValue() != null) {
			F_averageValue
					.setPosition(F_enum.get(F_averageValue).getPosition());
		}

		try {
			P_value.forceCheck();
		} catch (RuntimeException e) {
			log("Error: " + e.getMessage());
			F_retval = false;
		}
		// set positions according to registry

		return F_retval;
	}

	/*
	 * Checks if Token value fulfills the constraints.   
	 */
	private boolean _checkToken(Token P_value, ExtraFuncType P_constraints) {
		if (((ExtraFuncEnum) P_constraints).contains(P_value)) {
			return true;
		} else {
			log("Error: Token " + P_value.getValue() + " not in registry.");
			return false;
		}
	}

	/*
	 * Checks if Number (float, integer) value fulfills the constraints.   
	 */
	private boolean _checkNumber(ExtraFuncSimpleType P_value,
			ExtraFuncType P_constraints) {

		if (P_constraints.getType().equals(ExtraFuncType.INTERVAL)) {
			ExtraFuncInterval F_interval = (ExtraFuncInterval) P_constraints;
			boolean F_retval = F_interval
					.fitsWithinRange((ExtraFuncSimpleType) P_value);
			if (F_retval == false)
				log("Error: " + P_value.getValue() + " is not allowed value.");
			return F_retval;
		} else if (P_constraints.getType().equals(ExtraFuncType.ENUM)) {
			ExtraFuncEnum F_enum = (ExtraFuncEnum) P_constraints;
			boolean F_retval = F_enum.contains(P_value);
			if (F_retval == false)
				log("Error: " + P_value.getValue() + " is not allowed value.");
			return F_retval;
		}

		return false;
	}

	/*
	 * Reads registry file lines into the registryLines map.
	 */
	private void _fillRegistryLines(InputStream P_registryInput)
			throws IOException {
		BufferedReader F_reader = new BufferedReader(new InputStreamReader(
				P_registryInput));
		registryLines = new HashMap<String, String>();

		String Fs_value = null;
		String Fs_key = null;
		while (F_reader.ready()) {
			String Fs_nextLine = F_reader.readLine();

			if (Fs_nextLine.length() == 0 || Fs_nextLine.startsWith("#")) {
				continue;
			} else
				Fs_nextLine = Fs_nextLine.trim();

			int Fi_doubleDotIndex = Fs_nextLine.indexOf(":");
			if (Fi_doubleDotIndex > 0) {
				if (Fs_value != null && Fs_key != null) {
					registryLines.put(Fs_key, Fs_value);
				}
				Fs_key = Fs_nextLine.substring(0, Fi_doubleDotIndex);
				Fs_value = Fs_nextLine.substring(Fi_doubleDotIndex + 1).trim();
			} else {
				Fs_value += Fs_nextLine;
			}
		}
		registryLines.put(Fs_key, Fs_value);
	}



}
