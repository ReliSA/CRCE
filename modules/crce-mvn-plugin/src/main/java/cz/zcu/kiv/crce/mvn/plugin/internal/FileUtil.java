/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cz.zcu.kiv.crce.mvn.plugin.internal;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * This class is used instead of FileUtil from felix bundlerepository which somehow causes errors when running crce.
 */
@SuppressWarnings("PMD")
public class FileUtil
{


    /**
     * Checks whether a file is present in existing jar archive.
     *
     * @param pathToJar Path to an existing jar archive.
     * @param filename Full name of the file (without the initial '/') to be checked.
     * @return True or false.
     *
     * @throws IOException
     */
    public static boolean isFilePresentInJar(String pathToJar, String filename) throws IOException {
        JarFile jar = new JarFile(pathToJar);
        JarEntry entry = jar.getJarEntry(filename);
        boolean res = entry != null;
        jar.close();
        return res;
    }

    /**
     * This method adds a pom file to the root of existing jar archive. If there's already a pom in the archive,
     * it will be overwritten.
     *
     * @param pathToJar Path to an existing jar archive.
     * @param pomInputStrem Stream containing the pom.xml file.
     * @throws IOException Thrown when bad stuff happens.
     */
    public static void addPomToJar(String pathToJar, InputStream pomInputStrem) throws IOException {
        // apparently this method shown here: http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html
        // doesn't work at all, but the solution below works just fine

        // open jar file
        FileOutputStream out = new FileOutputStream(pathToJar);
        JarOutputStream jarOut = new JarOutputStream(out);

        // create new entry
        JarEntry pomEntry = new JarEntry("pom.xml");
        jarOut.putNextEntry(pomEntry);

        // write pom file to archive
        while(pomInputStrem.available() > 0) {
            jarOut.write(pomInputStrem.read());
        }
        jarOut.close();
        out.close();
    }

    /**
     * Extracts the contents of jar file to the targetDir. If the targetDir doesn't exist
     * it will be created.
     *
     * @param jarFileName Name of the jar file.
     * @param targetDir Target directory.
     */
    public static void unJar(String jarFileName, String targetDir) throws IOException, FileUtilOperationException {
        JarFile jar = new JarFile(jarFileName);
        Enumeration<JarEntry> enumEntries = jar.entries();

        // check the target dir
        File target = new File(targetDir);
        if(!target.exists()) {
            // not really necessary at all, but findbugs made me
            if(!target.mkdir()) {
                throw new FileUtilOperationException("Failed to create target directory for extracting a jar archive.");
            }
        }

        while(enumEntries.hasMoreElements()) {
            JarEntry entry = enumEntries.nextElement();
            File f = new File(targetDir + File.separator + entry.getName());
            if(entry.isDirectory()) {
                if(!f.mkdir()) {
                    throw new FileUtilOperationException("Failed to create an extracted directory.");
                }
                continue;
            }

            InputStream is = jar.getInputStream(entry);
            try (OutputStream out = new FileOutputStream(f)) {
                while(is.available() > 0) {
                    out.write(is.read());
                }
            }
            is.close();
        }
        jar.close();
    }

    public static void copy(
        InputStream is, File dir, String destName, String destDir, byte[] buffer)
        throws IOException
    {
        if (destDir == null)
        {
            destDir = "";
        }

        // Make sure the target directory exists and
        // that is actually a directory.
        File targetDir = new File(dir, destDir);
        if (!targetDir.exists())
        {
            if (!targetDir.mkdirs())
            {
                throw new IOException("Unable to create target directory: "
                    + targetDir);
            }
        }
        else if (!targetDir.isDirectory())
        {
            throw new IOException("Target is not a directory: "
                + targetDir);
        }

        BufferedOutputStream bos = new BufferedOutputStream(
            new FileOutputStream(new File(targetDir, destName)));
        int count = 0;
        while ((count = is.read(buffer)) > 0)
        {
            bos.write(buffer, 0, count);
        }
        bos.close();
    }

    public static void setProxyAuth(URLConnection conn) throws IOException
    {
        // Support for http proxy authentication
        String auth = System.getProperty("http.proxyAuth");
        if ((auth != null) && (auth.length() > 0))
        {
            if ("http".equals(conn.getURL().getProtocol())
                || "https".equals(conn.getURL().getProtocol()))
            {
                String base64 = Base64Encoder.base64Encode(auth);
                conn.setRequestProperty("Proxy-Authorization", "Basic " + base64);
            }
        }

    }

    public static InputStream openURL(final URL url) throws IOException
    {
        // Do it the manual way to have a chance to
        // set request properties as proxy auth (EW).
        return openURL(url.openConnection());
    }

    public static InputStream openURL(final URLConnection conn) throws IOException
    {
        // Do it the manual way to have a chance to
        // set request properties as proxy auth (EW).
        setProxyAuth(conn);
        try
        {
            return conn.getInputStream();
        }
        catch (IOException e)
        {
            // Rather than just throwing the original exception, we wrap it
            // because in some cases the original exception doesn't include
            // the full URL (see FELIX-2912).
            URL url = conn.getURL();
            IOException newException = new IOException("Error accessing " + url);
            newException.initCause(e);
            throw newException;
        }
    }
}