package cz.zcu.kiv.crce.metadata;

import org.junit.*;
import static cz.zcu.kiv.crce.metadata.Type.*;

/**
 *
 * @author kalwi
 */
public class TypeTest {

    @Test
    public void valueOf() throws Exception {
        assert Type.valueOf("STRING") == STRING;
        assert Type.getValue("string") == STRING;
    }
}
