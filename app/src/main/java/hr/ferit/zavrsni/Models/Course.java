package hr.ferit.zavrsni.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Course {

    private String name;
    private String id;
    private float P;
    private float A;
    private float L;
    private float K;


    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, float p, float a, float l, float k) {
        this.name = name;
        P = p;
        A = a;
        L = l;
        K = k;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("id", id);
        result.put("p", P);
        result.put("a", A);
        result.put("l", L);
        result.put("k", K);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getP() {
        return P;
    }

    public void setP(float p) {
        P = p;
    }

    public float getA() {
        return A;
    }

    public void setA(float a) {
        A = a;
    }

    public float getL() {
        return L;
    }

    public void setL(float l) {
        L = l;
    }

    public float getK() {
        return K;
    }

    public void setK(float k) {
        K = k;
    }
}
