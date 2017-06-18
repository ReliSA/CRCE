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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

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
        return  entry != null;
    }

    /**
     * This method adds a pom file to the root of existing jar archive. If there's already a pom in the archive,
     * it will be overwritten.
     *
     * @param pathToJar Path to an existing jar archive.
     * @param pathToPom Path to existing pom which will be added to the archive.
     * @throws IOException Thrown when bad stuff happens.
     */
    public static void addPomToJar(String pathToJar, String pathToPom) throws IOException {
        Map<String, String> env = new HashMap<>();
//        env.put("create", "true");

        // locate the jar and create file system
        URI uri = URI.create("jar:file:"+Paths.get(pathToJar).toUri().getPath());
        FileSystem jarfs = FileSystems.newFileSystem(uri,env);

        // locate other file and create filename in the archive
        Path otherFile = Paths.get(pathToPom);
        Path pathInJar = jarfs.getPath("/pom.xml");

        // copy the pom to the archive
        Files.copy(otherFile, pathInJar, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void unjar(JarInputStream jis, File dir)
        throws IOException
    {
        // Reusable buffer.
        byte[] buffer = new byte[4096];

        // Loop through JAR entries.
        for (JarEntry je = jis.getNextJarEntry();
             je != null;
             je = jis.getNextJarEntry())
        {
            if (je.getName().startsWith("/"))
            {
                throw new IOException("JAR resource cannot contain absolute paths.");
            }

            File target = new File(dir, je.getName());

            // Check to see if the JAR entry is a directory.
            if (je.isDirectory())
            {
                if (!target.exists())
                {
                    if (!target.mkdirs())
                    {
                        throw new IOException("Unable to create target directory: "
                            + target);
                    }
                }
                // Just continue since directories do not have content to copy.
                continue;
            }

            int lastIndex = je.getName().lastIndexOf('/');
            String name = (lastIndex >= 0) ?
                je.getName().substring(lastIndex + 1) : je.getName();
            String destination = (lastIndex >= 0) ?
                je.getName().substring(0, lastIndex) : "";

            // JAR files use '/', so convert it to platform separator.
            destination = destination.replace('/', File.separatorChar);
            copy(jis, dir, name, destination, buffer);
        }
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