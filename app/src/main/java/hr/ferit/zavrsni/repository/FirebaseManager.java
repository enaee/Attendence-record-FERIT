package hr.ferit.zavrsni.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.Models.User;
import hr.ferit.zavrsni.interfaces.IEnrolledCoursesCallback;
import hr.ferit.zavrsni.interfaces.INewUserListener;

public class FirebaseManager {


    private static FirebaseManager instance;
    private DatabaseReference enrolledCoursesReference;
    private DatabaseReference coursesReference;
    private DatabaseReference usersReference;
    private ChildEventListener mEnrolledCoursesListener;
    private ValueEventListener mCoursesListener;
    private ValueEventListener mUserListener;
    private IEnrolledCoursesCallback mECCallBack;
    private INewUserListener userListener;
    private Map<String, EnrolledCourse> enrolledCoursesMap;
    private Map<String, Course> coursesMap;
    private Map<String, User> userMap;


    public static FirebaseManager getInstance(String userID) {
        if (instance == null) {
            instance = new FirebaseManager(userID);
        }
        return instance;
    }

    public void destroyInstance() {
        instance = null;
    }
    private FirebaseManager(String userID) {
        enrolledCoursesReference = FirebaseDatabase.getInstance().getReference().child("enrolledCourses").child(userID);
        coursesReference = FirebaseDatabase.getInstance().getReference().child("Courses");
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        enrolledCoursesMap = new HashMap<>();
        coursesMap = new HashMap<>();
        userMap = new HashMap<>();
    }

    public void setInterface(IEnrolledCoursesCallback callback) {
        this.mECCallBack = callback;
    }

    public void setUserInterface(INewUserListener listener) {
        this.userListener = listener;
    }

    public void addListeners() {
        if (mEnrolledCoursesListener == null) {
            mEnrolledCoursesListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mECCallBack.onChildAddedEnrolledCourse(dataSnapshot);
                    enrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mECCallBack.OnChildChangedEnrolledCourse(dataSnapshot);
                    enrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    mECCallBack.OnChildDeletedEnrolledCourse(dataSnapshot);
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

        if (mCoursesListener == null) {
            mCoursesListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Course course = child.getValue(Course.class);
                        coursesMap.put(course.getId(), course);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            coursesReference.addValueEventListener(mCoursesListener);
        }
    }

    public void addUserListener() {
        if (mUserListener == null) {
            mUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        userMap.put(user.getUserID(), user);
                    }
                    userListener.onUsersInitialize();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
        usersReference.addListenerForSingleValueEvent(mUserListener);
    }


    public void removeListeners() {
        if (mEnrolledCoursesListener != null)
            enrolledCoursesReference.removeEventListener(mEnrolledCoursesListener);
        if (mCoursesListener != null) coursesReference.removeEventListener(mCoursesListener);
        if (mUserListener != null) usersReference.removeEventListener(mUserListener);
    }

    public Map<String, Course> getAllCourses() {
        return coursesMap;
    }

    public Map<String, EnrolledCourse> getEnrolledCourses() {
        return enrolledCoursesMap;
    }

    public EnrolledCourse getEnrolledCourse(String courseID) {
        return enrolledCoursesMap.get(courseID);
    }

    public void setCourseData(String path, float newCount) {
        enrolledCoursesReference.child(path).setValue(newCount);
    }

    public void deleteEnrolledCourse(String courseID) {
        enrolledCoursesReference.child(courseID).removeValue();
    }

    public boolean checkIfNewUSer(String mUserID) {
        boolean state = true;
        if (userMap.containsKey(mUserID)) state = false;
        return state;
    }

    public void addUserToDatabase(User user) {
        usersReference.child(user.getUserID()).setValue(user);
    }
}
