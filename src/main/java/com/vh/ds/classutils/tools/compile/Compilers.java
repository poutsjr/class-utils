package com.vh.ds.classutils.tools.compile;

import com.google.common.io.Files;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class Compilers {

    /**
     * Private singleton instance
     */
    private static final Compilers instance = new Compilers();

    /**
     * Private default constructor
     */
    private Compilers() {
        super();
    }

    /**
     * @param files
     * @return
     */
    public synchronized static File compile(List<File> files) {

        // -- JDK compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // --  Manager for java file
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);

        // -- List of java files object used for compilation
        Iterable<? extends JavaFileObject> javaFileObjects = stdFileManager.getJavaFileObjects(files.toArray(new File[]{}));

        // -- Compilation options
        File compileFiles = Files.createTempDir();
        String[] compileOptions = new String[]{"-d", compileFiles.getAbsolutePath()};
        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);

        // -- Diagnostics of compilation result
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // -- Compilation task
        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, javaFileObjects);

        // -- Perform the compilation
        boolean status = compilerTask.call();

        if (!status) {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            }
            compileFiles = null;
        }
        try {
            stdFileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compileFiles;
    }


}
