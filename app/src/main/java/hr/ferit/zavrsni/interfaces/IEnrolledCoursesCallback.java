package hr.ferit.zavrsni.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface IEnrolledCoursesCallback {
    void onChildAddedEnrolledCourse(DataSnapshot dataSnapshot);

    void OnChildDeletedEnrolledCourse(DataSnapshot dataSnapshot);

    void OnChildChangedEnrolledCourse(DataSnapshot dataSnapshot);
}
