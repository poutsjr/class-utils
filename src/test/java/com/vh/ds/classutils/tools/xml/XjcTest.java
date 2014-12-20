package com.vh.ds.classutils.tools.xml;

import com.sun.codemodel.JCodeModel;
import com.vh.ds.classutils.model.ClassModel;
import com.vh.ds.classutils.model.DataType;
import com.vh.ds.classutils.model.Field;
import com.vh.ds.classutils.tools.xml.pojo.Adresse;
import com.vh.ds.classutils.tools.xml.pojo.Client;
import com.vh.ds.classutils.tools.xml.pojo.Contrat;
import com.vh.ds.classutils.tools.xml.pojo.Personne;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class XjcTest {

    @Test
    public void loadByClassModel() throws Exception {
        String packageName = "simple.pack";
        String classname = "person";
        ClassModel classModel = newClassModel(packageName, classname);
        File temporaryFile = XjcUtils.generateFile(classModel);
        JCodeModel codeModel = XjcUtils.generateCode(temporaryFile, packageName);
        XjcUtils.loadModel(codeModel);
        Class personClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize(classname));
        Assert.assertTrue(personClazz.isAnnotationPresent(XmlRootElement.class));
        Assert.assertTrue(ArrayUtils.contains(personClazz.getInterfaces(), Serializable.class));
        Assert.assertEquals(Class.forName(personClazz.getName()), personClazz);
        temporaryFile.deleteOnExit();
        Assert.assertEquals(codeModel._getClass(packageName + "." + StringUtils.capitalize(classname)).fields().entrySet().size(), personClazz.getDeclaredFields().length);
    }

    @Test
    public void loadByGuichetUniqueXsd() throws Exception {
        String xsd = "schemaGuichetUnique.xsd";
        String packageName = "esb.echanges.guichetunique.xsd";
        JCodeModel codeModel = XjcUtils.generateCode(new File(getClass().getClassLoader().getResource(xsd).toURI()), packageName);
        XjcUtils.loadModel(codeModel);
        Class metierAppelClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("metierAppel"));
        Assert.assertSame(metierAppelClazz, Class.forName(metierAppelClazz.getName()));
        Assert.assertSame(metierAppelClazz, XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("metierAppel")));
        Assert.assertTrue(metierAppelClazz.isEnum());
        Class entrepriseClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("entreprise"));
        Assert.assertTrue(entrepriseClazz.isAnnotationPresent(XmlRootElement.class));
        Assert.assertTrue(ArrayUtils.contains(entrepriseClazz.getInterfaces(), Serializable.class));
        Assert.assertEquals(Class.forName(entrepriseClazz.getName()), entrepriseClazz);
        Assert.assertEquals(codeModel._getClass(packageName + "." + StringUtils.capitalize("entreprise")).fields().entrySet().size(), entrepriseClazz.getDeclaredFields().length);

        //Collection
        Class  appelRetraiteClazz = Class.forName(packageName + ".AppelRetraite");
        Object appelRetraite = appelRetraiteClazz.newInstance();
        appelRetraiteClazz.getDeclaredField("lignesAppel").setAccessible(true);
        List list = (List)appelRetraiteClazz.getDeclaredMethod("getLignesAppel").invoke(appelRetraite, null);
        list.add(Class.forName(packageName + ".LigneAppelRetraite").newInstance());
        list.add(Class.forName(packageName + ".LigneAppelRetraite").newInstance());
        Assert.assertEquals(2, ((List)appelRetraiteClazz.getDeclaredMethod("getLignesAppel").invoke(appelRetraite, null)).size());
        File f = XjcUtils.serializeIntoXml(Arrays.asList(appelRetraite), appelRetraiteClazz);
        Assert.assertNotNull(f);
    }
    
    @Test
    public void loadByXsdDucs() throws Exception {
        String xsd = "DtoDucsOrigineArchivage.xsd";
        String packageName = "esb.echanges.guichetunique.xsd";
        JCodeModel codeModel = XjcUtils.generateCode(new File(getClass().getClassLoader().getResource(xsd).toURI()), packageName);
        XjcUtils.loadModel(codeModel);
        Class dtoDucsOrigine = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("dtoDucsOrigineArchivage"));
        Assert.assertSame(dtoDucsOrigine, Class.forName(dtoDucsOrigine.getName()));
        Assert.assertSame(dtoDucsOrigine, XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("dtoDucsOrigineArchivage")));
        Class personneMoraleClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("dtoPersonneMorale"));
        Assert.assertTrue(ArrayUtils.contains(personneMoraleClazz.getInterfaces(), Serializable.class));
        Assert.assertEquals(Class.forName(personneMoraleClazz.getName()), personneMoraleClazz);
    }

    @Test
    public void loadByLivraisonXsd() throws Exception {
        String packageName = "jms.commons.dto";
        String xsd = "schemaLivraison.xsd";
        JCodeModel codeModel = XjcUtils.generateCode(new File(getClass().getClassLoader().getResource(xsd).toURI()), packageName);
        XjcUtils.loadModel(codeModel);
        Class livraisonMessageClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("livraisonMessage"));
        Assert.assertTrue(livraisonMessageClazz.isAnnotationPresent(XmlRootElement.class));
        Assert.assertTrue(ArrayUtils.contains(livraisonMessageClazz.getInterfaces(), Serializable.class));
        Assert.assertEquals(Class.forName(livraisonMessageClazz.getName()), livraisonMessageClazz);
        Class prospectMessageClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("prospectMessage"));
        Assert.assertTrue(prospectMessageClazz.isAnnotationPresent(XmlRootElement.class));
        Assert.assertTrue(ArrayUtils.contains(prospectMessageClazz.getInterfaces(), Serializable.class));
        Assert.assertEquals(Class.forName(prospectMessageClazz.getName()), prospectMessageClazz);
        Assert.assertTrue(codeModel._getClass(packageName + "." + StringUtils.capitalize("livraisonMessage"))._implements().hasNext());
        Assert.assertEquals(codeModel._getClass(packageName + "." + StringUtils.capitalize("livraisonMessage")).fields().entrySet().size(), livraisonMessageClazz.getDeclaredFields().length);
        Assert.assertEquals(codeModel._getClass(packageName + "." + StringUtils.capitalize("prospectMessage")).fields().entrySet().size(), prospectMessageClazz.getDeclaredFields().length);
    }

    @Test
	@Ignore
    public void loadAndExtractByLivraisonXsd() throws Exception {
        String packageName = "jms.commons.dto";
        String xsd = "schemaLivraison.xsd";
        XjcUtils.loadModel(new File(getClass().getClassLoader().getResource(xsd).toURI()), packageName);
        Class livraisonMessageClazz = XjcUtils.loadClass(packageName + "." + StringUtils.capitalize("livraisonMessage"));
        List livraisons = XjcUtils.extractFromXml(new File(this.getClass().getResource("/100_livraisons.xml").getFile()), livraisonMessageClazz);
        Assert.assertTrue(livraisons.get(0).getClass().equals(livraisonMessageClazz));
        Assert.assertNotNull(livraisons);
        Assert.assertEquals(100, livraisons.size());
    }

    @Test
    public void extractFromXml() throws Exception {
        List<Personne> personnes = XjcUtils.extractFromXml(new File(this.getClass().getResource("/50_personnes.xml").getFile()), Personne.class);
        Assert.assertNotNull(personnes);
        Assert.assertEquals(50, personnes.size());
    }

    @Test
    public void serializeIntoXml() throws Exception {
        List<Personne> personnes = newPersonnes();
        File f = XjcUtils.serializeIntoXml(personnes, Personne.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(personnes.size(), XjcUtils.extractFromXml(f, Personne.class).size());
    }

    @Test
    public void serializeIntoXml2() throws Exception {
        List<Client> clients = newClients();
        File f = XjcUtils.serializeIntoXml(clients, Client.class);
        Assert.assertNotNull(f);
        Assert.assertEquals(clients.size(), XjcUtils.extractFromXml(f, Personne.class).size());
    }

    private static List<Client> newClients() {
        Random r = new Random();
        List<Client> clients = new ArrayList<Client>();
        for (int i = 0; i < 3; i++) {
            Client client = new Client();
            Personne p = new Personne();
            Adresse a = new Adresse();
            a.setCp(Integer.toString(i));
            a.setNumero(Integer.toString(r.nextInt(100)));
            a.setRue("rue Vauban");
            a.setVille("LILLE");
            p.setAdresse(a);
            p.setNom("DUPONT");
            p.setPrenom("Jean");
            client.setContrats(newContrats());
            client.setPersonne(p);
            client.setNumClient(Integer.toString(100000000 + r.nextInt(100000000 - 1)));
            clients.add(client);
        }
        return clients;
    }

    private static Contrat[] newContrats() {
        Random r = new Random();
        Contrat[] contrats = new Contrat[r.nextInt(5)];
        for (int i = 0; i < contrats.length; i++) {
            Contrat contrat = new Contrat();
            contrat.setDateDebut(new Date());
            contrat.setPrix(r.nextInt());
            contrat.setNumero(Integer.toString(100000000 + r.nextInt(100000000 - 1)));
            contrats[i] = contrat;
        }
        return contrats;
    }

    private static List<Personne> newPersonnes() {
        Random r = new Random();
        List<Personne> personnes = new ArrayList<Personne>();
        for (int i = 0; i < 100; i++) {
            Personne p = new Personne();
            Adresse a = new Adresse();
            a.setCp(Integer.toString(i));
            a.setNumero(Integer.toString(r.nextInt(100)));
            a.setRue("rue Vauban");
            a.setVille("LILLE");
            p.setAdresse(a);
            p.setNom("DUPONT");
            p.setPrenom("Jean");
            personnes.add(p);
        }
        return personnes;
    }

    private static ClassModel newClassModel(String packageName, String classname) {
        ClassModel model = new ClassModel();
        model.setFields(newFields());
        model.setModelName(classname);
        model.setPackageName(packageName);

        return model;
    }

    private static Field[] newFields() {
        Field firstname = new Field(0, "prenom", "firstname", DataType.STRING);
        Field lastname = new Field(1, "nom", "lastname", DataType.STRING);
        Field phone = new Field(2, "telephone", "phone", DataType.STRING);
        Field numAdrress = new Field(3, "numero", "numAddress", DataType.INTEGER);
        Field street = new Field(4, "rue", "street", DataType.STRING);
        Field zipcode = new Field(5, "CP", "zipcode", DataType.STRING);
        Field city = new Field(6, "ville", "city", DataType.STRING);
        Field male = new Field(7, "masculin", "male", DataType.BOOLEAN);
        return new Field[]{firstname, lastname, phone, numAdrress, street, zipcode, city, male};
    }


}
