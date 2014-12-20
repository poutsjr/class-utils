package com.vh.ds.classutils.model;
     
import java.util.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="archive", namespace = "http://com.humanis/")
public class XmlWrapper<T> {
 
    private List<T> items;
 
    public XmlWrapper() {
        items = new ArrayList<T>();
    }
 
    public XmlWrapper(List<T> items) {
        this.items = items;
    }
 
    @XmlAnyElement(lax=true)
    public List<T> getItems() {
        return items;
    }
 
}