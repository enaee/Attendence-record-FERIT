package hr.ferit.zavrsni.Views.ChooseCourse;

import java.io.Serializable;

public class CourseElement implements Serializable {

    private String courseElementType;
    private String userID;
    private String courseID;

    public CourseElement(String courseElementType, String userID, String courseID) {
        this.courseElementType = courseElementType;
        this.userID = userID;
        this.courseID = courseID;
    }

    public String getCourseElementType() {
        return courseElementType;
    }

    public void setCourseElementType(String courseElementType) {
        this.courseElementType = courseElementType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
}
