/**
 * This package defines the API for store of results of tests running on
 * resources.
 * 
 * <p> Every plugin can set up capabilities or requirements to the resource it
 * runs on, e.g. some test on bundle. If there is a result file of such test
 * which can not be stored in capability or requirement (e.g. a big log file),
 * it can be stored within the <code>ResultsStore</code> as a basis for
 * capabilities or requirements that were set up. These capabilities and
 * requirements contain only the relevant summary of the test (e.g. property
 * memory.consumption = 10 or test.passed = true).
 * 
 * <p> The results file stored within the results store is associated with
 * corresponding capability or requirement of the tested resource and these
 * capabilities or requirements are automatically marked by the results store to
 * be able to find the results file in results store. Each results file is also
 * associated with the plugin that generated it and set up capabilities or
 * requirements.
 * 
 */
package cz.zcu.kiv.crce.results;