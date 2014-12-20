package com.vh.ds.classutils.tools.xml.pojo;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "client", propOrder = {
        "numClient",
        "personne",
        "contrats"
})
@XmlRootElement
public class Client implements Serializable {

    private final static long serialVersionUID = 1L;


    protected String numClient;
    protected Personne personne;

    public Contrat[] getContrats() {
        return contrats;
    }

    public void setContrats(Contrat[] contrats) {
        this.contrats = contrats;
    }

    protected Contrat[] contrats;

    public String getNumClient() {
        return numClient;
    }

    public void setNumClient(String numClient) {
        this.numClient = numClient;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }
}
