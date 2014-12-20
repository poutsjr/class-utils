package com.vh.ds.classutils.tools.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class FileUtils {

    private static final String JAVA_SOURCE_FILE_EXTENSION = ".java";
    private static final String JAVA_CLASS_FILE_EXTENSION = ".class";
    private static final List<String> UNPROCESSED_JAVA_FILES = Arrays.asList("package-info.java", "ObjectFactory.java");

    /**
     * Return recursively java source files of a directory.
     * @param rootDirectory
     * @return
     */
    public static List<File> listJavaSourceFiles(File rootDirectory) {
        final List<File> list = new ArrayList<File>();
        if (!rootDirectory.isDirectory()) {
            return list;
        }
        final File[] f = rootDirectory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(JAVA_SOURCE_FILE_EXTENSION) && !UNPROCESSED_JAVA_FILES.contains(pathname.getName())) {
                    return true;
                }

                if (pathname.isDirectory()) {
                    list.addAll(listJavaSourceFiles(pathname));
                }

                return false;

            }
        });

        list.addAll(Arrays.asList(f));
        return list;
    }

    public static List<File> listJavaClassFiles(File rootDirectory) {
        final List<File> list = new ArrayList<File>();
        if (!rootDirectory.isDirectory()) {
            return list;
        }
        final File[] f = rootDirectory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(JAVA_CLASS_FILE_EXTENSION)) {
                    return true;
                }

                if (pathname.isDirectory()) {
                    list.addAll(listJavaClassFiles(pathname));
                }

                return false;

            }
        });

        list.addAll(Arrays.asList(f));
        return list;
    }


}
