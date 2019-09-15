package hr.ferit.zavrsni.viewmodels;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.Database.FirebaseManager;

public class ChooseCourseViewModel extends ViewModel {


    private List<Course> coursesList;
    private List<EnrolledCourse> enrolledCourses;
    private Map<String, Course> allCoursesMap;
    private Map<String, Course> chosenCourses;
    private Map<String, Object> coursesToEnroll;
    private FirebaseManager mRepository;
    private String mUserID;


    public void init(String userID) {
        this.mUserID = userID;
        allCoursesMap = new HashMap<>();
        enrolledCourses = new ArrayList<>();
        coursesList = new ArrayList<>();
        chosenCourses = new HashMap<>();
        coursesToEnroll = new HashMap<>();
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

    public void clearCoursesToEnroll() {
        coursesToEnroll.clear();
    }

    public void putCourseToEnroll(Course course) {
        coursesToEnroll.put(course.getId(), prepareCourse(course));
    }

    public void removeCoursesToEnroll(Course course) {
        coursesToEnroll.remove(course.getId());
    }

    private Map<String, Object> prepareCourse(final Course courseData) {
        Map<String, Object> mapP = new HashMap<String, Object>() {
            {
                put("total", courseData.getP());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapA = new HashMap<String, Object>() {
            {
                put("total", courseData.getA());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapL = new HashMap<String, Object>() {
            {
                put("total", courseData.getL());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapK = new HashMap<String, Object>() {
            {
                put("total", courseData.getK());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> course = new HashMap<>();
        course.put("name", courseData.getName());
        course.put("id", courseData.getId());
        course.put("p", mapP);
        course.put("a", mapA);
        course.put("l", mapL);
        course.put("k", mapK);
        return course;
    }

}
