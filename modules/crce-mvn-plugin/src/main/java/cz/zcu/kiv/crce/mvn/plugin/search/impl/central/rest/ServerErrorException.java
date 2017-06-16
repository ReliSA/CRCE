package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

/**
 * Thrown when the search server returns html error instead of
 * json response.
 *
 * Created by Zdenek Vales on 15.6.2017.
 */
public class ServerErrorException extends Exception {

    public final int htmlCode;

    public ServerErrorException(int htmlCode) {
        super("Server returned response: "+htmlCode+"!");
        this.htmlCode = htmlCode;
    }
}
