package hr.ferit.zavrsni;


import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Models.User;
import hr.ferit.zavrsni.Views.ChooseCourse.ChooseCourseFragment;
import hr.ferit.zavrsni.Views.CourseOverview.CourseOverviewFragment;
import hr.ferit.zavrsni.Views.EnrolledCoursesFragment;
import hr.ferit.zavrsni.interfaces.IEnrolledItemClickListener;
import hr.ferit.zavrsni.interfaces.INewUserListener;
import hr.ferit.zavrsni.repository.FirebaseManager;

public class MainActivity extends AppCompatActivity implements IEnrolledItemClickListener, ChooseCourseFragment.addCoursesListener, INewUserListener {

    public static final int RC_SIGN_IN = 1; //RC is request code
    public static final String ANONYMOUS = "anonymous";
    public static boolean has_enrolled_courses = false;
    private boolean isChooseCourseOpen = false;
    private boolean isCourseOverviewOpen = false;

    private boolean mShouldCoursesBeAdded;
    private Map<String, Object> mCoursesToAdd = new HashMap<>();

    private String mUserID;
    public static FloatingActionButton mFAB_AddCourse;
    private Toolbar myToolbar;
    private ProgressBar mLoadingProgressbar;

    //FirebaseAuthentification
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private FirebaseManager mRepository;


    //Fragments
    private FragmentManager mFragmentManager;
    private Fragment mEnrolledCoursesFragment;
    private Fragment mChooseCoursesFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.search_icon).setVisible(false);
        menu.findItem(R.id.deleteItem).setVisible(false);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserID = ANONYMOUS;
        initializeFirebaseAuth();
        initializeUI();

    }

    private void initializeFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if (mFirebaseUser != null) {
                    mUserID = mFirebaseUser.getUid();
                    setRepository();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .setTheme(R.style.MyTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String userID) {
        mUserID = userID;
        if (!isChooseCourseOpen && !isCourseOverviewOpen) setUpFragments();
    }



    public void setRepository() {
        mRepository = FirebaseManager.getInstance(mUserID);
        mRepository.setUserInterface(this);
        mRepository.addUserListener();
        if (!mUserID.equals(ANONYMOUS)) onUsersInitialize();
    }
    private void initializeUI() {
        //Initialize reference to views
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        mLoadingProgressbar = findViewById(R.id.loading_progressbar);
        mFAB_AddCourse = findViewById(R.id.fab_add_course);
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
        if (!isChooseCourseOpen) {
            isChooseCourseOpen = true;
            myToolbar.setBackgroundColor(Color.WHITE);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment, mChooseCoursesFragment).addToBackStack(null).commit();
            animateFAB();

        } else {
            isChooseCourseOpen = false;
            myToolbar.setVisibility(View.VISIBLE);
            myToolbar.setBackgroundColor(Color.TRANSPARENT);
            if (mShouldCoursesBeAdded) addToDatabase();
            mFragmentManager.popBackStack();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment, mEnrolledCoursesFragment).commit();
            animateFAB();
        }
    }
    private void animateFAB() {
        Animation addToCancel = AnimationUtils.loadAnimation(this, R.anim.fab_add_to_cancel);
        Animation cancelToAdd = AnimationUtils.loadAnimation(this, R.anim.fab_cancel_to_add);
        Animation cancelToCheck = AnimationUtils.loadAnimation(this, R.anim.fab_cancel_to_check);
        Animation checkToAdd = AnimationUtils.loadAnimation(this, R.anim.fab_check_to_add);
        //otvaramo choose course i jos nema odabranih
        if (isChooseCourseOpen && !mShouldCoursesBeAdded)
            mFAB_AddCourse.startAnimation(addToCancel);
        //zatvorili smo choose course i nismo odabrali
        if (!isChooseCourseOpen && !mShouldCoursesBeAdded)
            mFAB_AddCourse.startAnimation(cancelToAdd);
        //zatvorili smo choose course nakon dodavanja
        if (!isChooseCourseOpen && mShouldCoursesBeAdded) mFAB_AddCourse.startAnimation(checkToAdd);
        //otvoren je i odabrali smo kolegije
        if (isChooseCourseOpen && mShouldCoursesBeAdded)
            mFAB_AddCourse.startAnimation(cancelToCheck);
        //otvoren i odznačili smo sve
    }
    private void addToDatabase() {
        if (mShouldCoursesBeAdded) {
            for (String key : mCoursesToAdd.keySet()) {
                FirebaseDatabase.getInstance().getReference().child("enrolledCourses").child(mUserID).child(key).setValue(mCoursesToAdd.get(key));
            }
        }
        mCoursesToAdd.clear();
    }

    private void onSignedOutCleanup() {
        mUserID = ANONYMOUS;
        mRepository.removeListeners();
        mRepository.destroyInstance();
        if (mEnrolledCoursesFragment.isAdded())
            mFragmentManager.beginTransaction().detach(mEnrolledCoursesFragment).commit();
    }

    private void setUpFragments() {
        mChooseCoursesFragment = ChooseCourseFragment.newInstance(mUserID);
        mEnrolledCoursesFragment = EnrolledCoursesFragment.newInstance(mUserID);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment, mEnrolledCoursesFragment).commit();
        mLoadingProgressbar.setVisibility(View.GONE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        mLoadingProgressbar.setVisibility(View.VISIBLE);
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
            isCourseOverviewOpen = false;
            myToolbar.setBackgroundColor(Color.TRANSPARENT);
            mFAB_AddCourse.setAlpha((float) 1);
        } else if (isChooseCourseOpen) {
            isChooseCourseOpen = false;
            mShouldCoursesBeAdded = false;
            myToolbar.setVisibility(View.VISIBLE);
            myToolbar.setBackgroundColor(Color.TRANSPARENT);
            Animation changeFrom = AnimationUtils.loadAnimation(this, R.anim.fab_check_to_add);
            mFAB_AddCourse.setAnimation(changeFrom);
        }
    }

    @Override
    public void onItemClicked(String courseID, String userID, View view) {
        isCourseOverviewOpen = true;
        mFAB_AddCourse.setAlpha((float) 0);
        myToolbar.setBackgroundColor(Color.WHITE);
        CourseOverviewFragment fragment = CourseOverviewFragment.newInstance(courseID, userID);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public void checkIfShouldAddCourses(Boolean shouldCoursesBeAdded, Map<String, Object> courses) {
        if (mShouldCoursesBeAdded != shouldCoursesBeAdded) {
            mShouldCoursesBeAdded = shouldCoursesBeAdded;
            animateFAB();
        }
        mShouldCoursesBeAdded = shouldCoursesBeAdded;
        mCoursesToAdd = courses;

    }

    @Override
    public void onUsersInitialize() {
        if (!mRepository.checkIfNewUSer(mUserID)) {
            onSignedInInitialize(mUserID);
        } else {
            User user = new User(mUserID, mFirebaseUser.getDisplayName());
            mRepository.addUserToDatabase(user);
        }

    }
}
