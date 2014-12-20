package com.vh.ds.classutils.tools.xml;

import com.google.common.io.Files;
import com.sun.codemodel.*;
import com.sun.tools.xjc.api.*;
import com.vh.ds.classutils.context.Contexts;
import com.vh.ds.classutils.model.ClassModel;
import com.vh.ds.classutils.model.XmlWrapper;
import com.vh.ds.classutils.tools.compile.Compilers;
import com.vh.ds.classutils.tools.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.*;

public class XjcUtils implements Serializable {


    private XjcUtils() {
        super();
    }

    private static final XjcUtils instance = new XjcUtils();

    /**
     * Generate codeModel (XJC API) of an XSD File
     *
     * @param schema
     * @param packageName
     * @return
     * @throws IOException
     */
    protected static JCodeModel generateCode(File schema, String packageName) throws Exception {

        SchemaCompiler sc = XJC.createSchemaCompiler();
        sc.forcePackageName(packageName);

        // -- Source of XSD file
        InputSource src = new InputSource(schema.toURI().toString());

        // -- Parse schema, bind the model and generate codeModel
        sc.parseSchema(src);
        S2JJAXBModel model = sc.bind();
        JCodeModel jCodeModel = model.generateCode(null, null);
        
        // -- Get the targetNamespace of the schema (probably not the best way to get it, but it works)
        String targetNamespace = "";
        if (model.getMappings() != null 
        		&& model.getMappings().iterator().hasNext() 
        		&& model.getMappings().iterator().next().getElement() != null) {
        	targetNamespace = model.getMappings().iterator().next().getElement().getNamespaceURI();
        }

        // -- Add additionnal bindings (serialization, xmlRootElement, ...)
        serializeModelClasses(jCodeModel);
        annotateRootElementModelClasses(jCodeModel, targetNamespace);

        // -- Return the codeModel
        return jCodeModel;
    }

    public static <T extends Object> List<T> extractFromXml(File xmlFile, Class<T> clazz) throws Exception {
        JAXBContext context = JAXBContext.newInstance(XmlWrapper.class, clazz);
        Unmarshaller um = context.createUnmarshaller();
        FileReader fileReader = null;
        XmlWrapper wrapper = null;
        try {
        	fileReader = new FileReader(xmlFile);
        	wrapper = (XmlWrapper) um.unmarshal(fileReader);
        } finally {
        	fileReader.close();
        }
        return wrapper != null ? wrapper.getItems() : null;
    }

    public static <T extends Object> File serializeIntoXml(List<T> objectsToArchive, Class<T> archiveClazz) throws Exception {

        JAXBContext context = JAXBContext.newInstance(XmlWrapper.class, archiveClazz);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        File tempFile = File.createTempFile("tmp_" + archiveClazz, ".xml");
        m.marshal(new XmlWrapper(objectsToArchive), System.out);
        m.marshal(new XmlWrapper(objectsToArchive), tempFile);
        return tempFile;
    }

    private static void annotateRootElementModelClasses(JCodeModel model, String namespace) {
        Iterator<JPackage> itPackage = model.packages();
        JAnnotationUse annotationUse = null;
        while (itPackage.hasNext()) {
            JPackage p = itPackage.next();
            Iterator<JDefinedClass> itClass = p.classes();
            while (itClass.hasNext()) {
                JDefinedClass c = itClass.next();
                annotationUse = c.annotate(XmlRootElement.class);
                annotationUse.param("namespace", namespace);
            }
        }
    }

    private static void serializeModelClasses(JCodeModel model) {
        Iterator<JPackage> itPackage = model.packages();
        while (itPackage.hasNext()) {
            JPackage p = itPackage.next();
            Iterator<JDefinedClass> itClass = p.classes();
            while (itClass.hasNext()) {
            	serializeClass(itClass.next());
            }
        }
    }
    
    private static void serializeClass(JDefinedClass clazz) {
    	clazz._implements(Serializable.class);
    	clazz.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, long.class, "serialVersionUID", JExpr.lit(1L));
    	
    	Iterator<JDefinedClass> itClass = clazz.classes();
        while (itClass.hasNext()) {
        	serializeClass(itClass.next());
        }
    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    public static File generateFile(ClassModel model) throws Exception {
        String schema = XjcUtils.getXmlSchemaValue(model);

        //create a temp file
        File xsdFile = File.createTempFile(model.getModelName(), ".xsd");

        //write it
        BufferedWriter bw = new BufferedWriter(new FileWriter(xsdFile));
        bw.write(schema);
        bw.close();
        return xsdFile;

    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    protected static String getXmlSchemaValue(ClassModel model) throws Exception {
        Map<String, ClassModel> params = new HashMap<String, ClassModel>();
        params.put("type", model);
        return populateTemplate("schema.vsl", params);
    }


    /**
     * @param templateFile
     * @param map
     * @return
     * @throws Exception
     */
    protected static String populateTemplate(String templateFile, Map<String, ?> map) throws Exception {
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(props);

        Template t = ve.getTemplate(templateFile);
        VelocityContext context = new VelocityContext(map);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    /**
     * @param codeModel
     * @return
     * @throws IOException
     */
    protected static File generateSourceFiles(JCodeModel codeModel) throws IOException {
        File temporaryDir = Files.createTempDir();
        codeModel.build(temporaryDir);
        return temporaryDir;
    }

    /**
     * @param xsd
     * @param packageName
     * @throws Exception
     */
    public static void loadModel(File xsd, String packageName) throws Exception {
        JCodeModel codeModel = XjcUtils.generateCode(xsd, packageName);
        XjcUtils.loadModel(codeModel);
    }

    /**
     * @param classModel
     * @throws Exception
     */
    public static void loadModel(ClassModel classModel) throws Exception {
        File temporaryFile = XjcUtils.generateFile(classModel);
        JCodeModel codeModel = XjcUtils.generateCode(temporaryFile, classModel.getPackageName());
        XjcUtils.loadModel(codeModel);
    }

    /**
     * @param codeModel
     * @throws Exception
     */
    protected static void loadModel(JCodeModel codeModel) throws Exception {

        // -- Generate java files to compile
        File rootDirectory = XjcUtils.generateSourceFiles(codeModel);
        List<File> files = FileUtils.listJavaSourceFiles(rootDirectory);

        // -- File compilation
        File targetDirectory = Compilers.compile(files);

        // -- Load new classloader
//		ClassLoader cl = Contexts.load(targetDirectory);
        Contexts.load(targetDirectory);

    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class loadClass(String className) throws Exception {
        return Contexts.loadClass(className);

    }


}
