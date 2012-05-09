package cz.zcu.kiv.crce.plugin.test;

import junit.framework.TestCase;
import cz.zcu.kiv.crce.plugin.internal.MetadataIndexingResultServiceImpl;

/**
 * MetadataIndexingResultServiceImplTest class provides JUnit test of MetadataIndexingResultServiceImp.
 */
public class MetadataIndexingResultServiceImplTest extends TestCase {

    /**
     * The method performs some basic operations,
     * which verifies the expected reaction
     * of MetadataIndexingResultServiceImp implementation.
     */
    public final void testEfpIndexerResultServiceImpl() {

        MetadataIndexingResultServiceImpl mirs = new MetadataIndexingResultServiceImpl();

        assertEquals(true, mirs.isEmpty());

        String testingMessage1 = "This is a message1 for testing purpose.";
        String testingMessage2 = "Testing Message #2.";

        mirs.addMessage(testingMessage1);
        mirs.addMessage(testingMessage2);

        assertEquals(false, mirs.isEmpty());	// MetadataIndexingResultService is not empty.

        String[] messageArray = mirs.getMessages();

        assertNotNull(messageArray);			// Message array can not be null.


        // This MetadataIndexingResultServiceImpl implementation returns a string messages
        // in the same order as the input, but in general it is not necessary at all.
        boolean result = testingMessage1.equals(messageArray[0]);
        assertEquals(true, result);				// TestingMessage1 and messageArray[0] must be the same.
        boolean result2 = testingMessage2.equals(messageArray[1]);
        assertEquals(true, result2);			// TestingMessage2 and messageArray[1] must be the same.

        mirs.removeAllMessages();

        assertEquals(true, mirs.isEmpty());		// MetadataIndexingResultService after removeAllMessages() is empty again.

        assertNull(mirs.getMessages());
    }
}
