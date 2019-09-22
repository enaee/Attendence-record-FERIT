package hr.ferit.zavrsni.Models;

public class Course {

    private String name;
    private String id;
    private float P;
    private float A;
    private float L;
    private float K;
    private String type;
    private String study_programme;
    private String course;
    private int semester;


    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, String id, float p, float a, float l, float k, String type, String study_programme, String course, int semester) {
        this.name = name;
        this.id = id;
        P = p;
        A = a;
        L = l;
        K = k;
        this.type = type;
        this.study_programme = study_programme;
        this.course = course;
        this.semester = semester;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStudy_programme() {
        return study_programme;
    }

    public void setStudy_programme(String study_programme) {
        this.study_programme = study_programme;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}
