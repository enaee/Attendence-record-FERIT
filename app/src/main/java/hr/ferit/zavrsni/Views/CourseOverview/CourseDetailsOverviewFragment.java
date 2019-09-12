package hr.ferit.zavrsni.Views.CourseOverview;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Map;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;
import hr.ferit.zavrsni.Utils.CourseElement;
import hr.ferit.zavrsni.viewmodels.CourseOverviewViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseDetailsOverviewFragment extends Fragment {

    private static final String HELPER = "helper";
    private static final String ENROLLED_COURSE = "enrolled_course";
    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";


    private TextView mTotalPresent, mTotalAbsent, mTotalSigned;
    private LinearLayout mLayoutPresent, mLayoutAbsent, mLayoutSigned;
    private CourseElement courseElement;
    private ViewGroup mRootView;
    private String mUserID, mCourseID, mCourseType;
    private EnrolledCourse mEnrolledCourse;
    private Map<String, Float> mCourseTypeAttendance;
    private EditText mInputSigned;
    private ImageButton mBtnDone;

    private CourseOverviewViewModel mViewModel;

    public CourseDetailsOverviewFragment() {
        // Required empty public constructor
    }

    public static CourseDetailsOverviewFragment newInstance(Serializable helper, Serializable enrolledCourse) {
        CourseDetailsOverviewFragment fragment = new CourseDetailsOverviewFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(HELPER, helper);
        arguments.putSerializable(ENROLLED_COURSE, enrolledCourse);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_course_details_overview, container, false);
        if (getArguments() != null) {
            courseElement = (CourseElement) getArguments().getSerializable(HELPER);
            mEnrolledCourse = (EnrolledCourse) getArguments().getSerializable(ENROLLED_COURSE);
        }
        initialize();


        return mRootView;
    }

    private void initialize() {
        mTotalPresent = mRootView.findViewById(R.id.tvPresentCount);
        mTotalAbsent = mRootView.findViewById(R.id.tvAbsentCount);
        mTotalSigned = mRootView.findViewById(R.id.tvSignedCount);
        mLayoutPresent = mRootView.findViewById(R.id.layoutPresent);
        mLayoutAbsent = mRootView.findViewById(R.id.layoutAbsent);
        mLayoutSigned = mRootView.findViewById(R.id.layoutSigned);
        mInputSigned = mRootView.findViewById(R.id.etSignedInput);
        mBtnDone = mRootView.findViewById(R.id.btnEditDone);
        mInputSigned.setVisibility(View.GONE);
        mBtnDone.setVisibility(View.GONE);
    }

    private void setUpView() {
        if (courseElement != null) {
            mCourseID = courseElement.getCourseID();
            mUserID = courseElement.getUserID();
            mCourseType = courseElement.getCourseElementType();
            setUpCourseData();
        }
    }


    private void setUpCourseData() {
        switch (mCourseType) {
            case "p":
                mCourseTypeAttendance = mEnrolledCourse.getP();
                checkIfNull(mCourseTypeAttendance);
                break;
            case "a":
                mCourseTypeAttendance = mEnrolledCourse.getA();
                checkIfNull(mCourseTypeAttendance);
                break;
            case "l":
                mCourseTypeAttendance = mEnrolledCourse.getL();
                checkIfNull(mCourseTypeAttendance);
                break;
            case "k":
                mCourseTypeAttendance = mEnrolledCourse.getK();
                checkIfNull(mCourseTypeAttendance);
                break;
        }
    }

    private void checkIfNull(Map<String, Float> mCourseTypeAttendance) {
        if (mCourseTypeAttendance.get(TOTAL) != 0) {
            mLayoutPresent.setElevation(2);
            mLayoutAbsent.setElevation(2);
            mLayoutSigned.setElevation(2);
            setNumbers();
            setClickListeners();
        } else {
            TextView tvPresent = mRootView.findViewById(R.id.tvPresent);
            tvPresent.setTextColor(Color.parseColor("#66666666"));
            TextView tvAbsent = mRootView.findViewById(R.id.tvAbsent);
            tvAbsent.setTextColor(Color.parseColor("#66666666"));
            TextView tvSigned = mRootView.findViewById(R.id.tvSigned);
            tvSigned.setTextColor(Color.parseColor("#66666666"));
            mLayoutPresent.setBackgroundColor(Color.parseColor("#55ffffff"));
            mLayoutAbsent.setBackgroundColor(Color.parseColor("#55ffffff"));
            mLayoutSigned.setBackgroundColor(Color.parseColor("#55ffffff"));
        }
    }

    private void setNumbers() {
        mTotalPresent.setText(Float.toString(mCourseTypeAttendance.get(PRESENT)));
        mTotalAbsent.setText(Float.toString(mCourseTypeAttendance.get(ABSENT)));
        mTotalSigned.setText(Float.toString(mCourseTypeAttendance.get(SIGNED)));
    }


    private void setClickListeners() {
        mLayoutPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(PRESENT);
            }
        });
        mLayoutAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(ABSENT);
            }
        });
        mLayoutSigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(SIGNED);
            }
        });


    }

    private void updateCourseData(String typeOfAttendance) {
        if (checkIfCanAdd()) {
            mCourseTypeAttendance.put(typeOfAttendance, mCourseTypeAttendance.get(typeOfAttendance) + 1);
            float newCount = mCourseTypeAttendance.get(typeOfAttendance);
            setNumbers();
            //mEnrolledCourseReference.child(mCourseType + "/" + typeOfAttendance).setValue(newCount);
            mViewModel.setCourseData(mCourseType, typeOfAttendance, newCount);
        } else {
            Toast.makeText(getContext(), "Sorry, you've used all your input.", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkIfCanAdd() {
        float totalInput = mCourseTypeAttendance.get(PRESENT) + mCourseTypeAttendance.get(ABSENT) + mCourseTypeAttendance.get(SIGNED);
        if (totalInput <= mCourseTypeAttendance.get(TOTAL)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(CourseOverviewViewModel.class);
        mViewModel.getCourse().observe(getViewLifecycleOwner(), new Observer<EnrolledCourse>() {
            @Override
            public void onChanged(@Nullable EnrolledCourse enrolledCourse) {
                mEnrolledCourse = enrolledCourse;
            }
        });
        setUpView();
    }
}



