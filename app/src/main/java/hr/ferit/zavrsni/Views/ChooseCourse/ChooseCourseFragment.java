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
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

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
    //fragment.xml
    private RecyclerView recyclerView;
    private FloatingActionButton mFABChooseCoures;
    private Toolbar searchToolbar;
    //recycler_item.xml
    private View rootView;
    private String mUserID;
    private RecyclerAdapter mAdapter;
    private ChooseCourseViewModel mViewModel;
    private List<Course> allCourses = new ArrayList<>();
    private List<EnrolledCourse> enrolledCourses = new ArrayList<>();
    private Map<String, Object> coursesToEnroll = new HashMap<>();
    private addCoursesListener mCheckIfShouldAddCourses;
    private LinearLayout mLayoutSearch, mLayoutDegreeLevel, mLayoutElectiveModules, mLayoutGraduateElectiveModules;
    private Button mBtnUndergraduate, mBtnGraduate, mBtnProfessional;
    private Button electivePowerEngineering, electiveCommunicationsAndInformatics, electiveComputerEngineering, electiveAutomotive, electiveInformatics;
    private Button btnA, btnB, btnC, btnD;


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

        final MenuItem searchItem = menu.findItem(R.id.search_icon);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Pretraži po nazivu...");
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchByCourseId();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                closeSearchByCourseID();
                ObjectAnimator.ofFloat(searchView, "alpha", 0f, 1f).getStartDelay();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLayoutSearch.getWindowToken(), 0);
                return false;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_choose_course, container, false);
        if (getArguments().containsKey(USER_ID)) {
            mUserID = getArguments().getString(USER_ID);
        }
        mFABChooseCoures = rootView.findViewById(R.id.fabChooseCourses);
        setViewModel();
        setRecycler();
        return rootView;
    }

    private void setViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ChooseCourseViewModel.class);
        mViewModel.init(mUserID);
        allCourses.addAll(mViewModel.getAllCourses());
        enrolledCourses.addAll(mViewModel.getEnrolledCourses());
    }

    private void setRecycler() {
        recyclerView = rootView.findViewById(R.id.choose_course_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setUpAdapter();
    }

    private void setUpAdapter() {
        mAdapter = new RecyclerAdapter(mViewModel.getAllButEnrolled());
        recyclerView.setAdapter(mAdapter);
    }

    // dropdown menu to choose course type
    private void closeSearchByCourseID() {
        ObjectAnimator.ofFloat(mLayoutSearch, "alpha", 0f, 1f).getStartDelay();
        mLayoutSearch.setVisibility(View.GONE);
    }

    private void searchByCourseId() {
        mLayoutSearch = rootView.findViewById(R.id.searchLayout);
        mLayoutDegreeLevel = rootView.findViewById(R.id.layoutDegreeLevel);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mLayoutSearch, "alpha", 0f, 1f);
        fadeIn.start();
        mBtnUndergraduate = rootView.findViewById(R.id.undergraduate);
        mBtnGraduate = rootView.findViewById(R.id.graduate);
        mBtnProfessional = rootView.findViewById(R.id.professional);
        mLayoutElectiveModules = rootView.findViewById(R.id.layoutElectiveModules);

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

        mLayoutSearch.setVisibility(View.VISIBLE);
        setClickListenersForDegreeLevel();
        setClickListenerForElectiveModules();
        setOnClickListenerForGraduateElectiveModules();
    }

    private void setOnClickListenerForGraduateElectiveModules() {
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnA.setSelected(true);
                GRAD_MODULE = "a";
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnB.setSelected(true);
                GRAD_MODULE = "b";
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnC.setSelected(true);
                GRAD_MODULE = "c";
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnD.setSelected(true);
                GRAD_MODULE = "d";
            }
        });
    }

    private void setClickListenerForElectiveModules() {
        electivePowerEngineering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    setGraduateElectivesNotClicked();
                    mLayoutGraduateElectiveModules.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.VISIBLE);
                    btnD.setVisibility(View.GONE);
                }
                setElectivesNotClicked();
                electivePowerEngineering.setSelected(true);
                MODULE = "E";
                searchCourse();
            }
        });

        electiveCommunicationsAndInformatics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    setGraduateElectivesNotClicked();
                    mLayoutGraduateElectiveModules.setVisibility(View.VISIBLE);
                    btnC.setVisibility(View.GONE);
                    btnD.setVisibility(View.GONE);
                }
                setElectivesNotClicked();
                electiveCommunicationsAndInformatics.setSelected(true);
                MODULE = "K";
                searchCourse();
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
                }
                setElectivesNotClicked();
                electiveComputerEngineering.setSelected(true);
                MODULE = "R";
                searchCourse();
            }
        });
        electiveAutomotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                }
                setElectivesNotClicked();
                electiveAutomotive.setSelected(true);
                MODULE = "A";
                searchCourse();
            }
        });
        electiveInformatics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElectivesNotClicked();
                electiveInformatics.setSelected(true);
                MODULE = "I";
                searchCourse();
            }
        });
    }

    private void setClickListenersForDegreeLevel() {

        mBtnUndergraduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                mBtnUndergraduate.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveAutomotive.setVisibility(View.GONE);
                electiveInformatics.setVisibility(View.GONE);
                electiveCommunicationsAndInformatics.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "P";
                searchCourse();
            }
        });
        mBtnGraduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                mBtnGraduate.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveCommunicationsAndInformatics.setVisibility(View.VISIBLE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                electiveInformatics.setVisibility(View.GONE);
                DEGREE_LEVEL = "D";
                searchCourse();
            }
        });
        mBtnProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDegreeLevelNotClicked();
                setElectivesNotClicked();
                mBtnProfessional.setSelected(true);
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                electiveCommunicationsAndInformatics.setVisibility(View.GONE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                electiveInformatics.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "S";
                searchCourse();
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

    private void searchCourse() {
        List<Course> list = new ArrayList<>();
        List<Course> degreeList = new ArrayList<>();
        List<Course> electiveList = new ArrayList<>();
        List<Course> electiveGradList = new ArrayList<>();
        for (Course course : allCourses) {
            if (course.getId().startsWith(DEGREE_LEVEL)) {
                degreeList.add(course);
            }
        }

        if (MODULE == null && GRAD_MODULE == null) {
            list = degreeList;
        } else if (MODULE != null && GRAD_MODULE == null) {
            for (Course course : degreeList) {
                Character ch = course.getId().charAt(1);
                if (!ch.equals('E') && !ch.equals('K') && !ch.equals('R') && !ch.equals('A') && !ch.equals('I')) {
                    electiveList.add(course);
                } else if (course.getId().contains(MODULE)) {
                    electiveList.add(course);
                }
            }
            list = electiveList;
        } else {
            for (Course course : electiveList) {
                if (course.getId().contains(MODULE + GRAD_MODULE)) {
                    electiveGradList.add(course);
                }
                list = electiveGradList;
            }
        }
        mAdapter.putData(list);
    }


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
            adapterCourseList = list;
            notifyDataSetChanged();
        }

/*        private void setEnrolledCourses(ViewHolder holder, Course course) {
            for (EnrolledCourse enrolledCourse : enrolledCourses) {
                if (enrolledCourse.getId().equals(course.getId())) {
                    holder.courseName.setBackgroundColor(Color.parseColor("#4e7ca9"));
                    holder.courseName.setTextColor(Color.WHITE);
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Already enrolled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        }*/

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
                            mFABChooseCoures.setAnimation(changeFrom);
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
                Toast.makeText(getContext(), "checked", Toast.LENGTH_SHORT).show();
            } else {
                holder.courseName.setBackgroundColor(Color.TRANSPARENT);
                holder.courseName.setTextColor(Color.BLACK);
                Toast.makeText(getContext(), "UNchecked", Toast.LENGTH_SHORT).show();
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
            courseName.setChecked(false);
        }

        public void setCourseName(Course course) {
            String text = course.getName() + " (" + course.getId() + ")";
            courseName.setText(text);
        }
    }
}
