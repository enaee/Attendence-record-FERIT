package hr.ferit.zavrsni.Views;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;
import hr.ferit.zavrsni.interfaces.IEnrolledItemClickListener;
import hr.ferit.zavrsni.viewmodels.EnrolledCoursesViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnrolledCoursesFragment extends Fragment {


    // UI references
    public static final String USER_ID = "userID";
    private View mRootView;
    private String mUserID;
    //Interface
    private IEnrolledItemClickListener mItemClickListener;

    //List of courses adapter
    private CourseAdapter mCourseAdapter;
    private ListView mCourseListView;
    private TextView mNoEnrolledCourses;
    private List<EnrolledCourse> mCourses;

    private EnrolledCoursesViewModel mViewModel;
    private Observer<List<EnrolledCourse>> mObserver;


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

        final View rootView = inflater.inflate(R.layout.fragment_enroled_courses, container, false);
        mRootView = rootView;
        if (getArguments().containsKey(USER_ID)) {
            mUserID = getArguments().getString(USER_ID);
        }
        final SwipeRefreshLayout pullToRefresh = mRootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCourses = mViewModel.getEnrolledCourses().getValue();
                mCourseAdapter.clear();
                mCourseAdapter.addAll(mCourses);
                if (mCourses.size() == 0) {
                    mNoEnrolledCourses.setVisibility(View.VISIBLE);
                } else {
                    mNoEnrolledCourses.setVisibility(View.GONE);
                } // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        initializeUI();
        setLiveData();
        return mRootView;
    }


    private void setLiveData() {
        mCourses = new ArrayList<>();
        mCourseAdapter = new CourseAdapter(getContext(), R.layout.item_course, mCourses);
        mCourseAdapter.setNotifyOnChange(true);
        mCourseListView.setAdapter(mCourseAdapter);
        mViewModel = ViewModelProviders.of(this).get(EnrolledCoursesViewModel.class);
        mViewModel.init(mUserID);
        if (mObserver == null) {
            mObserver = new Observer<List<EnrolledCourse>>() {
                @Override
                public void onChanged(@Nullable List<EnrolledCourse> enrolledCourses) {
                    mCourses = mViewModel.getEnrolledCourses().getValue();
                    mCourseAdapter.clear();
                    mCourseAdapter.addAll(mCourses);
                    if (mCourses.size() == 0) {
                        mNoEnrolledCourses.setVisibility(View.VISIBLE);
                    } else {
                        mNoEnrolledCourses.setVisibility(View.GONE);
                    }
                }
            };
        }
        mViewModel.getEnrolledCourses().observe(this, mObserver);

    }

    private void initializeUI() {
        mCourseListView = mRootView.findViewById(R.id.coursesListView);
        mNoEnrolledCourses = mRootView.findViewById(R.id.tvNoEnrolledCourses);
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

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IEnrolledItemClickListener) {
            this.mItemClickListener = (IEnrolledItemClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mItemClickListener = null;
        mCourseAdapter.clear();

    }

    @Override
    public void onStop() {
        super.onStop();
        this.mItemClickListener = null;
        mCourseAdapter.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCourseAdapter.clear();
        mViewModel.removeListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.addListener();
        mViewModel.setInterface();
        if (getContext() instanceof IEnrolledItemClickListener) {
            this.mItemClickListener = (IEnrolledItemClickListener) getContext();
        }

    }
}



