/*
 * Copyright (c) OSGi Alliance (2004, 2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.zcu.kiv.crce.metadata.type;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Version identifier for bundles and packages.
 *
 * <p>
 * Version identifiers have four components.
 * <ol>
 * <li>Major version. A non-negative integer.</li>
 * <li>Minor version. A non-negative integer.</li>
 * <li>Micro version. A non-negative integer.</li>
 * <li>Qualifier. A text string. See {@code Version(String)} for the
 * format of the qualifier string.</li>
 * </ol>
 *
 * <p>
 * {@code Version} objects are immutable.
 * <p>
 * Based on {@code org.osgi.framework.Version}
 *
 * @since 2.0.0
 * @Immutable
 * @version $Id: a71e2e2d7685e65b5bbe375efdf97fda16eff0a5 $
 */

public class Version implements Comparable<Version> {
	private final int			major;
	private final int			minor;
	private final int			micro;
	private final String		qualifier;
	private static final String	SEPARATOR		= ".";
	private transient String	versionString;

	/**
	 * The empty version "0.0.0".
	 */
	public static final Version	emptyVersion	= new Version(0, 0, 0);

	/**
	 * Creates a version identifier from the specified numerical components.
	 *
	 * <p>
	 * The qualifier is set to the empty string.
	 *
	 * @param major Major component of the version identifier.
	 * @param minor Minor component of the version identifier.
	 * @param micro Micro component of the version identifier.
	 * @throws IllegalArgumentException If the numerical components are
	 *         negative.
	 */
	public Version(int major, int minor, int micro) {
		this(major, minor, micro, null);
	}

	/**
	 * Creates a version identifier from the specified components.
	 *
	 * @param major Major component of the version identifier.
	 * @param minor Minor component of the version identifier.
	 * @param micro Micro component of the version identifier.
	 * @param qualifier Qualifier component of the version identifier. If
	 *        {@code null} is specified, then the qualifier will be set to
	 *        the empty string.
	 * @throws IllegalArgumentException If the numerical components are negative
	 *         or the qualifier string is invalid.
	 */
	public Version(int major, int minor, int micro, String qualifier) {
		if (qualifier == null) {
			qualifier = "";
		}

		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
		versionString = null;
		validate();
	}	
	
	/**
	 * Created a version identifier from the specified string.
	 *
	 * <p>
	 * Here is the grammar for version strings.
	 *
	 * <pre>
	 * version ::= major('.'minor('.'micro('.'qualifier)?)?)?
	 * major ::= digit+
	 * minor ::= digit+
	 * micro ::= digit+
	 * qualifier ::= (alpha|digit|'_'|'-')+
	 * digit ::= [0..9]
	 * alpha ::= [a..zA..Z]
	 * </pre>
	 *
	 * There must be no whitespace in version.
	 *
	 * @param version String representation of the version identifier.
	 * @throws IllegalArgumentException If {@code version} is improperly
	 *         formatted.
	 */
	public Version(String version) {
		int maj = 0;
		int min = 0;
		int mic = 0;
		String qual = "";

		try {
			StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
			maj = Integer.parseInt(st.nextToken());

			if (st.hasMoreTokens()) { // minor
				st.nextToken(); // consume delimiter
				min = Integer.parseInt(st.nextToken());

				if (st.hasMoreTokens()) { // micro
					st.nextToken(); // consume delimiter
					mic = Integer.parseInt(st.nextToken());

					if (st.hasMoreTokens()) { // qualifier
						st.nextToken(); // consume delimiter
						qual = st.nextToken(""); // remaining string

						if (st.hasMoreTokens()) { // fail safe
							throw new IllegalArgumentException(
									"invalid format: " + version);
						}
					}
				}
			}
		}
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid format: " + version, e);
		}

		major = maj;
		minor = min;
		micro = mic;
		qualifier = qual;
		versionString = null;
		validate();
	}

	/**
	 * Called by the Version constructors to validate the version components.
	 *
	 * @throws IllegalArgumentException If the numerical components are negative
	 *         or the qualifier string is invalid.
	 */
	private void validate() {
		if (major < -1) {
			throw new IllegalArgumentException("Negative major: " + major);
		}
		if (minor < -1) {
			throw new IllegalArgumentException("Negative minor: " + minor);
		}
		if (micro < -1) {
			throw new IllegalArgumentException("Negative micro: " + micro);
		}
		char[] chars = qualifier.toCharArray();
		for (int i = 0, length = chars.length; i < length; i++) {
	        char ch = chars[i];
			if ('A' <= ch && ch <= 'Z') {
				continue;
			}
			if ('a' <= ch && ch <= 'z') {
				continue;
			}
			if ('0' <= ch && ch <= '9') {
				continue;
			}
			if (ch == '_' || ch == '-' || ch == '.') {				
				continue;
			}			
			
			throw new IllegalArgumentException("invalid qualifier: " + qualifier);
		}
	}

	/**
	 * Parses a version identifier from the specified string.
	 *
	 * <p>
	 * See {@code Version(String)} for the format of the version string.
	 *
	 * @param version String representation of the version identifier. Leading
	 *        and trailing whitespace will be ignored.
	 * @return A {@code Version} object representing the version
	 *         identifier. If {@code version} is {@code null} or
	 *         the empty string then {@code emptyVersion} will be
	 *         returned.
	 * @throws IllegalArgumentException If {@code version} is improperly
	 *         formatted.
	 */
	public static Version parseVersion(String version) {
		if (version == null) {
			return emptyVersion;
		}

		version = version.trim();
		if (version.length() == 0) {
			return emptyVersion;
		}

		return new Version(version);
	}

	/**
	 * Returns the major component of this version identifier.
	 *
	 * @return The major component.
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Returns the minor component of this version identifier.
	 *
	 * @return The minor component.
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Returns the micro component of this version identifier.
	 *
	 * @return The micro component.
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Returns the qualifier component of this version identifier.
	 *
	 * @return The qualifier component.
	 */
	public String getQualifier() {
		return qualifier;
	}

	/**
	 * Returns the string representation of this version identifier.
	 *
	 * <p>
	 * The format of the version string will be {@code major.minor.micro}
	 * if qualifier is the empty string or
	 * {@code major.minor.micro.qualifier} otherwise.
	 *
	 * @return The string representation of this version identifier.
	 */
    @Override
	public String toString() {
		if (versionString != null) {
			return versionString;
		}
		int q = qualifier.length();
        @SuppressWarnings("StringBufferMayBeStringBuilder")
		StringBuffer result = new StringBuffer(20 + q);        
        result.append(convertNumber(major));
		result.append(SEPARATOR);
		result.append(convertNumber(minor));
		result.append(SEPARATOR);
		result.append(convertNumber(micro));
		if (q > 0) {
			result.append(SEPARATOR);
			result.append(qualifier);
		}
		return versionString = result.toString();
	}
    
    /**
     * Conversion from -1 to ""
     * @param n input nr.
     * @return new string value
     */
    private String convertNumber(int n){
    	if(n == -1){
    		return "";
    	}
    	else{
    		return ""+n;
    	}    	
    }

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return An integer which is a hash code value for this object.
	 */
    @Override
	public int hashCode() {
		return major << 24 + minor << 16 + micro << 8 + qualifier.hashCode();
	}

	/**
	 * Compares this {@code Version} object to another object.
	 *
	 * <p>
	 * A version is considered to be <b>equal to </b> another version if the
	 * major, minor and micro components are equal and the qualifier component
	 * is equal (using {@code String.equals}).
	 *
	 * @param object The {@code Version} object to be compared.
	 * @return {@code true} if {@code object} is a
	 *         {@code Version} and is equal to this object;
	 *         {@code false} otherwise.
	 */
    @Override
	public boolean equals(Object object) {
		if (object == this) { // quicktest
			return true;
		}

		if (!(object instanceof Version)) {
			return false;
		}

		Version other = (Version) object;
		return major == other.major && minor == other.minor && micro == other.micro && qualifier.equals(other.qualifier);
	}

	/**
	 * Compares this {@code Version} object to another {@code Version}.
	 *
	 * <p>
	 * A version is considered to be <b>less than </b> another version if its
	 * major component is less than the other version's major component, or the
	 * major components are equal and its minor component is less than the other
	 * version's minor component, or the major and minor components are equal
	 * and its micro component is less than the other version's micro component,
	 * or the major, minor and micro components are equal and it's qualifier
	 * component is less than the other version's qualifier component (using
	 * {@code String.compareTo}).
	 *
	 * <p>
	 * A version is considered to be <b>equal to</b> another version if the
	 * major, minor and micro components are equal and the qualifier component
	 * is equal (using {@code String.compareTo}).
	 *
	 * @param other The {@code Version} object to be compared.
	 * @return A negative integer, zero, or a positive integer if this version
	 *         is less than, equal to, or greater than the specified
	 *         {@code Version} object.
	 * @throws ClassCastException If the specified object is not a
	 *         {@code Version} object.
	 */
    @Override
	public int compareTo(Version other) {
		if (other == this) { // quicktest
			return 0;
		}

		int result = major - (other != null ? other.major : 0);
		if (result != 0) {
			return result;
		}

		result = minor - (other != null ? other.minor : 0);
		if (result != 0) {
			return result;
		}

		result = micro - (other != null ? other.micro : 0);
		if (result != 0) {
			return result;
		}

		return qualifier.compareTo(other != null ? other.qualifier : "");
	}
}
