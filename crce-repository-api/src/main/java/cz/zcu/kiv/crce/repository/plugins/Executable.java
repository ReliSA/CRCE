package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.Store;
import java.util.List;
import java.util.Properties;

/*
 * Zmeny od schuzky 2011-03-24:
 * - dve metody pro spousteni, jedna pro spousteni nad bufferem (docasnem
 *   uploadovacim ulozisti), druha pro spousteni nad store (trvalem ulozisti);
 * - misto Repository (mnozina Resourcu dostupnych v bufferu nebo v store) se
 *   predava reference na Store, prip. i Buffer, z nich je referenci na Repository
 *   mozno ziskat, ale krome toho lze se Store i Bufferem manipulovat (napr.
 *   mazat z nej Resourcy nebo i spustit dalsi Executable plugin).
 * - pridana metoda pro zajisteni vyhradniho (neparalelniho behu).
 */
/**
 * This interface specifies the kind of plugin which can be executed at any time
 * by external event, e.g. on user's request.
 * 
 * <p> The selection of resources contains resources that are to be e.g. tested.
 * Other resources, that are not the point of execution but can be used as 
 * supporting resources, can be obtained from repository of given Store or Buffer.
 * 
 * <p>Calling of executables can be <i>asynchronous</i> so there is no guarantee
 * that the calling method will wait for the end of execution.
 * 
 * <p>Executing of Executable plugins can be threaded (in dependence on
 * implementation of Store/Buffer). If the implementation of Executable needs to
 * be run exclusively, it have to return <code>true</code> from isExclusive()
 * method. It can be useful e.g. in case of changing data of artifact (e.g.
 * manifest of bundle) - in this case it ensures that the metadata will be valid
 * during the execution of other Executable plugins.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Executable extends Plugin {

    /**
     * Executes the plugin on the selection of resources stored in <i>buffer</i>.
     * 
     * @param resources the set of resources that this plugin will run on.
     * @param buffer 
     * @param store 
     * @param properties plugin configuration properties.
     */
    void executeOnBuffer(List<Resource> resources, Store store, Buffer buffer, Properties properties);

    /**
     * Executes the plugin on the selection of resources stored in <i>store</i>.
     * 
     * @param resources the set of resources that this plugin will run on.
     * @param store
     * @param properties plugin configuration properties.
     */
    void executeOnStore(List<Resource> resources, Store store, Properties properties);

    /**
     * Returns <code>true</code> if the plugin needs to be executed exclusively
     * (no other Executables will be executed at the same time until execution
     * of this plugin finishes).
     * @return <code>true</code> if the plugin needs to be executed exclusively.
     */
    boolean isExclusive();
    
    /**
     * Returns the instance of <code>Properties</code> which can be used to
     * reconfigure this plugin for specific execution.
     * <em>Note:</em> Implementing class is responsible to clean changed properties
     * after execution if needed.
     * @return the instance of <code>Properties</code>.
     */
    Properties getProperties();
}
