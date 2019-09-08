package hr.ferit.zavrsni;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import hr.ferit.zavrsni.Models.EnrolledCourse;

public class FirebaseDB implements ChildEventListener {
    private ArrayList<EnrolledCourse> mEnrolledCourses = new ArrayList<>();
    //Firebase
    private FirebaseDatabase mFirebaseDatabase; //entry point for our app to access database
    private DatabaseReference mUsersDatabaseReference; //references to specific point in database
    private DatabaseReference mEnrolledCoursesReference;
    private ChildEventListener mChildEventListener; //we need it to fetch data to our app


    public FirebaseDB getInstance() {

    }


    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
