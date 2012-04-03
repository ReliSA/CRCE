package cz.zcu.kiv.crce.efp.indexer.test;

import junit.framework.TestCase;
import cz.zcu.kiv.crce.efp.indexer.internal.EfpIndexerResultServiceImpl;

public class EfpIndexerResultServiceImplTest extends TestCase{

	public void testEfpIndexerResultServiceImpl(){
		
		EfpIndexerResultServiceImpl eirs = new EfpIndexerResultServiceImpl();
		
		assertNotNull(eirs.getMessage());	// Initial message can not be null.
		
		String testingMessage = "This is a message for testing purpose.";
		
		eirs.setMessage(testingMessage);
		
		String resultMessage = eirs.getMessage();
		
		boolean result = testingMessage.equals(resultMessage);
		assertEquals(true, result);			// TestingMessage and resultMessage must be same.
	}
}
