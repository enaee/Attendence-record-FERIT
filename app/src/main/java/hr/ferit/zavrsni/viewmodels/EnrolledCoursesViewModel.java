package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import hr.ferit.zavrsni.Models.EnrolledCourse;

public class EnrolledCoursesViewModel extends ViewModel {

    private MutableLiveData<List<EnrolledCourse>> mEnrolledCourses;

    public void init() {
    }

    public LiveData<List<EnrolledCourse>> getEnrolledCourses() {
        return mEnrolledCourses;
    }
}
