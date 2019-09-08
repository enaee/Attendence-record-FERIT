package hr.ferit.zavrsni;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Views.ChooseCourse.ChooseCourseFragment;
import hr.ferit.zavrsni.Views.CourseOverview.CourseOverviewFragment;
import hr.ferit.zavrsni.Views.EnrolledCoursesFragment;
import hr.ferit.zavrsni.interfaces.IEnrolledItemClickListener;

public class MainActivity extends AppCompatActivity implements IEnrolledItemClickListener, ChooseCourseFragment.addCoursesListener {

    public static final int RC_SIGN_IN = 1; //RC is request code
    public static final String ANONYMOUS = "anonymous";
    public static boolean has_enrolled_courses = false;
    private boolean isChooseCourseOpen = false;
    private boolean isCourseOverviewOpen = false;

    private boolean mShouldCoursesBeAdded;
    private Map<String, Object> mCoursesToAdd = new HashMap<>();

    private TextView tvNoEnrolledCourses;
    private String mUserID;
    public static FloatingActionButton mFAB_AddCourse;
    private Toolbar myToolbar;


    //Firebase
    private FirebaseDatabase mFirebaseDatabase; //entry point for our app to access database
    private DatabaseReference mUsersDatabaseReference; //references to specific point in database
    private DatabaseReference mEnrolledCoursesReference;
    private ChildEventListener mChildEventListener; //we need it to fetch data to our app

    //FirebaseAuthentification
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;


    //Fragments
    private FragmentManager mFragmentManager;
    private Fragment mEnrolledCoursesFragment;
    private Fragment mChooseCoursesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();

        initializeFirebaseAuth();
    }

    private void initializeFirebaseAuth() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    mUsersDatabaseReference.child(mFirebaseUser.getUid()).child("name").setValue(mFirebaseUser.getDisplayName());
                    if (mUsersDatabaseReference.child(mFirebaseUser.getUid()).child("name").getKey() == null) {
                        mUsersDatabaseReference.child(mFirebaseUser.getUid()).child("name").setValue(mFirebaseUser.getDisplayName());
                    }
                    onSignedInInitialize(mFirebaseUser.getUid());

                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void initializeUI() {
        //Initialize reference to views
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mFAB_AddCourse = findViewById(R.id.fab_add_course);
        mUserID = ANONYMOUS;
        tvNoEnrolledCourses = findViewById(R.id.tvNemaUpisanihKolegija);
        if (has_enrolled_courses) tvNoEnrolledCourses.setVisibility(View.GONE);
        mFragmentManager = getSupportFragmentManager();


        mFAB_AddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragments();
            }
        });


    }

    private void replaceFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Animation changeTo = AnimationUtils.loadAnimation(this, R.anim.fab_change_to);
        Animation changeFrom = AnimationUtils.loadAnimation(this, R.anim.fab_change_from);
        if (!isChooseCourseOpen) {
            isChooseCourseOpen = true;
            fragmentTransaction.replace(R.id.fragment, mChooseCoursesFragment).addToBackStack(null).commit();
            mFAB_AddCourse.startAnimation(changeTo);
        } else {
            isChooseCourseOpen = false;
            addToDatabase();
            mFragmentManager.popBackStack();
            fragmentTransaction.replace(R.id.fragment, mEnrolledCoursesFragment).commit();
            mFAB_AddCourse.startAnimation(changeFrom);
        }
    }

    private void addToDatabase() {
        if (mShouldCoursesBeAdded) {
            for (String key : mCoursesToAdd.keySet()) {
                mEnrolledCoursesReference.child(key).setValue(mCoursesToAdd.get(key));
            }
        }
        mCoursesToAdd.clear();
    }

    private void onSignedInInitialize(String userID) {
        mUserID = userID;
        mEnrolledCoursesReference = mFirebaseDatabase.getReference().child("enrolledCourses").child(mFirebaseUser.getUid());
        attachDatabaseReadListener();
        setUpFragments();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.hasChildren()) {
                        has_enrolled_courses = true;
                        tvNoEnrolledCourses.setVisibility(TextView.INVISIBLE);
                        //TODO dodaj kolegije u map, i kada se dogodi brisanje, na onBackPressed provjeri je li Map empty
                    } else tvNoEnrolledCourses.setVisibility(View.GONE);

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
            };
            mEnrolledCoursesReference.addChildEventListener(mChildEventListener);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.user_icon:
                Toast.makeText(this, "Signed in as " + mFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void onSignedOutCleanup() {
        mUserID = ANONYMOUS;
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mEnrolledCoursesReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!has_enrolled_courses) {
            tvNoEnrolledCourses.setVisibility(TextView.VISIBLE);
        } else {
            tvNoEnrolledCourses.setVisibility(TextView.INVISIBLE);
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    private void setUpFragments() {
        mChooseCoursesFragment = ChooseCourseFragment.newInstance(mUserID);
        mEnrolledCoursesFragment = EnrolledCoursesFragment.newInstance(mUserID);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment, mEnrolledCoursesFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign In Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkWhatFragmentIsOpen();
    }

    private void checkWhatFragmentIsOpen() {
        if (isCourseOverviewOpen) {
            isChooseCourseOpen = false;
            myToolbar.setVisibility(View.VISIBLE);
            mFAB_AddCourse.setAlpha((float) 1);
        } else if (isChooseCourseOpen) {
            isChooseCourseOpen = false;
            Animation changeFrom = AnimationUtils.loadAnimation(this, R.anim.fab_change_from);
            mFAB_AddCourse.setAnimation(changeFrom);
        }
    }

    @Override
    public void onItemClicked(String courseID, String userID, View view) {
        isCourseOverviewOpen = true;
        mFAB_AddCourse.setAlpha((float) 0);
        myToolbar.setVisibility(View.GONE);
        CourseOverviewFragment fragment = CourseOverviewFragment.newInstance(courseID, userID);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public void checkIfShouldAddCourses(Boolean shouldCoursesBeAdded, Map<String, Object> courses) {
        mShouldCoursesBeAdded = shouldCoursesBeAdded;
        mCoursesToAdd = courses;

    }
}
