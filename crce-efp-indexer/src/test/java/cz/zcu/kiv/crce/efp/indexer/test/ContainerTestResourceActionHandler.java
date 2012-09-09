package cz.zcu.kiv.crce.efp.indexer.test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Resource;

import org.osgi.service.log.LogService;

import junit.framework.TestCase;

import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;

/**
 * Testing class for ResourceActionHandler class.
 */
public class ContainerTestResourceActionHandler extends TestCase {

	/** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
	private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

	/** For creating resource from META file. */
	private volatile ResourceDAO resourceDao;

	/** For creating a new resource. */
	private volatile ResourceCreator resCreator;
	

	/**
	 * Constructor.
	 * @param rdao For creating resource from META file.
	 * @param resCreator For creating a new resource.
	 */
	public ContainerTestResourceActionHandler(final ResourceDAO rdao, final ResourceCreator resCreator) {
		this.resourceDao = rdao;
		this.resCreator = resCreator;
	}

	/**
	 * Initial method for testing the handleNewResource(Resource resource) method.
	 */
	public final void testHandleNewResource() {

		File fil = new File(dctp.PATH_TO_ARTIFACT_CORRESPONDING_TO_THE_META_FILE);

		Resource res4Test = resCreator.createResource();
		res4Test.setUri(dctp.getUri(fil.getAbsolutePath()));

		dctp.getRah().handleNewResource(res4Test);
		// This is tested method.

		File filMeta = new File(dctp.PATH_TO_META);
		Resource resFromMeta = null;
		boolean success = false;
		try {
			resFromMeta = resourceDao.getResource(dctp.getUri(filMeta.getAbsolutePath()));
			// Resource which is created by methods of crce-metadata-metafile module.
			success = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			dctp.getTestLogService().log(LogService.LOG_ERROR, "IOException during processing URI path to META file.");
		} finally{
			assertTrue(success);
		}

		//displayResourceObrMetadata(resFromMeta);
		//displayResourceObrMetadata(res4Test);

		compareRequirements(resFromMeta, res4Test);
		compareCapabilities(resFromMeta, res4Test);
	}

	//=================================================================================
	//		Auxiliary methods with non test prefix called from testHandleNewResource():
	//=================================================================================

	/**
	 * Method ensures comparing Requirement metadata of two given resources.
	 * @param resFromMeta - Resource which was created from META file by crce-metadata-metafile module.
	 * @param res4Test - Resource which was created by methods of crce-efp-indexer module.
	 */
	public final void compareRequirements(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n\ncompareRequirements:");
		Requirement [] reqyMeta = resFromMeta.getRequirements();
		Requirement [] reqy4Test = res4Test.getRequirements();

		// Resources in testing process should have positive number of Requirements.
		assertTrue(reqyMeta.length > 0);
		assertTrue(reqy4Test.length > 0);

		int i = 0;
		for (Requirement req : reqy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Requirement req2 : reqyMeta) {
				i2++;
				if (req.getFilter().equals(req2.getFilter()) == true) {
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "agree: " + i + " a " + i2);
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req.getFilter());
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req2.getFilter() + "\n");
					match = true;
					break;
				}
				//else
				//System.out.println("disagree");
			}
			if(match == false) {
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Requirement has no match: "+req.getFilter());
			}
			assertEquals(true, match);
		}
	}

	/**
	 * Method ensures comparing Capability metadata of two given resources.
	 * @param resFromMeta - Resource which was created from META file by crce-metadata-metafile module.
	 * @param res4Test - Resource which was created by methods of crce-efp-indexer module.
	 */
	public final void compareCapabilities(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n\ncompareCapabilities:");
		Capability [] capyMeta = resFromMeta.getCapabilities();
		Capability [] capy4Test = res4Test.getCapabilities();

		// Resources in testing process should have positive number of Capabilities.
		assertTrue(capyMeta.length > 0);
		assertTrue(capy4Test.length > 0);

		int i = 0;
		for (Capability cap : capy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Capability cap2 : capyMeta) {
				i2++;
				if (compareCapaProperties(cap2, cap) == true) {
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "agree: " + i + " a " + i2);
					displayCapDataAt1Line(cap);
					match = true;
					break;
				}
			}
			if(match == false) {
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Capability has no match: "+cap.getPropertyString("efp-name"));
				displayCapDataAt1Line(cap);
			}
			assertEquals(true, match);
		}

	}

	/**
	 * Every Capability can have many Properties.
	 * This method is used for comparing Capability Properties of two given Capabilities.
	 * @param capFromMeta - Capability of Resource which was created from META file.
	 * @param cap4Test - Capability of Resource which was created by crce-efp-indexer module.
	 * @return Result whether two Capabilities are same (true) or not (false).
	 */
	private boolean compareCapaProperties(final Capability capFromMeta, final Capability cap4Test) {

		Property [] propyMeta = capFromMeta.getProperties();
		Property [] propy4Test = cap4Test.getProperties();

		ArrayList<Property> propListM = new ArrayList<Property>();
		for (Property propM : propyMeta){
			propListM.add(propM);
		}

		// SOME DEBUG MESSAGES OF THIS METHOD IS COMMENTED OUT
		// BECAUSE THEY ARE TOO MUCH LOW LEVEL DEBUG MESSAGES
		// AND IT IS NOT IMPORTANT TO SHOW THEM IN USUAL DEBUG CASES.
		//int i = 0;
		boolean totalMatch = true;
		for (Property propT : propy4Test) {
			//i++;

			//int i2 = 0;
			boolean match = false;
			for (Property propM : propListM) {
				//i2++;
				if (propT.getName().equals(propM.getName()) == true) {
					if (propT.getValue().equals(propM.getValue()) == true) {
						//System.out.println(propT.getName()+ "\t\t" + propT.getType() + "\t\t" +propT.getValue());
						propListM.remove(propM);
						//System.out.println("\nagree: " + i + " a " + i2);
						match = true;
						break;
					}
				}
			}
			if (match == false) {
				totalMatch = false;
			}
		}
		//if(!totalMatch)
		//System.out.println("disagree");

		return totalMatch;
	}

	/**
	 * Method displays Capability data and Properties at one line.
	 * Method is used in debug process.
	 * @param cap - Given Capability for displaying its Properties.
	 */
	private final void displayCapDataAt1Line(final Capability cap) {
		StringBuilder sb = new StringBuilder();
		sb.append("CAP=" + cap.getName());

		Property [] propy = cap.getProperties();
		for (Property prop : propy) {
			sb.append("\t" + prop.getName() + "=" + prop.getType() + "=" + prop.getValue());
		}
		dctp.getTestLogService().log(LogService.LOG_DEBUG, sb.toString() + "\n");
	}

	/**
	 * Method display Capabilities and Requirements metadata of given Resource.
	 * Method is used in debug process.
	 * @param res - given Resource.
	 */
	public final void displayResourceObrMetadata(Resource res) {

		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\nvysypData:");

		Capability [] capy = res.getCapabilities();
		for (Capability cap : capy) {

			dctp.getTestLogService().log(LogService.LOG_DEBUG, "===== Cap: " + cap.getName());

			Property [] propy = cap.getProperties();
			for (Property prop : propy) {
				dctp.getTestLogService().log(LogService.LOG_DEBUG, prop.getName() + " "
						+ prop.getType() + " " + prop.getValue());
			}
		}

		Requirement[] reqy = res.getRequirements();
		for (Requirement cap : reqy) {
			dctp.getTestLogService().log(LogService.LOG_DEBUG, "===== REQ: " + cap.getName());
			dctp.getTestLogService().log(LogService.LOG_DEBUG, cap.getFilter());
		}

		dctp.getTestLogService().log(LogService.LOG_DEBUG, capy.length + "");
		dctp.getTestLogService().log(LogService.LOG_DEBUG, reqy.length + "");
	}


}