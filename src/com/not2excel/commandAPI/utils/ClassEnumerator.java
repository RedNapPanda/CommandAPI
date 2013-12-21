package com.not2excel.commandAPI.utils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Recursive idea based off ClassEnumerator by Dave Dopson
 * (used the recursion idea from him to loop through all inner directories as well)
 * I had a very inefficient method of looping through inner directories.
 * When I saw Dave Dopson's ClassEnumerator I realized I could have just used simple recursion to efficiently loop.
 *
 * @author Richmond Steele
 * @since 8/28/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class ClassEnumerator
{
    /**
     * Singleton instance
     */
    private static volatile ClassEnumerator instance;

    /**
     * Returns the singleton instance
     * creates one if the instance is null
     *
     * @return instance
     */
    public static ClassEnumerator getInstance()
    {
        if (instance == null) ;
        { instance = new ClassEnumerator(); }
        return instance;
    }

    /**
     * Parses a location for jar files and class files
     *
     * Recurses through if necessary
     *
     * @param location
     *         directory to parse
     * @return class array
     */
    @SuppressWarnings("ConstantConditions")
    public List<Class<?>> getClassesFromLocation(File location)
    {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        if(location.isDirectory())
        {
            for (File file : Arrays.asList(location.listFiles()))
            {
                try
                {
                    ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()},
                                                                 this.getClass().getClassLoader());
                    if (file.getName().toLowerCase().trim().endsWith(".class"))
                    {
                        classes.add(classLoader.loadClass(file.getName().replace(".class", "").replace("/", ".")));
                    }
                    else if (file.getName().toLowerCase().trim().endsWith(".jar"))
                    {
                        classes.addAll(getClassesFromJar(file, classLoader));
                    }
                    else if (file.isDirectory())
                    {
                        classes.addAll(getClassesFromLocation(file));
                    }
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                ClassLoader classLoader = new URLClassLoader(new URL[]{location.toURI().toURL()},
                                                             this.getClass().getClassLoader());
                if (location.getName().toLowerCase().trim().endsWith(".class"))
                {
                    classes.add(classLoader.loadClass(location.getName().replace(".class", "").replace("/", ".")));
                }
                if (location.getName().toLowerCase().trim().endsWith(".jar"))
                {
                    classes.addAll(getClassesFromJar(location, classLoader));
                }
                if (location.isDirectory())
                {
                    classes.addAll(getClassesFromLocation(location));
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return classes;
    }

    /**
     * Returns the class array of all classes within the current Running Jar
     * Utilizes the code source which could be a Jar file or could just be a directory of class files
     *
     * @return class array
     */
    public Class[] getClassesFromThisJar()
    {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        ClassLoader classLoader = null;
        URI uri = null;
        try
        {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            classLoader = new URLClassLoader(
                    new URL[]{uri.toURL()},
                    ClassEnumerator.class.getClassLoader());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        if (uri == null)
        {
            throw new RuntimeException(
                    "No uri for " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
        }
        if(classLoader == null)
        {
            throw new RuntimeException(
                    "No classLoader for " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
        }
        File file = new File(uri);
        classes.addAll(getClassesFromLocation(file));
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Returns all class files inside a jar
     *
     * @param file
     *         jar file
     * @param classLoader
     *         classloader created previously using the jar file
     * @return class list
     */
    public List<Class<?>> getClassesFromJar(File file, ClassLoader classLoader)
    {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        try
        {
            final JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements())
            {
                final JarEntry jarEntry = enumeration.nextElement();
                if (jarEntry.isDirectory() ||
                    !jarEntry.getName().toLowerCase().trim().endsWith(".class"))
                {
                    continue;
                }
                classes.add(classLoader.loadClass(jarEntry.getName().replace(".class", "").replace("/", ".")));
            }
            jarFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * Processes a directory and retrieves all classes from it and its subdirectories
     *
     * Recurses if necessary
     *
     * @deprecated Currently not used, getClassesFromLocation replaces this
     * @param directory
     *         directory file to traverse
     * @return list of classes
     */
    @Deprecated
    @SuppressWarnings({"unused", "deprecation"})
    private List<Class<?>> processDirectory(File directory, String append)
    {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        String[] files = directory.list();
        for (String fileName : files)
        {
            String className = null;
            if (fileName.endsWith(".class"))
            {
                className = append + '.' + fileName.replace(".class", "");
            }
            if (className != null)
            {
                classes.add(loadClass(className.substring(1)));
            }
            File subdir = new File(directory, fileName);
            if (subdir.isDirectory())
            {
                classes.addAll(processDirectory(subdir, append + "." + fileName));
            }
        }
        return classes;
    }

    /**
     * Loads a class based upon the name
     * Simple wrapper that catches ClassNotFoundException
     *
     * @param className
     *         name of class (.class is pre removed)
     * @return Class if it was loaded properly
     */
    private Class<?> loadClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Error loading class '" + className + "'");
        }
    }
}
