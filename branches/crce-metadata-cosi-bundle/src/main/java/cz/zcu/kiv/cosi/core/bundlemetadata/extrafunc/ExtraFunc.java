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
 * ExtraFunc holder. This class is capable to fill itself from a string which must
 * have following syntax: <br>
 * 
 * extrafunc ::= '(' attributes ')' | attributes <br>
 * attributes :: =  name = value | name <= value | name >= value<br>
 * name ::= scoped-token<br>
 * scoped-token ::= scoped-token '.' token | token<br>
 * token ::= ( alphanum | '_' | '-' )+<br>
 * value ::= scalar | '{' complex '}' | '<' interval '>'<br>
 * scalar ::= boolean | integer | float | token | string<br>
 * boolean ::= 'Y' | 'N' | 'true' | 'false' | '"' string '"' <br>
 * integer ::= ([0..9])+<br>
 * float ::= ([0..9])+'.'([0..9])+<br>
 * string ::= (any character)+ <br>
 * complex ::= enum | map
 * map ::= identifier ':' scalar (, identifier ':' scalar)* 
 * enum ::= scalar (',' scalar)*
 * identifier ::= <see OSGi Service Platform Core Specification, Chapter 1.3.2>
 * 
 * Default value for extrafunc is empty (no extra-functional properties present).
 * 
 * @author Vojtech Liska 
 * 
 */

import java.util.HashMap;

public class ExtraFunc {

	/*
	 * ExtraFunc attributes.
	 */
	private HashMap<ScopedToken, ExtraFuncType> attributes;

	public ExtraFunc(String Ps_parameter) {
		attributes = ExtraFuncParser.parseExtraFunc(Ps_parameter);
	}

	public HashMap<ScopedToken, ExtraFuncType> getAttributes() {
		return attributes;
	}

	public String toString() {
		if (this.isEmpty())
			return "";
		else
			return attributes.toString();
	}

	/**
	 * @return If ExtraFunc holds no attributes.
	 */
	public boolean isEmpty() {
		return attributes.isEmpty();
	}

}
