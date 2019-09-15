package hr.ferit.zavrsni.Database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.interfaces.IEnrolledCoursesCallback;

public class FirebaseEnrolledCoursesManager {


    private static FirebaseEnrolledCoursesManager instance;
    private DatabaseReference enrolledCoursesReference;
    private ChildEventListener mEnrolledCoursesListener;
    private IEnrolledCoursesCallback mCallback;
    private Map<String, EnrolledCourse> enrolledCoursesMap;


    public static FirebaseEnrolledCoursesManager getInstance(String userID, IEnrolledCoursesCallback callback) {
        if (instance == null) {
            instance = new FirebaseEnrolledCoursesManager(userID, callback);
        }
        return instance;
    }

    private FirebaseEnrolledCoursesManager(String userID, IEnrolledCoursesCallback callback) {
        enrolledCoursesReference = FirebaseDatabase.getInstance().getReference().child("enrolledCourses").child(userID);
        this.mCallback = callback;
        enrolledCoursesMap = new HashMap<>();
    }


    public void addListener() {
        if (mEnrolledCoursesListener == null) {
            mEnrolledCoursesListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mCallback.onChildAddedEnrolledCourse(dataSnapshot);
                    enrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mCallback.OnChildChangedEnrolledCourse(dataSnapshot);
                    enrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    mCallback.OnChildDeletedEnrolledCourse(dataSnapshot);
                    enrolledCoursesMap.remove(dataSnapshot.getKey());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
        enrolledCoursesReference.addChildEventListener(mEnrolledCoursesListener);


    }

    public void removeListeners() {
        if (mEnrolledCoursesListener != null)
            enrolledCoursesReference.removeEventListener(mEnrolledCoursesListener);
    }


    public Map<String, EnrolledCourse> getEnrolledCourses() {
        return enrolledCoursesMap;
    }

    public EnrolledCourse getEnrolledCourse(String courseID) {
        return enrolledCoursesMap.get(courseID);
    }


    public void deleteEnrolledCourse(String courseID) {
        enrolledCoursesReference.child(courseID).removeValue();
    }

}
