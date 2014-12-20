package com.vh.ds.classutils.context;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.*;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.bytecode.ClassFile;

import com.vh.ds.classutils.tools.io.FileUtils;

/**
 *
 */
public class Contexts {

    /**
     *
     */
    private Contexts() {
        super();
    }

    /**
     *
     */
    private static final Contexts instance = new Contexts();

    /**
     *
     */
    private final List<ClassLoader> pool = new ArrayList<ClassLoader>();

    /**
     *
     */
    private final Map<String, Class> map = new HashMap<String, Class>();

    /**
     * @param targetDirectory
     * @return
     * @throws MalformedURLException
     */
    public static void load(File targetDirectory) throws Exception {
        //TODO improve counter for avoid  endless loop
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(targetDirectory + "/");
        List<File> files = FileUtils.listJavaClassFiles(targetDirectory);

        Deque<File> queue = new ArrayDeque<File>(files);
        while (!queue.isEmpty()) {
            File file = queue.removeFirst();
            ClassFile f = new ClassFile(new DataInputStream(new FileInputStream(file)));
            try {
                System.out.print("Chargement de la classe " + f.getName());
                loadClass(f.getName());
                System.out.println(" : OK");
            } catch (CannotCompileException e) {
                System.out.println(" : KO");
                if(queue.isEmpty() || !(e.getCause().getCause() instanceof ClassNotFoundException)){
                    throw e;
                }
                queue.addLast(file);
            }
        }
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class loadClass(String className) throws Exception {
        Class c;
        if (isLoaded(className)) {
            c = instance.map.get(className);
        } else {
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                c = ClassPool.getDefault().get(className).toClass();
            }
            instance.map.put(className, c);
        }
        return c;
    }

    /**
     * @param className
     * @return
     */
    public static boolean isLoaded(String className) {
        return instance.map.containsKey(className);
    }
}
