package cz.zcu.kiv.crce.rest.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * When the client asks CRCE for data about replacement bundle versions and
 * provides identifier of a bundle version b_i which exists in the repository
 * and optionally provides a desired operation(op).
 *
 * Then CRCE sends a set of meta-data (the "core" part) of those bundle versions
 * b_r which are strictly compatible with b_i and have same provider, name as
 * b_i and optionally satisfy the operation or replies
 * "no such version found in repository".
 *
 * Note: The operation value can be
 *
 * <ul>
 * <li>upgrade [default] = b_r must have same provider, name as b_i and higher
 * version than b_i </li>
 * <li>nearest = metadata of a single bundle which has lowest r > i </li>
 * <li>highest = metadata of a single bundle which has highest r > i  </li>
 * <li>downgrade = b_r must have same provider, name as b_i and lower version than b_i  </li>
 * <li>any = b_r must have same provider, name as b_i and version of b_r
 * is different to that of b_i </li>
 * </ul>
 *
 * @author Jan Reznicek
 *
 */
public interface GetReplaceBundle {

	/**
	 * In current version return resource with same name as in id and highest possible version.
	 * @param id
     * @param op
	 * @param ui contextual info about URI
	 * @return resource
	 */
    Response replaceBundle(String id, String op, UriInfo ui);
}
