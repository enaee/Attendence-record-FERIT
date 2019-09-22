package hr.ferit.zavrsni.Utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.R;

public class SearchById {
    private static String DEGREE_LEVEL;
    private static String MODULE;
    private static String GRAD_MODULE;
    private LinearLayout mLayoutSearch, mLayoutDegreeLevel, mLayoutElectiveModules, mLayoutGraduateElectiveModules;
    private Button mBtnUndergraduate, mBtnGraduate, mBtnProfessional;
    private Button electivePowerEngineering, electiveCommunicationsAndInformatics, electiveComputerEngineering, electiveAutomotive, electiveInformatics;
    private Button btnA, btnB, btnC, btnD;

    List<Course> courseList;
    List<Course> filteredList;
    View rootView;
    Activity activity;

    public SearchById(List<Course> courseList, View rootView, Activity activity) {
        this.courseList = courseList;
        this.rootView = rootView;
        this.activity = activity;
        this.filteredList = new ArrayList<>();
        setUpSerach();
    }

    private void setUpSerach() {
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
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
        List<Course> degreeList = new ArrayList<>();
        List<Course> electiveList = new ArrayList<>();
        List<Course> electiveGradList = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getId().startsWith(DEGREE_LEVEL)) {
                degreeList.add(course);
            }
        }

        if (MODULE == null && GRAD_MODULE == null) {
            filteredList = degreeList;
        } else if (MODULE != null && GRAD_MODULE == null) {
            for (Course course : degreeList) {
                Character ch = course.getId().charAt(1);
                if (!ch.equals('E') && !ch.equals('K') && !ch.equals('R') && !ch.equals('A') && !ch.equals('I')) {
                    electiveList.add(course);
                } else if (course.getId().contains(MODULE)) {
                    electiveList.add(course);
                }
            }
            filteredList = electiveList;
        } else {
            for (Course course : electiveList) {
                if (course.getId().contains(MODULE + GRAD_MODULE)) {
                    electiveGradList.add(course);
                }
                filteredList = electiveGradList;
            }
        }
    }


}
