package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.util.StringTokenizer;

/**
* Container of Artifact Version
* Parsing incoming string a set versions
* @author Miroslav Brožek
*/
public class MavenArtifactVersion {
	private Integer majorVersion;

	private Integer minorVersion;

	private Integer incrementalVersion;

	private Integer buildNumber;

	private String qualifier;

	public MavenArtifactVersion(String v) {
		parseVersion(v);
	}

	public final void parseVersion(String version) {

		int index = version.indexOf("-");
		int index2 = version.indexOf("_");

		String part1;
		String part2 = null;

		if (index < 0) {
			if(index2<0){
				part1 = version;				
			}
			else{
				index = index2;
				part1 = version.substring(0, index);
				part2 = version.substring(index + 1);
			}
		} else {
			part1 = version.substring(0, index);
			part2 = version.substring(index + 1);
		}

		if (part2 != null) {
			try {
				if (part2.length() == 1 || !part2.startsWith("0")) {
					buildNumber = Integer.valueOf(part2);
				} else {
					qualifier = part2;
				}
			} catch (NumberFormatException e) {
				qualifier = part2;
			}
		}

		if (part1.indexOf(".") < 0 && !part1.startsWith("0")) {
			try {
				majorVersion = Integer.valueOf(part1);
			} catch (NumberFormatException e) {
				// qualifier is the whole version, including "-"
				qualifier = version;
				buildNumber = null;
			}
		} else {
			boolean fallback = false;
			StringTokenizer tok = new StringTokenizer(part1, ".");
			try {
				majorVersion = getNextIntegerToken(tok);
				if (tok.hasMoreTokens()) {
					minorVersion = getNextIntegerToken(tok);
				}
				if (tok.hasMoreTokens()) {
					incrementalVersion = getNextIntegerToken(tok);
				}
				if (tok.hasMoreTokens()) {
					qualifier = tok.nextToken("");
					qualifier = qualifier.substring(1);// cutting delimiter
				}
			} catch (NumberFormatException e) {
				fallback = true;
			}

			if (fallback) {
				// qualifier is the whole version, including "-"
				qualifier = version;
				majorVersion = null;
				minorVersion = null;
				incrementalVersion = null;
				buildNumber = null;
			}
		}
	}

	private static Integer getNextIntegerToken(StringTokenizer tok) {
		String s = tok.nextToken();
		if (s.length() > 1 && s.startsWith("0")) {
			throw new NumberFormatException("Number part has a leading 0: '" + s + "'");
		}
		return Integer.valueOf(s);
	}

	public int getMajorVersion() {
		return majorVersion != null ? majorVersion.intValue() : 0;
	}

	public int getMinorVersion() {
		return minorVersion != null ? minorVersion.intValue() : 0;
	}

	public int getIncrementalVersion() {
		return incrementalVersion != null ? incrementalVersion.intValue() : 0;
	}

	public int getBuildNumber() {
		return buildNumber != null ? buildNumber.intValue() : 0;
	}

	public String getQualifier() {
		return qualifier;
	}

}
