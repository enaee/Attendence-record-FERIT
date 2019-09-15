package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.interfaces.IEnrolledCoursesCallback;
import hr.ferit.zavrsni.Database.FirebaseManager;


public class CourseOverviewViewModel extends ViewModel implements IEnrolledCoursesCallback {
    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";


    private MutableLiveData<EnrolledCourse> enrolledCourse = new MutableLiveData<>();
    private FirebaseManager mRepository;


    private String mUserID, mCourseID;


    public void init(String userID, String courseID) {
        this.mUserID = userID;
        this.mCourseID = courseID;
        mRepository = FirebaseManager.getInstance(mUserID);
        mRepository.setInterface(CourseOverviewViewModel.this);
        mRepository.addListeners();
        Map<String, EnrolledCourse> enrolledCourses = new HashMap<>(mRepository.getEnrolledCourses());
        enrolledCourse.setValue(mRepository.getEnrolledCourse(mCourseID));
    }


    public LiveData<EnrolledCourse> getCourse() {
        return enrolledCourse;
    }

    public void setCourseData(String courseType, String typeOfAttendance, float newCount) {
        String path = mCourseID + "/" + courseType + "/" + typeOfAttendance;
        mRepository.setCourseData(path, newCount);
        enrolledCourse.setValue(mRepository.getEnrolledCourse(mCourseID));
    }

    public void deleteEnrolledCourse() {
        mRepository.deleteEnrolledCourse(mCourseID);
    }

    public int countPercentage(EnrolledCourse enrolledCourse) {
        Map<String, Float> p = enrolledCourse.getP();
        Map<String, Float> a = enrolledCourse.getA();
        Map<String, Float> l = enrolledCourse.getL();
        Map<String, Float> k = enrolledCourse.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float present = p.get(PRESENT) + a.get(PRESENT) + l.get(PRESENT) + k.get(PRESENT);
        float signed = p.get(SIGNED) + a.get(SIGNED) + l.get(SIGNED) + k.get(SIGNED);
        float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float presentAndSigned = present + signed;
        float percentage = (presentAndSigned / total) * 100;
        return (int) percentage;
    }

    public int countAbsence(EnrolledCourse enrolledCourse) {
        Map<String, Float> p = enrolledCourse.getP();
        Map<String, Float> a = enrolledCourse.getA();
        Map<String, Float> l = enrolledCourse.getL();
        Map<String, Float> k = enrolledCourse.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float percentage = (absent / total) * 100;
        return (int) percentage;
    }

    @Override
    public void onChildAddedEnrolledCourse(DataSnapshot dataSnapshot) {

    }

    @Override
    public void OnChildDeletedEnrolledCourse(DataSnapshot dataSnapshot) {

    }

    @Override
    public void OnChildChangedEnrolledCourse(DataSnapshot dataSnapshot) {
        enrolledCourse.setValue(mRepository.getEnrolledCourse(mCourseID));

    }
}
