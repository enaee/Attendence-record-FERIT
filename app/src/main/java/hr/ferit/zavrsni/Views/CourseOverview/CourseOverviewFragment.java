package hr.ferit.zavrsni.Views.CourseOverview;


import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;
import hr.ferit.zavrsni.Utils.CourseElement;
import hr.ferit.zavrsni.viewmodels.CourseOverviewViewModel;


@SuppressWarnings("ConstantConditions")
public class CourseOverviewFragment extends Fragment {

    public static final String COURSE_ID = "courseID";
    public static final String USER_ID = "userID";
    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";
    public CourseOverviewViewModel mViewModel;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView mCourseTitle, mPercentageDone, mPercentageLeft, mPercentageAbsent, mTvLeft;
    private String mCourseID, mUserID;
    private ProgressBar mProgressBarPresentSigned, mProgressbarAbsent;
    private ImageView ivCoureDone;
    private EnrolledCourse mEnrolledCourse;
    private View rootView;


    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    public static CourseOverviewFragment newInstance(String courseID, String userID) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle arguments = new Bundle();
        arguments.putString(COURSE_ID, courseID);
        arguments.putString(USER_ID, userID);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.user_icon).setVisible(false);
        menu.findItem(R.id.sign_out_menu).setVisible(false);
        menu.findItem(R.id.search_icon).setVisible(false);
        menu.findItem(R.id.deleteItem).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteItem:
                mViewModel.deleteEnrolledCourse();
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_course_overview, container, false);
        initViews();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(CourseOverviewViewModel.class);
        mViewModel.init(mUserID, mCourseID);
        mViewModel.getCourse().observe(getViewLifecycleOwner(), new Observer<EnrolledCourse>() {
            @Override
            public void onChanged(@Nullable EnrolledCourse enrolledCourse) {
                mEnrolledCourse = mViewModel.getCourse().getValue();
                setCourseData();
            }
        });
        setUpPager();

    }


    private void initViews() {
        mCourseID = getArguments().getString(COURSE_ID);
        mUserID = getArguments().getString(USER_ID);
        mViewPager = rootView.findViewById(R.id.viewPager);
        mTabLayout = rootView.findViewById(R.id.tab);

        mCourseTitle = rootView.findViewById(R.id.tvCourseOverviewTitle);
        mProgressBarPresentSigned = rootView.findViewById(R.id.courseOverviewProgressbarPresenceSigned);
        mProgressbarAbsent = rootView.findViewById(R.id.courseOverviewProgressbarAbsent);
        mPercentageDone = rootView.findViewById(R.id.percentage_done);
        mPercentageLeft = rootView.findViewById(R.id.tvPercentageLeft);
        mPercentageAbsent = rootView.findViewById(R.id.tvPercentageAbsent);
        mTvLeft = rootView.findViewById(R.id.tvLeft);
        ivCoureDone = rootView.findViewById(R.id.ivCourseDone);

        mEnrolledCourse = new EnrolledCourse();
    }

    private void setCourseData() {
        mCourseTitle.setText(mViewModel.getCourse().getValue().getName());

        int perc = mViewModel.countPercentage(mEnrolledCourse);
        int abs = mViewModel.countAbsence(mEnrolledCourse);
        int total = perc + abs;


        if (perc >= 70) {
            mPercentageDone.setVisibility(View.GONE);
            mTvLeft.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(ivCoureDone, "alpha", 0f, 0.9f).setDuration(300).start();
            ivCoureDone.setVisibility(View.VISIBLE);
        } else {
            mPercentageDone.setText(perc + "%");
        }

        if (total >= 100) total = 100;
        mPercentageLeft.setText(100 - total + "%\nPreostalo");
        mPercentageAbsent.setText(abs + "%\nOdsutnost");

        if (abs >= 30) {
            mPercentageAbsent.setTextColor(Color.parseColor("#B00020"));
        }
        if (abs >= 20) {
            mPercentageAbsent.setTextColor(Color.parseColor("#C77800"));
        } else {
            mPercentageAbsent.setTextColor(Color.GRAY);
        }
        mProgressBarPresentSigned.setProgress(perc);
        mProgressbarAbsent.setProgress(perc + abs);
    }

    private void setUpPager() {
        PagerAdapter pagerAdapter = new SlidePagerAdapter(getChildFragmentManager());
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


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
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
