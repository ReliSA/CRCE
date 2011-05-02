package cz.zcu.kiv.crce.metadata.metafile.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.metadata.dao.AbstractRepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAOFactory;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.plugin.PluginManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.ace.obr.metadata.MetadataGenerator;
import org.codehaus.plexus.util.FileUtils;

import org.osgi.service.log.LogService;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class MetafileRepositoryDAO extends AbstractRepositoryDAO implements MetadataGenerator, RepositoryDAO {

    private static final String INDEX_FILENAME = "repository";
    private static final String INDEX_EXTENSION = ".xml";
    
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile DataModelHelperExt m_helper;       /* injected by dependency manager */

    @Override
    public void generateMetadata(File directory) throws IOException {
        saveRepository(getRepository(directory.toURI()));
    }

    @Override
    public WritableRepository getRepository(URI uri) throws IOException {
        if (!"file".equals(uri.getScheme())) {
            throw new UnsupportedOperationException("Other URI schemes than 'file' are not supported yet.");
        }
        
        File directory = new File(uri);
        
        if (!directory.isDirectory()) {
            throw new IOException("File is not a directory: " + directory.getAbsolutePath());
        }
        
        /* Do not use MetafileResourceDAO directly, it could be included in other
         * ResourceDAO or ResourceDAOFactory implementation like
         * CombinedResourceMetadataDAOFactory.
         * 
         * 
         * TODO direct or indirect using of MetafileResourceDAO could be
         * parametrized (e.g. via ManagedService) for these possible scenarios:
         * 
         * 1) indirect use via CombinedResourceMetadataDAOFactory
         * - OBR metafile contains custom metadata only
         * - static metadata must be indexed by indexers
         * 
         * 2) direct use
         * - OBR metafile contains both static and custom metadata
         * - no need to index static metadata
         * 
         */
        
        ResourceDAOFactory factory = m_pluginManager.getPlugin(ResourceDAOFactory.class);
        ResourceDAO rdao;
        if (factory == null) {
            rdao = m_pluginManager.getPlugin(ResourceDAO.class);
        } else {
            rdao = factory.getResourceDAO();
        }

        WritableRepository repository = m_resourceCreator.createRepository(uri);
        recurse(repository, directory, rdao);
        
        return repository;
        
    }

    @Override
    public void saveRepository(Repository repository) throws IOException {
        if (!"file".equals(repository.getURI().getScheme())) {
            throw new UnsupportedOperationException("Other URI schemes than 'file' are not supported yet.");
        }

        File directory = new File(repository.getURI());
        if (!directory.isDirectory()) {
            throw new IOException("File is not a directory: " + directory.getAbsolutePath());
        }
        
        Resource[] sorted = repository.getResources();
        
        // sort by name and version
        Arrays.sort(sorted, new Comparator<Resource>() {

            public int compare(Resource r1, Resource r2) {
                int cmp = getName(r1).compareTo(getName(r2));
                return cmp != 0 ? cmp : r1.getVersion().compareTo(r2.getVersion());
            }

            private String getName(Resource r) {
                String name = r.getSymbolicName();
                return name == null ? "" : name;
            }
        });
        
        File tempIndex;
        try {
            tempIndex = File.createTempFile("repo", INDEX_EXTENSION, directory);
        } catch (IOException e) {
            m_log.log(LogService.LOG_ERROR, "Unable to create temporary file for new repository index", e);
            throw e;
        }
        
        Writer writer = new FileWriter(tempIndex);
        try {
            m_helper.writeRepository(repository, writer);
        } finally {
            try {
                writer.close();
            } catch(Exception e) { /* nothing */ }
        }
        
        File index = new File(directory, INDEX_FILENAME + INDEX_EXTENSION);
        
        boolean success = false;
        try {
            for (int i = 0; !success && i < 10; i++) {
                try {
                    FileUtils.rename(tempIndex, index);
                    success = true;
                } catch (IOException e) {
                    success = false;
                    Thread.sleep(1000);
                }
            }
            if (!success) {
                m_log.log(LogService.LOG_ERROR, "Unable to move new repository index to it's final location");
                throw new IOException("Could not move temporary index file (" + tempIndex.getAbsolutePath() + ") to it's final location (" + index.getAbsolutePath() + ")");
            }

        } catch (InterruptedException e) {
            m_log.log(LogService.LOG_ERROR, "Waiting for next attempt to move temporary repository index failed", e);
        }
    }

    private void recurse(WritableRepository repository, File artifact, ResourceDAO rdao) {
        if (artifact.isDirectory()) {
            String list[] = artifact.list();
            for (int i = 0; i < list.length; i++) {
                recurse(repository, new File(artifact, list[i]), rdao);
            }
        } else {
            if (!artifact.getName().endsWith(MetafileResourceDAO.METAFILE_EXTENSION) && !artifact.getName().equals(INDEX_FILENAME + INDEX_EXTENSION)) {
                Resource resource;
                try {
                    resource = rdao.getResource(artifact.toURI());
                } catch (IOException e) {
                    m_log.log(LogService.LOG_ERROR, "Can not index resource " + artifact.getAbsolutePath(), e);
                    return;
                }

                repository.addResource(resource);
            }
        }
    }

}
