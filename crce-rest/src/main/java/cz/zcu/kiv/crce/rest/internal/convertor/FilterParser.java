package cz.zcu.kiv.crce.rest.internal.convertor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.rest.internal.structures.VersionDemand;

/**
 * Parser of requirement filter.
 * 
 * TODO: Made filter more general
 * @author Jan Reznicek
 *
 */
public class FilterParser {	
	
	Logger log = LoggerFactory.getLogger(FilterParser.class);
	
	/**
	 * Get name of operation from string.
	 * Supported operations:
	 * 
	 * <ul>
	 * <li> '>='  - greater-than
	 * <li> '<='  - less-than
	 * <li> '='  - equal
	 * <li> '>'  - greater-equal
	 * <li> '<'  - less-equal
	 * <li> '<>'  - not-equal
 	 * </ul>
	 * 
	 * @param operation from list of supported operations
	 * @return name of operation
	 * @throws UnsupportedOperationException uknown operation
	 */
	private String operationToString(String operation) {
		if(operation==null) return "equal";
		else {
			switch (operation) {
			case ">=":
				return VersionDemand.GREATER_EQUEAL;
			case "<=":
				return VersionDemand.LESS_EQUEAL;
			case "=":
				return VersionDemand.EQUEAL;
			case "<":
				return VersionDemand.LESS_THAN;
			case ">":
				return VersionDemand.GREATER_THAN;
			case "<>":
				return VersionDemand.NOT_EQUEAL;
			default:
				throw new UnsupportedOperationException("Unssuported operation(" + operation + ") during parsing filter in a requirement.");
			}
			
		}
		
	}
	
	/**
	 * Parse filter in of osgi requirement wirings.
	 * Filter look like this: (&(osgi.wiring.package=cz.zcu.kiv.obcc.example.carpark.arrivals)(version&gt;=1.0.0))
	 * From this string should be parsed name  (string after 'osgi.wiring.package=' of after 'package='),
	 * and version with operation (version 1.0.0, operation greater-than).
	 * Version with operation can be present multiple time
	 * 
	 * @param filter filter string
	 * @return array of string. Name is always first in this array. 
	 * Its followed by version and operation, that can be present more than one (or can't be present).
	 * If parsing failed, empty array is returned.
	 */
	public String[] parseFilter(String filter) {
		//System.out.println("Parse filter : " + filter);
		Pattern filterPattern = Pattern.compile("\\(&(amp;)?\\((osgi.wiring.)?package=\\s*(.+?)\\s*\\)(\\(version\\s*(.+?)\\s*\\))?\\)");
		
		
		Matcher matcher =filterPattern.matcher(filter);
		if (matcher.matches()) {
			String name = matcher.group(3);
			//System.out.println("Parsed name:  " +  name);
			
			try {
				String versions = matcher.group(4);
				versions = versions.replaceAll("&gt;", ">");
				versions = versions.replaceAll("&lt;", "<");
				//System.out.println("Parsing versios: " + versions);
				String[] version = versions.split("[\\(\\)]");
				
				int versioncount = 0;
				for(String ver:version) {
					if(ver.length()>0) {
						//System.out.println("Parsed version: " + ver);
						versioncount++;
					}
				}
				
				String[] result = new String[1+2*versioncount];
				result[0] = name;
				
				int i = 1;
				for(String ver:version) {
					if(ver.length()>0) {
						Pattern versionPattern = Pattern.compile("version(>=|<=|<>|=|>|<)(.*)");
						Matcher versionMatcher =versionPattern.matcher(ver);
						if(versionMatcher.matches()) {
							String op = versionMatcher.group(1);
							//log.info("operation " + op);
							String vers = versionMatcher.group(2);
							//log.info("version " + vers);
							result[i++] = vers;
							result[i++] = operationToString(op);
							
							
						} else {
							//System.out.println("Version cant be parsed from filter");
							String[] nameArray = {name};
							return nameArray;
						}
					}
				}
				
				return result;
				
				
			} catch (NullPointerException e) {
				//System.out.println("Parsed filter has no version");
				String[] nameArray = {name};
				return nameArray;
			} catch (UnsupportedOperationException e) {
				log.warn(e.getMessage(), e);
				return new String[0];
			}
			
		} else {
			log.warn("Osgi wiring requirement filter dont match the pattern and can't be parsed.");
			return new String[0];
		}
	}
}
