package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.util.StringTokenizer;

import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
* Container of Artifact Version
* Parsing incoming string a set versions
* @author Miroslav Brožek
*/
public class MavenArtifactVersion {
	private Integer majorVersion;

	private Integer minorVersion;

	private Integer microVersion;

	private Integer buildNumber;

	private String qualifier;
	
	private String vMin;
	
	private Operator vMinOperator;
	
	private String vMax;
	
	private Operator vMaxOperator;
	
	private boolean rangeVersion = false;

	public MavenArtifactVersion(String v) {
		parseVersion(v);
	}

	public final void parseVersion(String version) {
		
		//check if version is written as range
		if( (version.startsWith("[") || version.startsWith("(")) && 
				(version.endsWith("]") || version.endsWith(")")) ){
			rangeVersion = checkRangeVersion(version);			
		}
		
		
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
					microVersion = getNextIntegerToken(tok);
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
				microVersion = null;
				buildNumber = null;	
				
				if(!rangeVersion){
					rangeVersion = checkRangeVersion(qualifier);
				}
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
		return majorVersion != null ? majorVersion.intValue() : -1;
	}

	public int getMinorVersion() {
		return minorVersion != null ? minorVersion.intValue() : -1;
	}

	public int getMicroVersion() {
		return microVersion != null ? microVersion.intValue() : -1;
	}

	public int getBuildNumber() {
		return buildNumber != null ? buildNumber.intValue() : -1;
	}

	public String getQualifier() {
		return qualifier;
	}
	
	private boolean checkRangeVersion(String ver) {
		
		if(checkNormalRange(ver) || checkHardVersion(ver)){
			return true;
		}		
		
		return false;
	}

	private boolean checkHardVersion(String ver) {

		vMin = ver.substring(0, ver.length() - 1);
		vMax = ver.substring(1, ver.length());

		// parse brackets
		if (setMinOperator(vMin) && setMaxOperator(vMax)) {
			return true;
		}

		return false;
	}

	private boolean checkNormalRange(String ver) {
		boolean range = false;

		int index = ver.indexOf(",");

		if (index < 0) {
			return range;
		}

		else {

			// must have 2 brackets , one coma and at least 1 digit >> qualifier
			// must be bigger than 3
			if (ver.length() > 3) {

				vMin = ver.substring(0, index);
				vMax = ver.substring(index + 1);

				// parse brackets
				if (setMinOperator(vMin) && setMaxOperator(vMax)) {
					range = true;
				}

				// at least one part must have number
				if (!hasNumber(vMin) && !hasNumber(vMax)) {
					range = false;
				}
			}

		}
		return range;
	}

	private boolean setMinOperator(String v) {
		boolean b = false;
		
		if (v.charAt(0) == '(') {
			vMinOperator = Operator.GREATER;
			b = true;
			
		} else if (v.charAt(0) == '[') {
			vMinOperator = Operator.GREATER_EQUAL;
			b =  true;
		}
		
		if (vMin.length() > 1) {
			vMin = vMin.substring(1).trim();
		}
		else{
			vMin = "";
		}
		
		return b;
	}

	private boolean setMaxOperator(String v) {
		boolean b = false;
		
		if (v.charAt(v.length()-1) == ')') {
			vMaxOperator = Operator.LESS;
			b = true;
		} else if (v.charAt(v.length()-1)  == ']') {
			vMaxOperator = Operator.LESS_EQUAL;
			b = true;
		}
		
		if (vMax.length() >= 2) {
			vMax = vMax.substring(0, vMax.length() - 1).trim();
		}
		
		else{
			vMax = "";
		}
		
		return b;
	}
	
	
	private boolean hasNumber(String v){	

		char[] chars = v.toCharArray();
		for (int i = 0, length = chars.length; i < length; i++) {
			char ch = chars[i];

			if ('0' <= ch && ch <= '9') {
				return true;
			} 
		}
		return false;
	}
	

	public String getvMin() {
		return vMin;
	}

	public void setvMin(String vMin) {
		this.vMin = vMin;
	}

	public Operator getvMinOperator() {
		return vMinOperator;
	}

	public void setvMinOperator(Operator vMinOperator) {
		this.vMinOperator = vMinOperator;
	}

	public String getvMax() {
		return vMax;
	}

	public void setvMax(String vMax) {
		this.vMax = vMax;
	}

	public Operator getvMaxOperator() {
		return vMaxOperator;
	}

	public void setvMaxOperator(Operator vMaxOperator) {
		this.vMaxOperator = vMaxOperator;
	}

	public boolean isRangeVersion() {
		return rangeVersion;
	}

	public void setRangeVersion(boolean rangeVersion) {
		this.rangeVersion = rangeVersion;
	}
	
	/**
	 * Prevent failing validation because of 
	 * short version format or strange qualifier
	 * @param v handled version from Artifact
	 * @return new format of Version.class
	 */
	public Version convertVersion() {
		return new Version(getMajorVersion(), getMinorVersion(), getMicroVersion(), getQualifier());
	}
	
}
