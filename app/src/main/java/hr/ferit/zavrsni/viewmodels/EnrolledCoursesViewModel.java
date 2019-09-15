package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Database.FirebaseManager;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.interfaces.IEnrolledCoursesCallback;

public class EnrolledCoursesViewModel extends ViewModel implements IEnrolledCoursesCallback {


    private MutableLiveData<List<EnrolledCourse>> mEnrolledCourses;
    private Map<String, EnrolledCourse> enrolledCourses;
    private List<EnrolledCourse> enrolledCoursesList = new ArrayList<>();
    ;
    private FirebaseManager mRepository;

    public void init(String userID) {
        if (mEnrolledCourses != null) {
            return;
        }
        mEnrolledCourses = new MutableLiveData<>();
        mRepository = FirebaseManager.getInstance(userID);
        mRepository.setInterface(this);
        mRepository.addListeners();
        enrolledCourses = new HashMap<>(mRepository.getEnrolledCourses());
    }

    public LiveData<List<EnrolledCourse>> getEnrolledCourses() {
        return mEnrolledCourses;
    }


    @Override
    public void onChildAddedEnrolledCourse(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            enrolledCourses.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
        }
        enrolledCoursesList.clear();
        enrolledCoursesList.addAll(enrolledCourses.values());
        mEnrolledCourses.setValue(enrolledCoursesList);
    }

    @Override
    public void OnChildDeletedEnrolledCourse(DataSnapshot dataSnapshot) {
        enrolledCourses.remove(dataSnapshot.getKey());
        enrolledCoursesList.clear();
        enrolledCoursesList.addAll(enrolledCourses.values());
        mEnrolledCourses.setValue(enrolledCoursesList);
    }

    @Override
    public void OnChildChangedEnrolledCourse(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            enrolledCourses.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
        }
        enrolledCoursesList.clear();
        enrolledCoursesList.addAll(enrolledCourses.values());
        mEnrolledCourses.setValue(enrolledCoursesList);
    }

    public void setInterface() {
        mRepository.setInterface(this);
    }

    public void removeListeners() {
        mRepository.removeListeners();
    }

    public void addListener() {
        mRepository.addListeners();
    }

    public void deleteInstance() {
        mRepository.destroyInstance();
    }
}
