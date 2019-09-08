package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.repository.EnrolledCourseRepository;


public class CourseOverviewViewModel extends ViewModel {

    private MutableLiveData<EnrolledCourse> enrolledCourse;

    public LiveData<EnrolledCourse> getCourse() {
        return enrolledCourse;
    }

    private EnrolledCourseRepository repository = new EnrolledCourseRepository();

}
