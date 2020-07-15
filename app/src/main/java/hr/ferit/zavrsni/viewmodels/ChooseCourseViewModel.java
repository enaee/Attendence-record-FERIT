package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Database.FirebaseManager;
import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;

import static hr.ferit.zavrsni.MainActivity.AV;
import static hr.ferit.zavrsni.MainActivity.KV;
import static hr.ferit.zavrsni.MainActivity.LV;
import static hr.ferit.zavrsni.MainActivity.PR;

public class ChooseCourseViewModel extends ViewModel {


    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";

    private List<Course> coursesList;
    private List<EnrolledCourse> enrolledCourses;
    private Map<String, Course> allCoursesMap;
    private Map<String, Object> coursesToEnroll;
    private Map<String, Course> coursesToEnrollMap;
    private FirebaseManager mRepository;
    private String mUserID;


    public void init(String userID) {
        this.mUserID = userID;
        allCoursesMap = new HashMap<>();
        enrolledCourses = new ArrayList<>();
        coursesList = new ArrayList<>();
        coursesToEnroll = new HashMap<>();
        coursesToEnrollMap = new HashMap<>();
        putData();
    }

    private void putData() {
        mRepository = FirebaseManager.getInstance(mUserID);
        allCoursesMap = mRepository.getAllCourses();
        enrolledCourses.addAll(mRepository.getEnrolledCourses().values());
        coursesList.addAll(mRepository.getAllCourses().values());
    }

    public List<Course> getAllCourses() {
        return coursesList;
    }

    public List<Course> getAllButEnrolled() {
        List<Course> list = new ArrayList<>();
        Map<String, Course> map = new HashMap<>(allCoursesMap);
        if (enrolledCourses.size() == 0) {
            list.addAll(coursesList);
        } else {
            for (String key : allCoursesMap.keySet()) {
                for (EnrolledCourse course : enrolledCourses) {
                    if (course.getId().equals(key)) {
                        map.remove(key);
                        break;
                    }
                }
            }
            list.addAll(map.values());
        }
        return list;
    }

    public List<EnrolledCourse> getEnrolledCourses() {
        return enrolledCourses;
    }

    public Map<String, Object> getCoursesToEnroll() {
        return coursesToEnroll;
    }

    public List<Course> getCoursesToEnrollList() {
        List<Course> coursesToEnrollList = new ArrayList<>();
        coursesToEnrollList.clear();
        coursesToEnrollList.addAll(coursesToEnrollMap.values());
        return coursesToEnrollList;
    }

    public void clearCoursesToEnroll() {
        coursesToEnrollMap.clear();
        coursesToEnroll.clear();
    }

    public void putCourseToEnroll(Course course) {
        coursesToEnrollMap.put(course.getId(), course);
        coursesToEnroll.put(course.getId(), prepareCourse(course));
    }

    public void removeCoursesToEnroll(Course course) {
        coursesToEnrollMap.remove(course.getId());
        coursesToEnroll.remove(course.getId());
    }

    private Map<String, Object> prepareCourse(final Course courseData) {
        Map<String, Object> mapP = new HashMap<String, Object>() {
            {
                put(TOTAL, courseData.getP());
                put(PRESENT, 0);
                put(ABSENT, 0);
                put(SIGNED, 0);
            }
        };
        Map<String, Object> mapA = new HashMap<String, Object>() {
            {
                put(TOTAL, courseData.getA());
                put(PRESENT, 0);
                put(ABSENT, 0);
                put(SIGNED, 0);
            }
        };
        Map<String, Object> mapL = new HashMap<String, Object>() {
            {
                put(TOTAL, courseData.getL());
                put(PRESENT, 0);
                put(ABSENT, 0);
                put(SIGNED, 0);
            }
        };
        Map<String, Object> mapK = new HashMap<String, Object>() {
            {
                put(TOTAL, courseData.getK());
                put(PRESENT, 0);
                put(ABSENT, 0);
                put(SIGNED, 0);
            }
        };
        Map<String, Object> course = new HashMap<>();
        course.put("name", courseData.getName());
        course.put("id", courseData.getId());
        course.put(PR, mapP);
        course.put(AV, mapA);
        course.put(LV, mapL);
        course.put(KV, mapK);
        return course;
    }

}
