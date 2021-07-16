package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlTools {
    /**
     * Gets query from given path
     * 
     * @param path Path to process
     * @return Query part in path
     */
    public static String getQuery(String path) {

        try {
            URL parsed = new URL(path);
            if (parsed.getQuery() == null || parsed.getQuery().isEmpty()) {
                return null;
            }
            return parsed.getQuery();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Matrix query from path
     * 
     * @param path Path to process
     * @return Matrix part in path
     */
    public static String getMatrixQuery(String path) {
        String matrixQuery;
        try {
            URL parsed = new URL(path);
            matrixQuery = parsed.getPath();
            if (parsed.getQuery() != null) {
                matrixQuery = matrixQuery.replace("?" + parsed.getQuery(), "");
            }
            if (parsed.getPath() != null) {
                String[] matrixParams = matrixQuery.split(";");
                if (matrixParams[0].equals(matrixQuery)) {
                    return null;
                }
                return matrixQuery.replace(matrixParams[0] + ";", "");
            }
            return matrixQuery;
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
