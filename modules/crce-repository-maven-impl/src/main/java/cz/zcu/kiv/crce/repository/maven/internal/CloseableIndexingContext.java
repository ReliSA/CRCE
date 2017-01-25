package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.artifact.GavCalculator;
import org.apache.maven.index.context.DocumentFilter;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexingContext;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CloseableIndexingContext implements IndexingContext, Closeable {

    private final IndexingContext delegate;
    private final Indexer indexer;
    private boolean closed;

    public CloseableIndexingContext(IndexingContext delegate, Indexer indexer) {
        this.delegate = delegate;
        this.indexer = indexer;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getRepositoryId() {
        return delegate.getRepositoryId();
    }

    @Override
    public File getRepository() {
        return delegate.getRepository();
    }

    @Override
    public String getRepositoryUrl() {
        return delegate.getRepositoryUrl();
    }

    @Override
    public String getIndexUpdateUrl() {
        return delegate.getIndexUpdateUrl();
    }

    @Override
    public boolean isSearchable() {
        return delegate.isSearchable();
    }

    @Override
    public void setSearchable(boolean searchable) {
        delegate.setSearchable(searchable);
    }

    @Override
    public Date getTimestamp() {
        return delegate.getTimestamp();
    }

    @Override
    public void updateTimestamp() throws IOException {
        delegate.updateTimestamp();
    }

    @Override
    public void updateTimestamp(boolean save) throws IOException {
        delegate.updateTimestamp(save);
    }

    @Override
    public void updateTimestamp(boolean save, Date date) throws IOException {
        delegate.updateTimestamp(save, date);
    }

    @Override
    public int getSize() throws IOException {
        return delegate.getSize();
    }

    @Override
    public IndexSearcher acquireIndexSearcher() throws IOException {
        return delegate.acquireIndexSearcher();
    }

    @Override
    public void releaseIndexSearcher(IndexSearcher s) throws IOException {
        delegate.releaseIndexSearcher(s);
    }

    @Override
    public IndexWriter getIndexWriter() throws IOException {
        return delegate.getIndexWriter();
    }

    @Override
    public List<IndexCreator> getIndexCreators() {
        return delegate.getIndexCreators();
    }

    @Override
    public Analyzer getAnalyzer() {
        return delegate.getAnalyzer();
    }

    @Override
    public void commit() throws IOException {
        delegate.commit();
    }

    @Override
    public void rollback() throws IOException {
        delegate.rollback();
    }

    @Override
    public void optimize() throws IOException {
        delegate.optimize();
    }

    @Override
    public void close(boolean deleteFiles) throws IOException {
        delegate.close(deleteFiles);
    }

    @Override
    public void purge() throws IOException {
        delegate.purge();
    }

    @Override
    public void merge(Directory directory) throws IOException {
        delegate.merge(directory);
    }

    @Override
    public void merge(Directory directory, DocumentFilter filter) throws IOException {
        delegate.merge(directory, filter);
    }

    @Override
    public void replace(Directory directory) throws IOException {
        delegate.replace(directory);
    }

    @Override
    public Directory getIndexDirectory() {
        return delegate.getIndexDirectory();
    }

    @Override
    public File getIndexDirectoryFile() {
        return delegate.getIndexDirectoryFile();
    }

    @Override
    public GavCalculator getGavCalculator() {
        return delegate.getGavCalculator();
    }

    @Override
    public void setAllGroups(Collection<String> groups) throws IOException {
        delegate.setAllGroups(groups);
    }

    @Override
    public Set<String> getAllGroups() throws IOException {
        return delegate.getAllGroups();
    }

    @Override
    public void setRootGroups(Collection<String> groups) throws IOException {
        delegate.setRootGroups(groups);
    }

    @Override
    public Set<String> getRootGroups() throws IOException {
        return delegate.getRootGroups();
    }

    @Override
    public void rebuildGroups() throws IOException {
        delegate.rebuildGroups();
    }

    @Override
    public boolean isReceivingUpdates() {
        return delegate.isReceivingUpdates();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            if (indexer != null) {
                indexer.closeIndexingContext(delegate, false);
            } else {
                delegate.close(true);
            }
            closed = true;
        }
    }

    public Indexer getIndexer() {
        return indexer;
    }
}
