package hr.ferit.zavrsni;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.zavrsni.Models.EnrolledCourse;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnrolledCoursesFragment extends Fragment {
    private View mRootView;
    private String mUserID;
    // UI references

    public static final int RC_SIGN_IN = 1; //RC is request code
    public static final String ANONYMOUS = "anonymous";
    public static final String USER_ID = "userID";
    public static final String COURSE_ID = "courseID";
    public static final String ENROLLED_COURSES = "enrolled_courses";
    public static final String NEW_USER = "new user";
    public static boolean has_enrolled_courses = false;

    //Interface
    private ItemClickListener mItemClickListener;

    //Firebase
    private FirebaseDatabase mFirebaseDatabase; //entry point for our app to access database
    private DatabaseReference mEnrolledCoursesReference;
    private ChildEventListener mChildEventListener; //we need it to fetch data to our app

    //FirebaseAuthentification
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;

    //List of courses adapter
    private CourseAdapter mCourseAdapter;
    private ListView mCourseListView;
    private List<EnrolledCourse> mCourses;

    public interface ItemClickListener {
        void onItemClicked(String courseID, String userID, View view);
    }

    public EnrolledCoursesFragment() {
        // Required empty public constructor
    }

    public static EnrolledCoursesFragment newInstance(String userID) {
        EnrolledCoursesFragment fragment = new EnrolledCoursesFragment();
        Bundle arguments = new Bundle();
        arguments.putString(USER_ID, userID);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_enroled_courses, container, false);
        mRootView = rootView;

        if (getArguments().containsKey(USER_ID)) {
            mUserID = getArguments().getString(USER_ID);
        }
        initializeUI();
        return mRootView;
    }

    private void initializeUI() {
        //Initialize reference to views
        final FrameLayout frameLayout = mRootView.findViewById(R.id.fragment_overview);

        //initialize courses list and its adapter
        mCourseListView = mRootView.findViewById(R.id.coursesListView);

        mCourses = new ArrayList<>();
        mCourseAdapter = new CourseAdapter(getContext(), R.layout.item_course, mCourses);
        mCourseAdapter.setNotifyOnChange(true);
        mCourseListView.setAdapter(mCourseAdapter);

        mCourseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mItemClickListener.onItemClicked(mCourses.get(position).getId(), mUserID, view);

            }
        });
        mCourseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "You long-clicked " + mCourses.get(position).getName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEnrolledCoursesReference = mFirebaseDatabase.getReference().child("enrolledCourses").child(mUserID);
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    EnrolledCourse course = dataSnapshot.getValue(EnrolledCourse.class);
                    mCourseAdapter.add(course);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            this.mItemClickListener = (ItemClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mItemClickListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mChildEventListener != null) {
            mEnrolledCoursesReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


}



