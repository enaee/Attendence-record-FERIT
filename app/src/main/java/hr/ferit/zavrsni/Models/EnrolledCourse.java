package hr.ferit.zavrsni.Models;

import java.io.Serializable;
import java.util.Map;

public class EnrolledCourse implements Serializable {
    private String name;
    private String id;

    private Map<String, Float> p;
    private Map<String, Float> a;
    private Map<String, Float> l;
    private Map<String, Float> k;

    public EnrolledCourse() {
    }

    public Map<String, Float> getP() {
        return p;
    }

    public void setP(Map<String, Float> p) {
        this.p = p;
    }

    public Map<String, Float> getA() {
        return a;
    }

    public void setA(Map<String, Float> a) {
        this.a = a;
    }

    public Map<String, Float> getL() {
        return l;
    }

    public void setL(Map<String, Float> l) {
        this.l = l;
    }

    public Map<String, Float> getK() {
        return k;
    }

    public void setK(Map<String, Float> k) {
        this.k = k;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
