package hr.ferit.zavrsni.CourseOverview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.ChooseCourse.CourseElement;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;

public class CourseOverviewActivity extends AppCompatActivity {

    public static final String COURSE_ID = "courseID";
    public static final String USER_ID = "userID";
    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";


    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView mCourseTitle;
    private String mCourseID, mUserID;
    private ProgressBar mProgressBar;
    private ImageButton mEditButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEnrolledCourseReference;

    private Map<String, EnrolledCourse> mEnrolledCoursesMap = new HashMap<>();
    private EnrolledCourse mEnrolledCourse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_overview);
        initViews();
        setUpDatabase();
    }

    private void initViews() {
        Intent intent = getIntent();
        mCourseID = intent.getStringExtra(COURSE_ID);
        mUserID = intent.getStringExtra(USER_ID);
        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tab);
        mCourseTitle = findViewById(R.id.tvCourseOverviewTitle);
        mProgressBar = findViewById(R.id.courseOverviewProgressBarRound);
        mEditButton = findViewById(R.id.btnDelete);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditButton.setElevation(4);
                mEditButton.setColorFilter(Color.RED);
                mFirebaseDatabase.getReference("enrolledCourses/" + mUserID + "/" + mCourseID).removeValue();
                finish();
            }
        });
    }

    private void setUpPager() {
        PagerAdapter pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        setUpTabLayout();
    }

    private void setUpTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            switch (i) {
                case 0:
                    mTabLayout.getTabAt(0).setText("PR");
                case 1:
                    mTabLayout.getTabAt(1).setText("AV");
                case 2:
                    mTabLayout.getTabAt(2).setText("LV");
                default:
                    mTabLayout.getTabAt(3).setText("KV");
            }
        }

    }

    private void setUpDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEnrolledCourseReference = mFirebaseDatabase.getReference("enrolledCourses/" + mUserID/*+"/"+mCourseID*/);
        mEnrolledCourseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mEnrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                setCourseData();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mEnrolledCoursesMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(EnrolledCourse.class));
                updateData();
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
        });
    }

    private void updateData() {
        if (mEnrolledCoursesMap.containsKey(mCourseID)) {
            mEnrolledCourse = mEnrolledCoursesMap.get(mCourseID);
            if (mEnrolledCourse != null) {
                countPercentage();
            }
        }
    }

    private void setCourseData() {
        if (mEnrolledCoursesMap.containsKey(mCourseID)) {
            mEnrolledCourse = mEnrolledCoursesMap.get(mCourseID);
            if (mEnrolledCourse != null) {
                //postavljanje naslova
                mCourseTitle.setText(mEnrolledCourse.getName());
                countPercentage();
                setUpPager();
                setUpTabLayout();
            }
        }
    }

    private void countPercentage() {
        Map<String, Float> p = mEnrolledCourse.getP();
        Map<String, Float> a = mEnrolledCourse.getA();
        Map<String, Float> l = mEnrolledCourse.getL();
        Map<String, Float> k = mEnrolledCourse.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float present = p.get(PRESENT) + a.get(PRESENT) + l.get(PRESENT) + k.get(PRESENT);
        float signed = p.get(SIGNED) + a.get(SIGNED) + l.get(SIGNED) + k.get(SIGNED);
        float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float presentAndSigned = present + signed;
        float percentage = (presentAndSigned / total) * 100;
        mProgressBar.setSecondaryProgress((int) percentage);
        //mProgressBar.setProgress((int)percentage);
    }


    class SlidePagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 4;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {
                case 0:
                    CourseElement helperPR = new CourseElement("p", mUserID, mCourseID);
                    return CourseDetailsOverviewFragment.newInstance(helperPR, mEnrolledCourse);
                case 1:
                    CourseElement helperAV = new CourseElement("a", mUserID, mCourseID);
                    return CourseDetailsOverviewFragment.newInstance(helperAV, mEnrolledCourse);
                case 2:
                    CourseElement helperLV = new CourseElement("l", mUserID, mCourseID);
                    return CourseDetailsOverviewFragment.newInstance(helperLV, mEnrolledCourse);
                default:
                    CourseElement helperKV = new CourseElement("k", mUserID, mCourseID);
                    return CourseDetailsOverviewFragment.newInstance(helperKV, mEnrolledCourse);

            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}


