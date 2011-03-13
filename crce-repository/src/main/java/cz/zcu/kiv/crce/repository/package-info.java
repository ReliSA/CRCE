/**
 * <h1>Core repository package</h1>
 * 
 * This package contains interfaces of core repository.
 * 
 * <h2>Artifacts processing</h2>
 * 
 * Artifacts and their metadata should be processed (added/removed) in correct
 * order to ensure data/metadata consistency in case of failure (IOException).
 * So this is proposed sequence of operations:
 * 
 * <h3>Adding</h3>
 * <ol>
 * <li>Add the artifact file to the physical storage,
 * <li>generate the OBR metadata (Resource) for the artifact,
 * <li>store the OBR metadata to the physical storage,
 * <li>add the OBR metadata to the OBR repository,
 * <li>store the OBR repository to the physical storage (repository.xml)<br>
 *    (optional - OBR repository can always be recreated from the stored
 *     artifacts).
 * </ol>
 * 
 * <h3>Removing</h3>
 * <ol>
 * <li>Remove the artifact from the physical storage,
 * <li>remove the metadata from the physical storage,
 * <li>remove the metadata from the repository,
 * <li>store the repository to the physical storage<br>
 *     (optional - OBR repository can always be recreated from the stored
 *     artifacts).
 * <li>destroy the metadata (discard references).
 * </ol>
 * 
 */
package cz.zcu.kiv.crce.repository;