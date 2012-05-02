package cz.zcu.kiv.crce.plugin.test;

import junit.framework.TestCase;
import cz.zcu.kiv.crce.plugin.internal.MetadataIndexingResultServiceImpl;

public class MetadataIndexingResultServiceImplTest extends TestCase{

	public void testEfpIndexerResultServiceImpl(){
		
		MetadataIndexingResultServiceImpl mirs = new MetadataIndexingResultServiceImpl();

		assertEquals(true, mirs.isEmpty());
		
		String testingMessage = "This is a message for testing purpose.";
		
		mirs.addMessage(testingMessage);
		
		assertEquals(false, mirs.isEmpty());
		
		assertNotNull(mirs.getMessages());	// Initial message can not be null.
		
		String resultMessage = (mirs.getMessages())[0];
		
		boolean result = testingMessage.equals(resultMessage);
		assertEquals(true, result);			// TestingMessage and resultMessage must be same.
	}
}
