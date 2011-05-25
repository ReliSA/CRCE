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

package cz.zcu.kiv.cosi.core.extrafuncregistryservice;

import java.net.URL;

import cz.zcu.kiv.cosi.core.bundlemetadata.extrafunc.ExtraFunc;

/**
 * 
 * This is the system bundle. It provides facility to check properties against <code>ExtraFunc</code>
 * registry and to verify, if one bundle <code>ExtraFunc</code> properties provide all required <code>ExtraFunc</code> properties
 * of another bundle. 
 * 
 * @author Vojtech Liska
 * 
 */
public interface ExtrafuncRegistryService {

	/**
	 * Checks, if P_providedExtraFunc provides all <code>ExtraFunc</code> properties required by
	 * P_requiredExtraFunc.
	 * 
	 * @param P_lhsExtraFunc ExtraFunc properties required by bundle
	 * @param P_rhsExtraFunc ExtraFunc properties provided by bundle
	 * @return <code>true</code> if P_providedExtraFunc provides all <code>ExtraFunc</code> propreties required
	 * by P_requiredExtraFunc
	 * 
	 * We want lhs := rhs  =>  lhs \subsetOrEqual rhs
	 */
	public boolean checkExtraFuncMatch(ExtraFunc P_lhsExtraFunc, ExtraFunc P_rhsExtraFunc);
	
	/**
	 * Checks P_extraFunc <code>ExtraFunc</code> properties against registry (checks types, values, etc).
	 * @param P_extrafunc <code>ExtraFunc</code> properties to check
	 * @return <code>true</code> if check was ok
	 */
	public boolean checkExtraFuncValid(ExtraFunc P_extrafunc);
	
	/**
	 * Loads registry from different file than default.
	 * @param P_url URL which contains registry
	 * @throws Exception if something goes wrong
	 */
	public void loadRegistryFromURL(URL P_url) throws Exception;
	
}
