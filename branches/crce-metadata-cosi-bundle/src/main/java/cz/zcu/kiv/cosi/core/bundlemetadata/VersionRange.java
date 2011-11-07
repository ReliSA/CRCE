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
 * Version range holder
 * 
 * Version Range:<br>
 * version-range ::= interval | atleast<br>
 * interval ::= ( '[' | '(' ) floor ',' ceiling ( ']' | ')' )<br>
 * atleast ::= version<br>
 * floor ::= version<br>
 * ceiling ::= version<br>
 * 
 * When specifing version range as an interval, then by using '[' or ']' it is
 * meant that version is <b>included</b> in version range. By using '(' or ')'
 * it is meant that version range is delimited by a specified version, but
 * version itself is excluded from the interval. If a version range is specified
 * as a single version, it must be interpreted as the range [version,infinity).
 * The default for a non-specified version range is 0, which maps to
 * [0.0.0,infinity).
 * 
 * 
 * @see Version
 * 
 * @author Bretislav Wajtr
 * 
 */
public class VersionRange {
	private Version floorVersion = new Version("0");

	private Version ceilingVersion = null;

	private boolean includingFloor = true;

	private boolean includingCeiling = false;

	/**
	 * Creates new VersionRange object from a string - see details in class
	 * description for details about version range syntax
	 * 
	 * @param P_versionRange
	 */
	public VersionRange(String P_versionRange) {
		parse(P_versionRange);
	}

	/**
	 * Constructs version range from specified Version objects. Version range is
	 * costructed as interval from <code>P_floorVersion</code> to
	 * <code>P_ceilingVersion</code>.
	 * 
	 * @param P_floorVersion
	 *            Left part of the interval.
	 * @param P_includingFloor
	 *            If true then <code>P_floorVersion</code> is included in the
	 *            interval (it is like using '[' in string specification).<br>
	 *            When set to false then <code>P_floorVersion</code> is
	 *            excluded from the interval (it is like using '(' in string
	 *            specification).<br>
	 * @param P_ceilingVersion
	 *            Right part of the interval.
	 * @param P_includingCeiling
	 *            If true then <code>P_ceilingVersion</code> is included in
	 *            the interval (it is like using ']' in string specification).<br>
	 *            When set to false then <code>P_ceilingVersion</code> is
	 *            excluded from the interval (it is like using ')' in string
	 *            specification).<br>
	 */
	public VersionRange(Version P_floorVersion, boolean P_includingFloor, Version P_ceilingVersion,
			boolean P_includingCeiling) {
		floorVersion = P_floorVersion;
		includingFloor = P_includingFloor;
		ceilingVersion = P_ceilingVersion;
		includingCeiling = P_includingCeiling;
	}

	/**
	 * Fills this VersionRange object from the string.
	 * 
	 * @param P_versionRange
	 */
	private void parse(String P_versionRange) {
		String versionRange = P_versionRange.trim();

		if (versionRange.length() == 0) {
			throw new RuntimeException(
					"Incorrect version range format: version range must not be empty string!");
		}

		// first determine if is it 'interval' or 'atleast'
		if (versionRange.startsWith("(") || versionRange.startsWith("[")) {
			// interval
			if (versionRange.startsWith("(")) {
				includingFloor = false;
			} else if (versionRange.startsWith("[")) {
				includingFloor = true;
			}

			versionRange = versionRange.substring(1);

			if (versionRange.endsWith(")")) {
				includingCeiling = false;
			} else if (versionRange.endsWith("]")) {
				includingCeiling = true;
			} else {
				throw new RuntimeException(
						"Incorrect version range format: version range must end with ) or ] chars!");
			}

			versionRange = versionRange.substring(0, versionRange.length() - 1);

			String[] array = versionRange.split(",");
			String version1 = array[0].trim();
			String version2 = array[1].trim();
			floorVersion = new Version(version1);
			ceilingVersion = new Version(version2);
		} else {
			// atleast
			floorVersion = new Version(versionRange);
			includingFloor = true;
		}
	}

	/**
	 * @return Returns true if <code>Ps_version</code> fits within version
	 *         interval represented by this object.
	 * @param Ps_version
	 *            Version String to be tested.
	 * 
	 * @see VersionRange#fitsWithinRange(Version)
	 */
	public boolean fitsWithinRange(String Ps_version) {
		if (Ps_version == null) {
			return false;
		}

		Version version = new Version(Ps_version);
		return fitsWithinRange(version);
	}

	/**
	 * @return Returns true if <code>P_version</code> fits within version
	 *         interval represented by this object.
	 * 
	 * @param P_version
	 *            Version String to be tested. If null then false is returned.
	 * 
	 */
	public boolean fitsWithinRange(Version P_version) {
		boolean result;

		if (P_version == null) {
			return false;
		}

		int high;
		if (ceilingVersion != null) { // can be null if version range is as
			// 'atleast' (ceiling goes to infinity)
			high = P_version.compareTo(ceilingVersion);
		} else {
			high = -1;
		}

		int low = P_version.compareTo(floorVersion);

		boolean lowerThanHigh = high < 0;
		boolean equalToHigh = includingCeiling && (high == 0);
		boolean higherThanLow = low > 0;
		boolean equalToLow = includingFloor && (low == 0);

		if ((lowerThanHigh || equalToHigh) && (higherThanLow || equalToLow)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * @return Returns right boudary of the version interval.
	 */
	public Version getCeilingVersion() {
		return ceilingVersion;
	}

	/**
	 * Sets the right boudary of the version interval.
	 * 
	 * @param ceilingVersion
	 */
	public void setCeilingVersion(Version ceilingVersion) {
		this.ceilingVersion = ceilingVersion;
	}

	/**
	 * @return Returns left boundary of the version interval.
	 */
	public Version getFloorVersion() {
		return floorVersion;
	}

	/**
	 * Sets the left boundary of the version interval.
	 * 
	 * @param floorVersion
	 */
	public void setFloorVersion(Version floorVersion) {
		this.floorVersion = floorVersion;
	}

	/**
	 * @return Returns true if ceiling boundary of the interval is included in
	 *         the interval, false otherwise.
	 */
	public boolean isIncludingCeiling() {
		return includingCeiling;
	}

	/**
	 * If <code>Pb_includingCeiling</code> is true then ceiling version will
	 * be included in the version interval (it is like using ']' in string
	 * specification).<br>
	 * When false then ceiling version will be excluded from the
	 * interval (it is like using ')' in string specification).
	 */
	public void setIncludingCeiling(boolean Pb_includingCeiling) {
		this.includingCeiling = Pb_includingCeiling;
	}

	/**
	 * @return Returns true if floor boundary of the interval is included in
	 *         the interval, false otherwise.
	 */
	public boolean isIncludingFloor() {
		return includingFloor;
	}

	/**
	 * If <code>Pb_includingFloor</code> is true then floor version will
	 * be included in the version interval (it is like using '[' in string
	 * specification).<br>
	 * When false then floor version will be excluded from the
	 * interval (it is like using '(' in string specification).
	 */
	public void setIncludingFloor(boolean Pb_includingFloor) {
		this.includingFloor = Pb_includingFloor;
	}

	@Override
	public String toString() {
		String Fs_ret = "";

		if (isIncludingFloor()) {
			Fs_ret += "[";
		} else {
			Fs_ret += "(";
		}

		if (floorVersion != null) {
			Fs_ret += floorVersion.toString();
		} else {
			Fs_ret += "0";
		}

		Fs_ret += ",";

		if (ceilingVersion != null) {
			Fs_ret += ceilingVersion.toString();
		} else {
			Fs_ret += "infinity";
		}

		if (isIncludingCeiling()) {
			Fs_ret += "]";
		} else {
			Fs_ret += ")";
		}

		return Fs_ret;
	}
}
