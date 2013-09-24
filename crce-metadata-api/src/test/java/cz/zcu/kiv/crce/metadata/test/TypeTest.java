package cz.zcu.kiv.crce.metadata.test;

import cz.zcu.kiv.crce.metadata.Type;
import org.junit.*;
import static cz.zcu.kiv.crce.metadata.Type.*;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class TypeTest {

    @Test
    public void valueOf() throws Exception {
        assert Type.valueOf("STRING") == STRING;
        assert Type.getValue("string") == STRING;
    }
}
