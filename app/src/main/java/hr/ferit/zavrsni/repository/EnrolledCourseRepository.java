package hr.ferit.zavrsni.repository;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.zavrsni.FirebaseDB;
import hr.ferit.zavrsni.Models.EnrolledCourse;

public class EnrolledCourseRepository {


    private static EnrolledCourseRepository instance;
    private ArrayList<EnrolledCourse> enrolledCourses = new ArrayList<>();
    private FirebaseDB firebaseDB = new FirebaseDB();

    public static EnrolledCourseRepository getInstance() {
        if (instance == null) {
            instance = new EnrolledCourseRepository();
        }
        return instance;
    }

    public MutableLiveData<List<EnrolledCourse>> getEnrolledCourses() {
        MutableLiveData<List<EnrolledCourse>> data = new MutableLiveData<>();
        //TODO make Firebase class
        data.setValue(firebaseDB.getEnrolledCourses);
        return data;
    }

    private void setEnrolledCourses() {
        firebaseDB.getInstance();
    }
}
