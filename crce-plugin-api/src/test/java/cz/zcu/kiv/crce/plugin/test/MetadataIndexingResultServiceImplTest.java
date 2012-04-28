package cz.zcu.kiv.crce.plugin.test;

import junit.framework.TestCase;
import cz.zcu.kiv.crce.plugin.internal.MetadataIndexingResultServiceImpl;

public class MetadataIndexingResultServiceImplTest extends TestCase{

	public void testEfpIndexerResultServiceImpl(){
		
		MetadataIndexingResultServiceImpl mirs = new MetadataIndexingResultServiceImpl();
		
		assertNotNull(mirs.getMessage());	// Initial message can not be null.
		
		String testingMessage = "This is a message for testing purpose.";
		
		mirs.setMessage(testingMessage);
		
		String resultMessage = mirs.getMessage();
		
		boolean result = testingMessage.equals(resultMessage);
		assertEquals(true, result);			// TestingMessage and resultMessage must be same.
	}
}
