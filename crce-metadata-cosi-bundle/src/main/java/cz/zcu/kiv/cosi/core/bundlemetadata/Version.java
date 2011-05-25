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

/**
 * Version holder. This class is capable to fill itself from a string which have
 * to have following syntax:<br>
 * 
 * version ::= major( '.' minor ( '.' micro ( '.' qualifier )? )? )?<br>
 * major ::= number<br>
 * minor ::= number<br>
 * micro ::= number<br>
 * qualifier ::= ( alphanum | '_' | '-' )+<br>
 * 
 * A version token must not contain any white space. The default value for a
 * version is 0.0.0. Note that qualifier comparsion is made through standard
 * java String.compareTo method! (this means that for example "1.1.1.build-05" <
 * "1.1.1.build-1" is true, altough intuitively not) A version token must not be
 * empty string.
 * 
 * In this implementation a qualifier can contain any character (including '.')
 * 
 * @author Bretislav Wajtr
 */
public class Version implements Comparable<Version> {

	/**
	 * Regular expression which is used to test correct syntax of the
	 * version-string when parsing.
	 */
	private static final String VERSION_REGEX_EXPRESSION = "\\d+(\\.\\d+(\\.\\d+(\\.[\\w-]+)?)?)?";
	protected VersionData versionData;

	/**
	 * Creates new Version object from a String which has to have correct syntax
	 * - this syntax is specified in a class description.
	 * 
	 * @param Ps_version
	 *            A version string to be parsed.
	 * @throws Throws
	 *             VersionFormatException if the string doesn't have the correct
	 *             syntax.
	 */
	public Version(String Ps_version) {
		versionData = parseVersion(Ps_version);
	}

	private VersionData parseVersion(String P_version) {
		String version = P_version.trim();

		// regex explanation:
		// \\d+ - digit once or more (any number)
		// ( something )? - grouping once or not at all (optional)
		// \\. - simple dot
		// \\w- - A word character: [a-zA-Z_0-9] plus a '-' character
		//
		// so it is simply: number(.number(.number(.string)?)?)?
		// where number must consist from at least one digit
		// where string consists only from chars a-z or A-Z or 0-9 or '_' or
		// '-'
		if (!version.matches(VERSION_REGEX_EXPRESSION)) {
			throw new VersionFormatException(
					"Incorrect version format: must pass regex expression \\d+(\\.\\d+(\\.\\d+(\\.[\\w-]+)?)?)?");
		}

		VersionData result = new VersionData();

		String[] array = version.split("\\.", 4);

		if (array.length > 0) {
			result.major = Integer.valueOf(array[0]);
		}
		if (array.length > 1) {
			result.minor = Integer.valueOf(array[1]);
		}
		if (array.length > 2) {
			result.micro = Integer.valueOf(array[2]);
		}
		if (array.length > 3) {
			result.qualifier = array[3];
		}

		return result;
	}

	/**
	 * Tests if version represented by this object is equal to the
	 * <code>Ps_version</code>. A <code>Ps_version</code> is parsed first, which
	 * means that this parameter must be valid version string.
	 * 
	 * Versions are equal if theirs major, minor and micro number are equal and
	 * if their qualifiers are equal in <b>a manner of
	 * <code>String.equalsIgnoreCase()</code> method</b>.
	 * 
	 * @param Ps_version
	 *            Version to be compared.
	 * @return Returns true if versions are equal, false otherwise.
	 * @throws Throws
	 *             VersionFormatException if <code>Ps_version</code> doesn't
	 *             have correct syntax.
	 */
	public boolean isEqualTo(String Ps_version) {
		VersionData data = parseVersion(Ps_version);
		return isEqualTo(data);
	}

	/**
	 * Tests if version represented by this object is equal to the
	 * <code>P_version</code>.
	 * 
	 * Versions are equal if theirs major, minor and micro number are equal and
	 * if their qualifiers are equal in <b>a manner of
	 * <code>String.equalsIgnoreCase()</code> method</b>.
	 * 
	 * @param P_version
	 *            Version to be compared.
	 * @return Returns true if versions are equal, false otherwise.
	 */
	public boolean isEqualTo(Version P_version) {
		return isEqualTo(P_version.versionData);
	}

	/**
	 * Tests if version represented by this object is equal to the
	 * <code>P_versionData</code> object, which represents only a holder for
	 * version values.
	 * 
	 * Versions are equal if theirs major, minor and micro number are equal and
	 * if their qualifiers are equal in <b>a manner of
	 * <code>String.equalsIgnoreCase()</code> method</b>.
	 * 
	 * @see VersionData
	 * @param P_versionData
	 *            Version data to be compared.
	 * @return Returns true if versions are equal, false otherwise.
	 */
	public boolean isEqualTo(VersionData P_versionData) {
		boolean result = true;
		result = result && (P_versionData.major.equals(versionData.major));
		result = result && (P_versionData.minor.equals(versionData.minor));
		result = result && (P_versionData.micro.equals(versionData.micro));
		result = result
				&& (P_versionData.qualifier
						.equalsIgnoreCase(versionData.qualifier));
		return result;
	}

	/**
	 * @return Returns simple holder for a version data (it's major, minor and
	 *         micro numbers and a version qualifier)
	 * @see VersionData
	 */
	public VersionData getVersionData() {
		return versionData;
	}

	/**
	 * @return Returns major number part of the version. For example in
	 *         version.'1.2.3.build05' this method will return 1.
	 */
	public Integer getMajor() {
		return versionData.major;
	}

	/**
	 * Sets the major number part of the version. For example in
	 * version.'1.2.3.build05', number 1 is the major number part.
	 * 
	 * @param major
	 */
	public void setMajor(Integer major) {
		versionData.major = major;
	}

	/**
	 * @return Returns micro number part of the version. For example in
	 *         version.'1.2.3.build05' this method will return 3.
	 */
	public Integer getMicro() {
		return versionData.micro;
	}

	/**
	 * Sets the micro number part of the version. For example in
	 * version.'1.2.3.build05', number 3 is the micro number part.
	 * 
	 * @param micro
	 */
	public void setMicro(Integer micro) {
		versionData.micro = micro;
	}

	/**
	 * @return Returns minor number part of the version. For example in
	 *         version.'1.2.3.build05' this method will return 2.
	 */
	public Integer getMinor() {
		return versionData.minor;
	}

	/**
	 * Sets the minor number part of the version. For example in
	 * version.'1.2.3.build05', number 2 is the minor number part.
	 * 
	 * @param minor
	 */
	public void setMinor(Integer minor) {
		versionData.minor = minor;
	}

	/**
	 * @return Returns qualifier part of the version. For example in
	 *         version.'1.2.3.build05' this method will return string 'build05'.
	 */
	public String getQualifier() {
		return versionData.qualifier;
	}

	/**
	 * Sets the qualifier part of the version. For example in
	 * version.'1.2.3.build05', string 'build05' is the qualifier part.
	 * 
	 * @param qualifier
	 */
	public void setQualifier(String qualifier) {
		versionData.qualifier = qualifier;
	}

	/**
	 * Compares version <code>P_version</code> with version represented by this
	 * object.
	 * 
	 * When comparing major, minor and micro numbers Integer.compareTo method is
	 * used and when comparing qualifier parts then String.compareTo method is
	 * used.
	 * 
	 * @return Returns value less than zero if <code>P_version</code> is lower
	 *         than version represented by this object.<br>
	 *         Returns value greater than zero if <code>P_version</code> is
	 *         higher than version represented by this object.<br>
	 *         Returns zero if <code>P_version</code> is equal to version
	 *         represented by this object.<br>
	 */
	public int compareTo(Version P_version) {
		int result = 0;

		result = versionData.major.compareTo(P_version.getMajor());
		if (result == 0) {
			result = versionData.minor.compareTo(P_version.getMinor());
			if (result == 0) {
				result = versionData.micro.compareTo(P_version.getMicro());
				if (result == 0) {
					result = versionData.qualifier.compareTo(P_version
							.getQualifier());
				}
			}
		}

		return result;
	}

	@Override
	public String toString() {
		String result = "" + getMajor() + "." + getMinor() + "." + getMicro();
		if (getQualifier().length() > 0) {
			result += "." + getQualifier();
		}
		return result;
	}

	/**
	 * A holder for the values of the version.
	 * 
	 * @author Bretislav Wajtr
	 */
	public class VersionData {
		public Integer major = 0;
		public Integer minor = 0;
		public Integer micro = 0;
		public String qualifier = "";
	}

}
