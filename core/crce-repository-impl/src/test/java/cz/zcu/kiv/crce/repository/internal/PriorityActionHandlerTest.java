package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.repository.filebased.internal.PriorityActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

import org.junit.*;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PriorityActionHandlerTest {

    /*
     * This test can detect mismatch declaration of reflective methods in PriorityActionHandler.
     * @throws Exception 
     */
    @Test
    public void testInstantiation() throws Exception {
        try {
            ActionHandler handler = new PriorityActionHandler();
        } catch (Throwable e) {
            assert false : e.getCause().getMessage();
        }
    }
}
