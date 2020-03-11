package cz.zcu.kiv.crce.apicomp.impl.mov;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;

/**
 * A diff that is capable of carrying additional MOV information.
 */
public class MovDiff extends DefaultDiffImpl {

    public static boolean isMovDiff(Diff d) {
        return d instanceof MovDiff && ((MovDiff)d).isMov();
    }

    private boolean mov;

    public MovDiff() {
        this(false);
    }

    public MovDiff(boolean mov) {
        this.mov = mov;
    }

    public boolean isMov() {
        return mov;
    }

    public void setMov(boolean mov) {
        this.mov = mov;
    }
}
