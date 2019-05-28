package cz.zcu.kiv.crce.metadata.type;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class VersionTest {

    /**
     * Regression test which validates new implementation of {@link Version#hashCode()} against the old one.
     * The old implementation was marked by FindBugs as potentially buggy and upon detailed inspection the
     * FindBUgs proved to be correct (= there was a bug in old implementation).
     *
     * For more details, see:
     * <a href="http://findbugs.sourceforge.net/bugDescriptions.html#BSHIFT_WRONG_ADD_PRIORITY">FindBugs docs</a>.
     */
    @Test
    public void testHashCode() {
        int major = 5,
            minor = 10,
            micro = 11;
        String qualifier = "qualifier";

        Version version = new Version(major,minor,micro,qualifier);

        // this is how the old hash was created
        int oldHash = major << 24 + minor << 16 + micro << 8 + qualifier.hashCode();

        // should not be same as the new hash
        int newHash = version.hashCode();

        assertNotEquals("Has codes should not be same!", oldHash, newHash);
    }
}
