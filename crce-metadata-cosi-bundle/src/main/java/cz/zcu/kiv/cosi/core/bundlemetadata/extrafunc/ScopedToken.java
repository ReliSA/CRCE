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
 * Class represents scoped-token and token Extra-Functional property.<BR>
 * 
 * scoped-token ::= scoped-token.token | token <BR>
 * token := as per 1.3.2 of [OR4] (see Specification of CoSi v2)
 * 
 * @author Vojtech Liska
 * 
 */

public class ScopedToken extends ExtraFuncType {

	private static final String SCOPEDTOKEN_REGEX_EXPRESSION = "[\\w-]+(\\.[\\w-]+)*";

	private String[] scopedTokenNames = new String[10];

	private String scopedTokenName;

	public ScopedToken(String Ps_scopedTokenName) {
		parse(Ps_scopedTokenName);
	}

	private void parse(String Ps_scopedTokenName) {
		String F_scopedTokenName = Ps_scopedTokenName.trim();

		if (F_scopedTokenName.length() == 0) {
			throw new RuntimeException(
					"Incorrect scoped-token format: scoped-token must not be empty string!");
		}

		if (!F_scopedTokenName.matches(SCOPEDTOKEN_REGEX_EXPRESSION))
			throw new RuntimeException(
					"Incorrect scoped-token format: scoped-token must match regexp "
							+ SCOPEDTOKEN_REGEX_EXPRESSION + " !");

		scopedTokenName = F_scopedTokenName;
		scopedTokenNames = F_scopedTokenName.split("\\.");

	}

	public String[] getScopedTokenNames() {
		if (scopedTokenNames == null) {
			scopedTokenNames = new String[0];
		}
		String[] F_retval = scopedTokenNames.clone();
		return F_retval;
	}

	public String toString() {
		return scopedTokenName;
	}

	@Override
	public String getValue() {
		return scopedTokenName;
	}

	@Override
	public String getType() {
		return TOKEN;
	}

	protected void setValue(String Ps_tokenName) {
		this.scopedTokenName = Ps_tokenName;
		parse(Ps_tokenName);
	}

	public boolean equals(Object P_token) {
		if (!(P_token instanceof ScopedToken))
			return false;
		else
			return scopedTokenName
					.compareTo(((ScopedToken) P_token).scopedTokenName) == 0;
	}

	public int hashCode() {
		return scopedTokenName.hashCode();
	}

	@Override
	public boolean fulfil(ExtraFuncType P_requiredType) {
		if (!P_requiredType.getType().equals(ExtraFuncType.TOKEN))
			return false;
		else
			return this.equals((ScopedToken) P_requiredType);
	}

}
