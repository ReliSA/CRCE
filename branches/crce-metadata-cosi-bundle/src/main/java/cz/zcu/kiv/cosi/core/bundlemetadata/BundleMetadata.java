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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;
import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ManifestExtraFuncHeader;

/**
 * This class serves as a manifest.mf file parser. This class and classes used
 * by this class makes easier to read manifest.mf file and performs number of
 * checks that parsed manifest.mf file is valid. A format of a manifest.mf file
 * is specified as part of the CoSi framework and differs in 'ordinary' Java
 * manifest.mf file only in more specific headers.
 *
 * Manifest have following general syntax:
 *
 * Manifest ::= ( header ) * <br>
 * header ::= headername ':' headervalue <br>
 * headername ::= any string <br>
 * headervalue :: any string <br>
 *
 * List of supported headers is defined in this class as constants. For exact
 * syntax for every header look into CoSi documentation or look for them in
 * classes in this package.
 *
 * Altough it has different headers it behaves as Java's manifest - for example
 * that one header can be specified multiple times, but only the last one is
 * taken into account.<br>
 * There are several headers, which are considered mandatory, and thus have to
 * be included in every CoSi manifest.mf file. These mandatory headers are
 * specified in method {@link BundleMetadata#_getMandatoryHeaders()}.
 *
 * Class <code>BundleMetadata</code> also offers methods for gaining values
 * for specified headers in a parsed form. This means that if a value of the
 * header is a version (for example in a case of Bundle-Version header), then
 * there is a method {@link BundleMetadata#getBundleVersion()} which returns a
 * Version object instead of a simple String. For every header and it's value in
 * manifest.mf there exist a corresponding class which parses this value and a
 * corresponding method in BundleMetadata to obtain this value.
 *
 * Manifest.mf can be 'multiline' - one header doesn't have to necessarily be on
 * one line, but it can spread on multiple lines. Of course next header have to
 * begin on a new line.
 *
 * @author Bretislav Wajtr
 * @author Premysl Brada
 * @author Vojtech Liska
 */
public class BundleMetadata {
	public static final String REQUIRE_TYPES = "Require-Types";
	public static final String PROVIDE_TYPES = "Provide-Types";
	public static final String REQUIRE_ATTRIBUTES = "Require-Attributes";
	public static final String PROVIDE_ATTRIBUTES = "Provide-Attributes";
	public static final String CONSUME_EVENTS = "Consume-Events";
	public static final String GENERATE_EVENTS = "Generate-Events";
	public static final String REQUIRE_INTERFACES = "Require-Interfaces";
	public static final String PROVIDE_INTERFACES = "Provide-Interfaces";
	public static final String REQUIRE_SERVICES = "Require-Services";
	public static final String PROVIDE_SERVICES = "Provide-Services";
	public static final String CONTROL_CLASS = "Control-Class";
	public static final String BUNDLE_CLASS_PATH = "Bundle-ClassPath";
	public static final String BUNDLE_DESCRIPTION = "Bundle-Description";
	public static final String BUNDLE_VERSION = "Bundle-Version";
	public static final String BUNDLE_NAME = "Bundle-Name";
	public static final String BUNDLE_PROVIDER = "Bundle-Provider";
	public static final String BUNDLE_EXTRAFUNC = "Bundle-ExtraFunc";
	public static final String COSI_VERSION = "Cosi-Version";
	public static final String PROVIDE_PACKAGES = "Provide-Packages";
	public static final String REQUIRE_PACKAGES = "Require-Packages";
	public static final String EXTRAFUNC_CATALOG = "ExtraFunc-Catalog";



	private ArrayList<ManifestGenericHeader> headers = new ArrayList<ManifestGenericHeader>(3);
	private HashMap<String, String> manifestLines = new HashMap<String, String>();

	public BundleMetadata(InputStream P_manifestInput) throws IOException {
		_fillManifestLines(P_manifestInput);
		parseManifest();
	}

	private void _fillManifestLines(InputStream P_manifestInput) throws IOException {
		BufferedReader F_reader = new BufferedReader(new InputStreamReader(P_manifestInput));

		String Fs_value = null;
		String Fs_key = null;
		while (F_reader.ready()) {
			String Fs_nextLine = F_reader.readLine().trim();

			if (Fs_nextLine.length() == 0) {
				continue;
			}

			int Fi_doubleDotIndex = Fs_nextLine.indexOf(":");
			if (Fi_doubleDotIndex > 0) {
				if (Fs_value != null && Fs_key != null) {
					manifestLines.put(Fs_key, Fs_value);
				}
				Fs_key = Fs_nextLine.substring(0, Fi_doubleDotIndex);
				Fs_value = Fs_nextLine.substring(Fi_doubleDotIndex + 1).trim();
			} else {
				Fs_value += Fs_nextLine;
			}
		}
		manifestLines.put(Fs_key, Fs_value);

	}

	private ManifestGenericHeader _parseHeader(String P_headerName,
			String P_headerValue) {
		// TODO try to make this different way
		// TODO Refactoring: Join attributes and events as it is with interfaces
		// and types
		if (P_headerName.equalsIgnoreCase(BUNDLE_VERSION)) {
			return new ManifestVersionHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(PROVIDE_INTERFACES)
				|| P_headerName.equalsIgnoreCase(PROVIDE_TYPES)
				|| P_headerName.equalsIgnoreCase(PROVIDE_SERVICES)
				|| P_headerName.equalsIgnoreCase(PROVIDE_PACKAGES)) {
			return new ManifestProvidingHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(REQUIRE_INTERFACES)
				|| P_headerName.equalsIgnoreCase(REQUIRE_TYPES)
				|| P_headerName.equalsIgnoreCase(REQUIRE_SERVICES)
				|| P_headerName.equalsIgnoreCase(REQUIRE_PACKAGES)) {
			return new ManifestRequiringHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(PROVIDE_ATTRIBUTES)) {
			return new ManifestAttributeProvidingHeader(P_headerName,
					P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(REQUIRE_ATTRIBUTES)) {
			return new ManifestAttributeRequiringHeader(P_headerName,
					P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(GENERATE_EVENTS)) {
			return new ManifestEventProvidingHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(CONSUME_EVENTS)) {
			return new ManifestEventConsumingHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(BUNDLE_CLASS_PATH)) {
			return new ManifestStringListHeader(P_headerName, P_headerValue);
		} else if (P_headerName.equalsIgnoreCase(BUNDLE_EXTRAFUNC)) {
			return new ManifestExtraFuncHeader(P_headerName, P_headerValue);
		} else {
			return new ManifestGenericHeader(P_headerName, P_headerValue);
		}
	}

	private void parseManifest() {
		Set<String> keys = manifestLines.keySet();
		for (String key : keys) {
			String headerName = key;
			String headerValue = manifestLines.get(key);
			ManifestGenericHeader parsedHeader = _parseHeader(headerName, headerValue);
			headers.add(parsedHeader);
		}
	}

	/**
	 * Checks if manifest file contains mandatory headers.
	 */
	public boolean isManifestValid() {

		ArrayList<String> F_mandatoryHeaders = _getMandatoryHeaders();

		for (String Fs_mandatoryHeader : F_mandatoryHeaders) {
			if (!manifestLines.containsKey(Fs_mandatoryHeader)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return Returns list of headers which must be present in every CoSi
	 *         manifest.mf file.
	 */
	private ArrayList<String> _getMandatoryHeaders() {
		ArrayList<String> F_ret = new ArrayList<String>();

		F_ret.add(BUNDLE_VERSION);
		F_ret.add(BUNDLE_NAME);
//		F_ret.add(CONTROL_CLASS);   - manifest "Control-Class must be present" constraint check
		F_ret.add(BUNDLE_PROVIDER);

		return F_ret;
	}

	/**
	 * Returns parsed value for a header.
	 *
	 * @param P_attributeName
	 * @return
	 */
	private Object getHeaderValue(String Ps_headerName) {
		for (ManifestGenericHeader header : headers) {
			if (header.getHeaderName().equalsIgnoreCase(Ps_headerName)) {
				return header.getHeaderValue();
			}
		}
		return null;
	}

	public String getCosiVersion() {
		return (String) getHeaderValue(COSI_VERSION);
	}

	public String getBundleName() {
		return (String) getHeaderValue(BUNDLE_NAME);
	}

	public Version getBundleVersion() {
		return (Version) getHeaderValue(BUNDLE_VERSION);
	}

	public String getBundleDescription() {
		return (String) getHeaderValue(BUNDLE_DESCRIPTION);
	}

	public String[] getBundleClasspath() {
		return (String[]) getHeaderValue(BUNDLE_CLASS_PATH);
	}

	public String getControlClass() {
		return (String) getHeaderValue(CONTROL_CLASS);
	}

	public String getBundleProvider() {
		return (String) getHeaderValue(BUNDLE_PROVIDER);
	}

	public ExtraFunc getBundleExtraFunc() {
		return (ExtraFunc) getHeaderValue(BUNDLE_EXTRAFUNC);
	}

	@SuppressWarnings("unchecked")
	public List<ProvidingTypeHeaderEntry> getProvideInterfaces() {
		if (getHeaderValue(PROVIDE_SERVICES) == null)
			return (List<ProvidingTypeHeaderEntry>) getHeaderValue(PROVIDE_INTERFACES);
		else
			return (List<ProvidingTypeHeaderEntry>) getHeaderValue(PROVIDE_SERVICES);
	}

	@SuppressWarnings("unchecked")
	public List<ProvidingTypeHeaderEntry> getProvidePackages() {
		return (List<ProvidingTypeHeaderEntry>) getHeaderValue(PROVIDE_PACKAGES);
	}

	@SuppressWarnings("unchecked")
	public List<RequiringTypeHeaderEntry> getRequireInterfaces() {
		if (getHeaderValue(REQUIRE_SERVICES) == null)
			return (List<RequiringTypeHeaderEntry>) getHeaderValue(REQUIRE_INTERFACES);
		else
			return (List<RequiringTypeHeaderEntry>) getHeaderValue(REQUIRE_SERVICES);
	}

	@SuppressWarnings("unchecked")
	public List<RequiringTypeHeaderEntry> getRequirePackages() {
		return (List<RequiringTypeHeaderEntry>) getHeaderValue(REQUIRE_PACKAGES);
	}

	@SuppressWarnings("unchecked")
	public List<ProvidingNamedTypeHeaderEntry> getGenerateEvents() {
		return (List<ProvidingNamedTypeHeaderEntry>) getHeaderValue(GENERATE_EVENTS);
	}

	@SuppressWarnings("unchecked")
	public List<RequiringNamedTypeHeaderEntry> getConsumeEvents() {
		return (List<RequiringNamedTypeHeaderEntry>) getHeaderValue(CONSUME_EVENTS);
	}

	@SuppressWarnings("unchecked")
	public List<ProvidingNamedTypeHeaderEntry> getProvideAttributes() {
		return (List<ProvidingNamedTypeHeaderEntry>) getHeaderValue(PROVIDE_ATTRIBUTES);
	}

	@SuppressWarnings("unchecked")
	public List<RequiringNamedTypeHeaderEntry> getRequireAttributes() {
		return (List<RequiringNamedTypeHeaderEntry>) getHeaderValue(REQUIRE_ATTRIBUTES);
	}

	@SuppressWarnings("unchecked")
	public List<ProvidingTypeHeaderEntry> getProvideTypes() {
		return (List<ProvidingTypeHeaderEntry>) getHeaderValue(PROVIDE_TYPES);
	}

	@SuppressWarnings("unchecked")
	public List<RequiringTypeHeaderEntry> getRequireTypes() {
		return (List<RequiringTypeHeaderEntry>) getHeaderValue(REQUIRE_TYPES);
	}

	// sets provide types if the header is not present - for provide-packages
	public void setProvideTypes(String Ps_headerValue) {
		if (getHeaderValue(PROVIDE_TYPES) == null)
			headers.add(new ManifestProvidingHeader(PROVIDE_TYPES,Ps_headerValue));
	}

	@Override
	public String toString() {
		String Fs_ret = "";

		Set<String> keys = manifestLines.keySet();
		for (String key : keys) {
			Fs_ret += key + ": ";
			Fs_ret += manifestLines.get(key);
			Fs_ret += "\n";
		}

		return Fs_ret;
	}
}
