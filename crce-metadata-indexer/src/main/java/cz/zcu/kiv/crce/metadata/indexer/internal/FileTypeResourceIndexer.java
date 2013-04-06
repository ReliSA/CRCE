package cz.zcu.kiv.crce.metadata.indexer.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper;

/**
 * A samle implementation of <code>ResourceIndexer</code> which can determine
 * a file type. It supports ZIP, PNG and JPG files.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class FileTypeResourceIndexer extends AbstractResourceIndexer {

    private static final int BUFFER_LENGTH = 8;

    private static final String PNG = new String(new byte[]  {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
                                                        (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A});
    private static final String JPEG = new String(new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
    private static final String ZIP = new String(new byte[]  {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});

    private static final Logger logger = LoggerFactory.getLogger(FileTypeResourceIndexer.class);

    private volatile ResourceFactory resourceFactory;

    @Override
    public List<String> index(InputStream input, Resource resource) {
        byte[] buffer = new byte[BUFFER_LENGTH];

        int read = 0;
        try {
            read = input.read(buffer);
            input.close(); // TODO close input stream by its creator.
        } catch (IOException e) {
            logger.error("Could not index resource.", e);
            return Collections.emptyList();
        }

        if (read != BUFFER_LENGTH) {
            return Collections.emptyList();
        }

        String str = new String(buffer);

        if (str.startsWith(ZIP)) {
            LegacyMetadataHelper.addCategory(resourceFactory, resource, "zip");
            return Collections.singletonList("zip");
        } else if (str.startsWith(JPEG)) {
            LegacyMetadataHelper.addCategory(resourceFactory, resource, "jpeg");
            return Collections.singletonList("jpeg");
        } else if (str.startsWith(PNG)) {
            LegacyMetadataHelper.addCategory(resourceFactory, resource, "png");
            return Collections.singletonList("png");
        }

        return Collections.emptyList();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public List<String> getProvidedCategories() {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, "zip", "jpeg", "png");
        return result;
    }

}
