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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Map;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;
import hr.ferit.zavrsni.Utils.CourseElement;
import hr.ferit.zavrsni.viewmodels.CourseOverviewViewModel;

import static hr.ferit.zavrsni.MainActivity.AV;
import static hr.ferit.zavrsni.MainActivity.KV;
import static hr.ferit.zavrsni.MainActivity.LV;
import static hr.ferit.zavrsni.MainActivity.PR;

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

    private TextView tvTotalPresent, tvTotalAbsent, tvTotalSigned, tvPresentInNumbers;
    private ImageView ivEditDone;
    private LinearLayout LayoutPresent, LayoutAbsent, LayoutSigned;
    private ImageButton mBtnOpenEdit, btnPresentSub, btnPresentAdd, btnAbsSub, btnAbsAdd, btnSignSub, btnSignAdd;

    private CourseElement courseElement;
    private ViewGroup mRootView;
    private String mUserID, mCourseID, mCourseType;
    private EnrolledCourse mEnrolledCourse;
    private Map<String, Float> mCourseTypeAttendance;
    private Boolean editMode = false;



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
        tvPresentInNumbers = mRootView.findViewById(R.id.tvPresentInNumbers);
        tvTotalPresent = mRootView.findViewById(R.id.tvPresentCount);
        tvTotalAbsent = mRootView.findViewById(R.id.tvAbsentCount);
        tvTotalSigned = mRootView.findViewById(R.id.tvSignedCount);
        LayoutPresent = mRootView.findViewById(R.id.layoutPresent);
        LayoutAbsent = mRootView.findViewById(R.id.layoutAbsent);
        LayoutSigned = mRootView.findViewById(R.id.layoutSigned);
        mBtnOpenEdit = mRootView.findViewById(R.id.ibtnOpenEdit);
        ivEditDone = mRootView.findViewById(R.id.ivEditDone);
        btnPresentSub = mRootView.findViewById(R.id.btnPresentSubstract);
        btnPresentAdd = mRootView.findViewById(R.id.btnPresentAdd);
        btnAbsSub = mRootView.findViewById(R.id.btnAbsentSubstract);
        btnAbsAdd = mRootView.findViewById(R.id.btnAbsentAdd);
        btnSignSub = mRootView.findViewById(R.id.btnSignSubstract);
        btnSignAdd = mRootView.findViewById(R.id.btnSignAdd);
        btnPresentAdd.setVisibility(View.GONE);
        btnPresentSub.setVisibility(View.GONE);
        btnAbsAdd.setVisibility(View.GONE);
        btnAbsSub.setVisibility(View.GONE);
        btnSignAdd.setVisibility(View.GONE);
        btnSignSub.setVisibility(View.GONE);
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
            case PR:
                mCourseTypeAttendance = mEnrolledCourse.getP();
                checkIfNull(mCourseTypeAttendance);
                break;
            case AV:
                mCourseTypeAttendance = mEnrolledCourse.getA();
                checkIfNull(mCourseTypeAttendance);
                break;
            case LV:
                mCourseTypeAttendance = mEnrolledCourse.getL();
                checkIfNull(mCourseTypeAttendance);
                break;
            case KV:
                mCourseTypeAttendance = mEnrolledCourse.getK();
                checkIfNull(mCourseTypeAttendance);
                break;
        }
    }

    private void checkIfNull(Map<String, Float> mCourseTypeAttendance) {
        if (mCourseTypeAttendance.get(TOTAL) != 0) {
            LayoutPresent.setElevation(2);
            LayoutAbsent.setElevation(2);
            LayoutSigned.setElevation(2);
            setNumbers();
            setClickListeners();
        } else {
            mBtnOpenEdit.setVisibility(View.INVISIBLE);
            TextView tvPresent = mRootView.findViewById(R.id.tvPresent);
            tvPresent.setTextColor(Color.parseColor("#66666666"));
            TextView tvAbsent = mRootView.findViewById(R.id.tvAbsent);
            tvAbsent.setTextColor(Color.parseColor("#66666666"));
            TextView tvSigned = mRootView.findViewById(R.id.tvSigned);
            tvSigned.setTextColor(Color.parseColor("#66666666"));
            LayoutPresent.setBackgroundColor(Color.parseColor("#55ffffff"));
            LayoutAbsent.setBackgroundColor(Color.parseColor("#55ffffff"));
            LayoutSigned.setBackgroundColor(Color.parseColor("#55ffffff"));
        }
    }

    private void setNumbers() {
        Float presentSignedCount = mCourseTypeAttendance.get(PRESENT) + mCourseTypeAttendance.get(SIGNED);
        tvPresentInNumbers.setText(presentSignedCount + "/" + mCourseTypeAttendance.get(TOTAL));
        tvTotalPresent.setText(Float.toString(mCourseTypeAttendance.get(PRESENT)));
        tvTotalAbsent.setText(Float.toString(mCourseTypeAttendance.get(ABSENT)));
        tvTotalSigned.setText(Float.toString(mCourseTypeAttendance.get(SIGNED)));
    }

    private void setClickListeners() {
        View.OnClickListener deleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        mBtnOpenEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnOpenEdit.setVisibility(View.GONE);
                ivEditDone.setVisibility(View.VISIBLE);
                btnPresentAdd.setVisibility(View.VISIBLE);
                btnPresentSub.setVisibility(View.VISIBLE);
                btnAbsAdd.setVisibility(View.VISIBLE);
                btnAbsSub.setVisibility(View.VISIBLE);
                btnSignAdd.setVisibility(View.VISIBLE);
                btnSignSub.setVisibility(View.VISIBLE);
                editMode = true;
            }
        });
        ivEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnOpenEdit.setVisibility(View.VISIBLE);
                ivEditDone.setVisibility(View.GONE);
                btnPresentAdd.setVisibility(View.GONE);
                btnPresentSub.setVisibility(View.GONE);
                btnAbsAdd.setVisibility(View.GONE);
                btnAbsSub.setVisibility(View.GONE);
                btnSignAdd.setVisibility(View.GONE);
                btnSignSub.setVisibility(View.GONE);
                editMode = false;
            }
        });

        LayoutPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) updateCourseData(PRESENT);
            }
        });
        LayoutAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) updateCourseData(ABSENT);
            }
        });
        LayoutSigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) updateCourseData(SIGNED);
            }
        });

        btnSignAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(SIGNED);
            }
        });
        btnAbsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(ABSENT);
            }
        });
        btnPresentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCourseData(PRESENT);
            }
        });

        btnPresentSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                substractCourseData(PRESENT);
            }
        });
        btnAbsSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                substractCourseData(ABSENT);
            }
        });
        btnSignSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                substractCourseData(SIGNED);
            }
        });
    }

    private void substractCourseData(String typeOfAttendance) {
        if (mCourseTypeAttendance.get(typeOfAttendance) > 0) {
            mCourseTypeAttendance.put(typeOfAttendance, mCourseTypeAttendance.get(typeOfAttendance) - 1);
            float newCount = mCourseTypeAttendance.get(typeOfAttendance);
            setNumbers();
            mViewModel.setCourseData(mCourseType, typeOfAttendance, newCount);
        } else {
            Toast.makeText(getContext(), "Sorry, you've reached 0.", Toast.LENGTH_SHORT).show();
        }
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
        if (totalInput < mCourseTypeAttendance.get(TOTAL)) {
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



