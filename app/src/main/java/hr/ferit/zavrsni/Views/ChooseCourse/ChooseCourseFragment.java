package hr.ferit.zavrsni.Views.ChooseCourse;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;
import hr.ferit.zavrsni.viewmodels.ChooseCourseViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCourseFragment extends Fragment {

    public static final String USER_ID = "userID";
    //filter courses in recycler
    private static String DEGREE_LEVEL;
    private static String MODULE;
    private static String GRAD_MODULE;
    private static int SEMESTER;
    public Boolean isFilterOpen = true;
    //fragment.xml
    private RecyclerView recyclerView;
    private FloatingActionButton mFABChooseCourses;
    //recycler_item.xml
    private View rootView;
    private String mUserID;
    private RecyclerAdapter mAdapter;
    private ChooseCourseViewModel mViewModel;
    private List<Course> allCourses = new ArrayList<>();
    private List<EnrolledCourse> enrolledCourses = new ArrayList<>();
    private List<Course> allButEnrolledCourses = new ArrayList<>();
    private Map<String, Object> coursesToEnroll = new HashMap<>();
    private addCoursesListener mCheckIfShouldAddCourses;
    //search_layout.xml
    private LinearLayout mLayoutSearch, mLayoutDegreeLevel, mLayoutElectiveModules, mLayoutGraduateElectiveModules, mLayoutSemester14, mLayoutSemester56;
    private Button mBtnUndergraduate, mBtnGraduate, mBtnProfessional;
    private Button electivePowerEngineering, electiveCommunicationsAndInformatics, electiveComputerEngineering, electiveAutomotive, electiveInformatics;
    private Button btnA, btnB, btnC, btnD;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;
    private Button btnSearch;


    public ChooseCourseFragment() {
        // Required empty public constructor
    }

    public static ChooseCourseFragment newInstance(String userID) {
        ChooseCourseFragment fragment = new ChooseCourseFragment();
        Bundle arguments = new Bundle();
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
        menu.findItem(R.id.search_icon).setVisible(true);
        menu.findItem(R.id.filter).setVisible(true);
        final MenuItem searchItem = menu.findItem(R.id.search_icon);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Pretraži po nazivu...");
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                closeSearchByCourseID();
                mAdapter.putData(allButEnrolledCourses);
                isFilterOpen = false;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLayoutSearch.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                if (isFilterOpen) {
                    closeSearchByCourseID();
                    mAdapter.putData(new ArrayList<Course>());
                    isFilterOpen = false;
                } else {
                    searchByCourseId();
                    isFilterOpen = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_choose_course, container, false);
        if (getArguments().containsKey(USER_ID)) {
            mUserID = getArguments().getString(USER_ID);
        }
        mFABChooseCourses = rootView.findViewById(R.id.fabChooseCourses);
        setViewModel();
        setRecycler();
        searchByCourseId();
        return rootView;
    }

    private void setViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ChooseCourseViewModel.class);
        mViewModel.init(mUserID);
        allCourses.addAll(mViewModel.getAllCourses());
        allButEnrolledCourses.addAll(mViewModel.getAllButEnrolled());
        enrolledCourses.addAll(mViewModel.getEnrolledCourses());
    }

    private void setRecycler() {
        recyclerView = rootView.findViewById(R.id.choose_course_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecyclerAdapter(new ArrayList<Course>());
        recyclerView.setAdapter(mAdapter);
    }

    // filter to choose course type
    private void closeSearchByCourseID() {
        ObjectAnimator.ofFloat(mLayoutSearch, "alpha", 0f, 1f).getStartDelay();
        mLayoutSearch.setVisibility(View.GONE);
    }

    private void searchByCourseId() {
        mLayoutSearch = rootView.findViewById(R.id.searchLayout);
        mLayoutDegreeLevel = rootView.findViewById(R.id.layoutDegreeLevel);
        mBtnUndergraduate = rootView.findViewById(R.id.undergraduate);
        mBtnGraduate = rootView.findViewById(R.id.graduate);
        mBtnProfessional = rootView.findViewById(R.id.professional);
        mLayoutElectiveModules = rootView.findViewById(R.id.layoutElectiveModules);
        mLayoutSemester14 = rootView.findViewById(R.id.layoutSemester14);
        mLayoutSemester56 = rootView.findViewById(R.id.layoutSemester56);

        electivePowerEngineering = rootView.findViewById(R.id.electivePowerEngineering);
        electiveCommunicationsAndInformatics = rootView.findViewById(R.id.electiveCommunicationsAndInformatics);
        electiveComputerEngineering = rootView.findViewById(R.id.electiveComputerEngineering);
        electiveAutomotive = rootView.findViewById(R.id.electiveAutomotive);
        electiveInformatics = rootView.findViewById(R.id.electiveInformatics);
        mLayoutGraduateElectiveModules = rootView.findViewById(R.id.layoutGraduateElectiveModules);

        btnA = rootView.findViewById(R.id.btnA);
        btnB = rootView.findViewById(R.id.btnB);
        btnC = rootView.findViewById(R.id.btnC);
        btnD = rootView.findViewById(R.id.btnD);

        btn1 = rootView.findViewById(R.id.btnSem1);
        btn2 = rootView.findViewById(R.id.btnSem2);
        btn3 = rootView.findViewById(R.id.btnSem3);
        btn4 = rootView.findViewById(R.id.btnSem4);
        btn5 = rootView.findViewById(R.id.btnSem5);
        btn6 = rootView.findViewById(R.id.btnSem6);

        btnSearch = rootView.findViewById(R.id.btnSearch);
        btnSearch.setEnabled(false);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCourse();
            }
        });

        mLayoutSearch.setVisibility(View.VISIBLE);
        setDegreeLevelNotClicked();
        setElectivesNotClicked();
        setGraduateElectivesNotClicked();
        setSemesterNotClicked();
        setVisibility();
        setClickListenersForDegreeLevel();
        setClickListenerForElectiveModules();
        setOnClickListenerForGraduateElectiveModules();
        setOnClickListenerForSemester();
    }

    private void setOnClickListenerForSemester() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn1.setSelected(true);
                SEMESTER = 1;
                btnSearch.setEnabled(true);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn2.setSelected(true);
                SEMESTER = 2;
                btnSearch.setEnabled(true);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn3.setSelected(true);
                SEMESTER = 3;
                btnSearch.setEnabled(true);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn4.setSelected(true);
                SEMESTER = 4;
                btnSearch.setEnabled(true);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn5.setSelected(true);
                SEMESTER = 5;
                btnSearch.setEnabled(true);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSemesterNotClicked();
                btn6.setSelected(true);
                SEMESTER = 6;
                btnSearch.setEnabled(true);
            }
        });
    }

    private void setClickListenersForDegreeLevel() {
        mBtnUndergraduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                setSemesterNotClicked();
                setVisibility();
                mBtnUndergraduate.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveAutomotive.setVisibility(View.GONE);
                electiveInformatics.setVisibility(View.GONE);
                electiveCommunicationsAndInformatics.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "P";
            }
        });
        mBtnGraduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                setSemesterNotClicked();
                setVisibility();
                mBtnGraduate.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveCommunicationsAndInformatics.setVisibility(View.VISIBLE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                electiveInformatics.setVisibility(View.GONE);
                DEGREE_LEVEL = "D";
            }
        });
        mBtnProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                setSemesterNotClicked();
                setVisibility();
                mBtnProfessional.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveCommunicationsAndInformatics.setVisibility(View.GONE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                electiveInformatics.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "S";
            }
        });

    }

    private void setClickListenerForElectiveModules() {
        electivePowerEngineering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    setGraduateElectivesNotClicked();
                    setVisibility();
                    mLayoutGraduateElectiveModules.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.VISIBLE);
                    btnD.setVisibility(View.GONE);
                } else {
                    setVisibility();
                    mLayoutSemester14.setVisibility(View.VISIBLE);
                    mLayoutSemester56.setVisibility(View.VISIBLE);
                }
                setElectivesNotClicked();
                electivePowerEngineering.setSelected(true);
                MODULE = "E";
            }
        });

        electiveCommunicationsAndInformatics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    setGraduateElectivesNotClicked();
                    setVisibility();
                    mLayoutGraduateElectiveModules.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.GONE);
                    btnD.setVisibility(View.GONE);
                } else {
                    setVisibility();
                    mLayoutSemester14.setVisibility(View.VISIBLE);
                    mLayoutSemester56.setVisibility(View.VISIBLE);
                }
                setElectivesNotClicked();
                electiveCommunicationsAndInformatics.setSelected(true);
                MODULE = "K";
            }
        });
        electiveComputerEngineering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    setGraduateElectivesNotClicked();
                    mLayoutGraduateElectiveModules.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.VISIBLE);
                    btnD.setVisibility(View.VISIBLE);
                } else {
                    mLayoutSemester14.setVisibility(View.VISIBLE);
                    mLayoutSemester56.setVisibility(View.VISIBLE);
                }
                setElectivesNotClicked();
                electiveComputerEngineering.setSelected(true);
                MODULE = "R";
            }
        });
        electiveAutomotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                } else {
                    mLayoutSemester14.setVisibility(View.VISIBLE);
                    mLayoutSemester56.setVisibility(View.VISIBLE);
                }
                setElectivesNotClicked();
                electiveAutomotive.setSelected(true);
                MODULE = "A";
            }
        });
        electiveInformatics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutSemester14.setVisibility(View.VISIBLE);
                mLayoutSemester56.setVisibility(View.VISIBLE);
                setElectivesNotClicked();
                electiveInformatics.setSelected(true);
                MODULE = "I";
            }
        });
    }

    private void setOnClickListenerForGraduateElectiveModules() {
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnA.setSelected(true);
                mLayoutSemester14.setVisibility(View.VISIBLE);
                GRAD_MODULE = "a";

            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnB.setSelected(true);
                mLayoutSemester14.setVisibility(View.VISIBLE);
                GRAD_MODULE = "b";
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnC.setSelected(true);
                mLayoutSemester14.setVisibility(View.VISIBLE);
                GRAD_MODULE = "c";
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnD.setSelected(true);
                mLayoutSemester14.setVisibility(View.VISIBLE);
                GRAD_MODULE = "d";
            }
        });
    }

    private void setDegreeLevelNotClicked() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLayoutSearch.getWindowToken(), 0);
        mBtnUndergraduate.setSelected(false);
        mBtnGraduate.setSelected(false);
        mBtnProfessional.setSelected(false);
        DEGREE_LEVEL = null;
    }

    private void setElectivesNotClicked() {
        electivePowerEngineering.setSelected(false);
        electiveInformatics.setSelected(false);
        electiveAutomotive.setSelected(false);
        electiveComputerEngineering.setSelected(false);
        electiveCommunicationsAndInformatics.setSelected(false);
        MODULE = null;
    }

    private void setGraduateElectivesNotClicked() {
        btnA.setSelected(false);
        btnB.setSelected(false);
        btnC.setSelected(false);
        btnD.setSelected(false);
        GRAD_MODULE = null;
    }

    private void setSemesterNotClicked() {
        btn1.setSelected(false);
        btn2.setSelected(false);
        btn3.setSelected(false);
        btn4.setSelected(false);
        btn5.setSelected(false);
        btn6.setSelected(false);
        btnSearch.setEnabled(true);
        SEMESTER = 0;
    }

    private void setVisibility() {
        btnSearch.setEnabled(false);
        if (GRAD_MODULE == null) {
            mLayoutSemester14.setVisibility(View.GONE);
            mLayoutSemester56.setVisibility(View.GONE);
            if (MODULE == null) {
                mLayoutSemester14.setVisibility(View.GONE);
                mLayoutSemester56.setVisibility(View.GONE);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                if (DEGREE_LEVEL == null) {
                    mLayoutElectiveModules.setVisibility(View.GONE);
                }
            }
        }
    }

    private void searchCourse() {
        List<Course> list = new ArrayList<>();
        List<Course> degreeList = new ArrayList<>();
        List<Course> electiveList = new ArrayList<>();
        List<Course> electiveGradList = new ArrayList<>();
        List<Course> semesterList = new ArrayList<>();
        for (Course course : allButEnrolledCourses) {
            if (course.getId().startsWith(DEGREE_LEVEL)) {
                degreeList.add(course);
            }
        }
        if (MODULE != null) {
            for (Course course : degreeList) {
                Character ch = course.getId().charAt(1);
                if (!ch.equals('E') && !ch.equals('K') && !ch.equals('R') && !ch.equals('A') && !ch.equals('I')) {
                    electiveList.add(course);
                } else if (course.getId().contains(MODULE)) {
                    electiveList.add(course);
                }
            }
            list = electiveList;

            if (GRAD_MODULE != null) {
                for (Course course : electiveList) {
                    String mGm = MODULE + GRAD_MODULE;
                    if (!course.getId().contains("a") && !course.getId().contains("b") && !course.getId().contains("c") && !course.getId().contains("d")) {
                        electiveGradList.add(course);
                    } else if (course.getId().contains(mGm)) {
                        electiveGradList.add(course);
                    }
                    list = electiveGradList;
                }
                if (SEMESTER != 0) {
                    for (Course c : electiveGradList) {
                        if (c.getSemester() == SEMESTER) {
                            semesterList.add(c);
                        }
                    }
                    list = semesterList;
                }
            } else {
                if (SEMESTER != 0) {
                    for (Course c : electiveList) {
                        if (c.getSemester() == SEMESTER) {
                            semesterList.add(c);
                        }
                    }
                    list = semesterList;
                }
            }
        }
        mAdapter.putData(list);
        closeSearchByCourseID();
    }
//end of filter

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof addCoursesListener) {
            this.mCheckIfShouldAddCourses = (addCoursesListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mCheckIfShouldAddCourses = null;
        allCourses.clear();
        coursesToEnroll.clear();
        enrolledCourses.clear();
        mViewModel.clearCoursesToEnroll();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public interface addCoursesListener {
        void checkIfShouldAddCourses(Boolean shouldCoursesBeAdded, Map<String, Object> courses);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> implements Filterable {

        private List<Course> adapterCourseList;
        private List<Course> adapterCourseListFull;
        private Filter courseNameFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Course> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(adapterCourseListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Course course : adapterCourseListFull) {
                        if (course.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(course);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapterCourseList.clear();
                adapterCourseList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        RecyclerAdapter(List<Course> courseList) {
            this.adapterCourseList = courseList;
            adapterCourseListFull = new ArrayList<>(adapterCourseList);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_course_checkable, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.setCourseName(adapterCourseList.get(i));
            setUpListItemView(holder, adapterCourseList.get(i));
        }

        @Override
        public int getItemCount() {
            return adapterCourseList.size();
        }

        public void putData(List<Course> list) {
            adapterCourseList.clear();
            adapterCourseList = list;
            adapterCourseListFull = new ArrayList<>(adapterCourseList);
            notifyDataSetChanged();
        }

        private void setUpListItemView(ViewHolder holder, Course course) {
            //setEnrolledCourses(holder, course);

            if (mViewModel.getCoursesToEnroll().isEmpty()) {
                setOnItemClickListener(holder, course);
            } else if (mViewModel.getCoursesToEnroll().containsKey(course.getId())) {
                setHolderView(true, holder);
                setOnItemClickListener(holder, course);
            } else if (!mViewModel.getCoursesToEnroll().containsKey(course.getId())) {
                setHolderView(false, holder);
                setOnItemClickListener(holder, course);
            }
        }

        private void setOnItemClickListener(final ViewHolder holder, final Course course) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mViewModel.getCoursesToEnroll().containsKey(course.getId())) {
                        setHolderView(true, holder);
                        mViewModel.putCourseToEnroll(course);
                    } else if (mViewModel.getCoursesToEnroll().containsKey(course.getId())) {
                        setHolderView(false, holder);
                        mViewModel.removeCoursesToEnroll(course);
                    }
                    if (!mViewModel.getCoursesToEnroll().isEmpty()) {
                        if (mViewModel.getCoursesToEnroll().size() == 1) {
                            Animation changeFrom = AnimationUtils.loadAnimation(getContext(), R.anim.fab_check_to_add);
                            mFABChooseCourses.setAnimation(changeFrom);
                        }
                        mCheckIfShouldAddCourses.checkIfShouldAddCourses(true, mViewModel.getCoursesToEnroll());
                    } else {
                        mCheckIfShouldAddCourses.checkIfShouldAddCourses(false, null);
                    }
                }

            });

        }

        private void setHolderView(boolean b, ViewHolder holder) {
            if (b) {
                holder.courseName.setBackgroundColor(Color.parseColor("#4e7ca9"));
                holder.courseName.setTextColor(Color.WHITE);
            } else {
                holder.courseName.setBackgroundColor(Color.TRANSPARENT);
                holder.courseName.setTextColor(Color.BLACK);
            }
        }

        @Override
        public Filter getFilter() {
            return courseNameFilter;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public CheckedTextView courseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            courseName = itemView.findViewById(R.id.tvCourseListTitle_Checkable);
        }

        public void setCourseName(Course course) {
            String text = course.getName() + " (" + course.getId() + ")";
            courseName.setText(text);
        }
    }
}
