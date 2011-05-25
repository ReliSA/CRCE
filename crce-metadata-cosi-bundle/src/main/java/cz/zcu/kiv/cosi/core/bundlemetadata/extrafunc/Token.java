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
 * @see ScopedToken
 * 
 * @author Vojtech Liska
 * 
 */
public class Token extends ScopedToken {
	private static final String TOKEN_REGEX_EXPRESSION = "[\\w-]+";

	public Token(String Ps_tokenName) {
		super(Ps_tokenName);
		parse(Ps_tokenName);
	}

	public Token(Token P_token) {
		super(P_token.getValue());
		parse(P_token.getValue());
	}

	private void parse(String Ps_tokenName) {
		String Fs_name = Ps_tokenName.trim();

		if (Fs_name.length() == 0) {
			throw new RuntimeException(
					"Incorrect token format: token must not be empty string!");
		}

		if (!Fs_name.matches(TOKEN_REGEX_EXPRESSION))
			throw new RuntimeException(
					"Incorrect token format: token must match regexp "
							+ TOKEN_REGEX_EXPRESSION + " !");

		super.setValue(Fs_name);
	}

	public boolean equals(Object P_token) {
		return super.equals(P_token);
	}

}
