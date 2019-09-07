package hr.ferit.zavrsni.ChooseCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;

public class ChooseCoursesActivity extends AppCompatActivity {

    public static final String USER_ID = "userID";
    public static final String ENROLLED_COURSES = "enrolled_courses";
    public static ArrayList<EnrolledCourse> enrolledCourses;


    private static String DEGREE_LEVEL;
    private static String MODULE;
    private static String GRAD_MODULE;
    private static final List<Course> courses = new ArrayList<>();

    private String mUserID;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    private LinearLayout mLayoutDegreeLevel, mLayoutElectiveModules, mLayoutGraduateElectiveModules;
    private Button mBtnUndergraduate, mBtnGraduate, mBtnProfessional;
    private Button electivePowerEngineering, electiveCommunicationsAndInformatics, electiveComputerEngineering, electiveAutomotive, electiveInformatics;
    private Button btnA, btnB, btnC, btnD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_courses);
        Intent intent = getIntent();


        mUserID = intent.getStringExtra(USER_ID);
        enrolledCourses = (ArrayList<EnrolledCourse>) intent.getSerializableExtra(ENROLLED_COURSES);
        FloatingActionButton mFAB_chose_courses = findViewById(R.id.fab_chose_courses);
        dropDownInitialize();
        DatabaseReference mCoursesDatabaseReference = mFirebaseDatabase.getReference().child("Courses");

    }


    // dropdown menu to choose course type
    private void dropDownInitialize() {
        mBtnUndergraduate = findViewById(R.id.undergraduate);
        mBtnGraduate = findViewById(R.id.graduate);
        mBtnProfessional = findViewById(R.id.professional);
        mLayoutElectiveModules = findViewById(R.id.layoutElectiveModules);

        electivePowerEngineering = findViewById(R.id.electivePowerEngineering);
        electiveCommunicationsAndInformatics = findViewById(R.id.electiveCommunicationsAndInformatics);
        electiveComputerEngineering = findViewById(R.id.electiveComputerEngineering);
        electiveAutomotive = findViewById(R.id.electiveAutomotive);
        electiveInformatics = findViewById(R.id.electiveInformatics);
        mLayoutGraduateElectiveModules = findViewById(R.id.layoutGraduateElectiveModules);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);

        setClickListenersForDegreeLevel();
        setClickListenerForElectiveModules();
        setOnClickListenerForGraduateElectiveModules();
    }

    private void setOnClickListenerForGraduateElectiveModules() {
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnA.setBackground(getDrawable(R.drawable.border_ring_selected));
                GRAD_MODULE = "a";
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnB.setBackground(getDrawable(R.drawable.border_ring_selected));
                GRAD_MODULE = "b";
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnC.setBackground(getDrawable(R.drawable.border_ring_selected));
                GRAD_MODULE = "c";
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGraduateElectivesNotClicked();
                btnD.setBackground(getDrawable(R.drawable.border_ring_selected));
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
                electivePowerEngineering.setBackground(getDrawable(R.drawable.border_selected));
                MODULE = "E";
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
                electiveCommunicationsAndInformatics.setBackground(getDrawable(R.drawable.border_selected));
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
                }
                setElectivesNotClicked();
                electiveComputerEngineering.setBackground(getDrawable(R.drawable.border_selected));
                MODULE = "R";
            }
        });
        electiveAutomotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEGREE_LEVEL.equals("D")) {
                    mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                }
                setElectivesNotClicked();
                electiveAutomotive.setBackground(getDrawable(R.drawable.border_selected));
                MODULE = "A";
            }
        });
        electiveInformatics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElectivesNotClicked();
                electiveInformatics.setBackground(getDrawable(R.drawable.border_selected));
                MODULE = "I";
            }
        });
    }

    private void setClickListenersForDegreeLevel() {
        mBtnUndergraduate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElectivesNotClicked();
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                mBtnUndergraduate.setBackground(getDrawable(R.drawable.border_selected));
                mBtnGraduate.setBackground(getDrawable(R.drawable.border_selector));
                mBtnProfessional.setBackground(getDrawable(R.drawable.border_selector));
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
                setElectivesNotClicked();
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);

                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                mBtnUndergraduate.setBackground(getDrawable(R.drawable.border_selector));
                mBtnGraduate.setBackground(getDrawable(R.drawable.border_selected));
                mBtnProfessional.setBackground(getDrawable(R.drawable.border_selector));
                electiveCommunicationsAndInformatics.setVisibility(View.VISIBLE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "D";
            }
        });
        mBtnProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setElectivesNotClicked();
                mLayoutGraduateElectiveModules.setVisibility(View.GONE);
                mLayoutElectiveModules.setVisibility(View.VISIBLE);
                mBtnUndergraduate.setBackground(getDrawable(R.drawable.border_selector));
                mBtnGraduate.setBackground(getDrawable(R.drawable.border_selector));
                mBtnProfessional.setBackground(getDrawable(R.drawable.border_selected));
                electiveCommunicationsAndInformatics.setVisibility(View.GONE);
                electiveAutomotive.setVisibility(View.VISIBLE);
                electiveInformatics.setVisibility(View.VISIBLE);
                DEGREE_LEVEL = "S";
            }
        });

    }

    private void setElectivesNotClicked() {
        electivePowerEngineering.setBackground(getDrawable(R.drawable.border_selector));
        electiveInformatics.setBackground(getDrawable(R.drawable.border_selector));
        electiveAutomotive.setBackground(getDrawable(R.drawable.border_selector));
        electiveComputerEngineering.setBackground(getDrawable(R.drawable.border_selector));
        electiveCommunicationsAndInformatics.setBackground(getDrawable(R.drawable.border_selector));
    }

    private void setGraduateElectivesNotClicked() {
        btnA.setBackground(getDrawable(R.drawable.border_ring_selector));
        btnB.setBackground(getDrawable(R.drawable.border_ring_selector));
        btnC.setBackground(getDrawable(R.drawable.border_ring_selector));
        btnD.setBackground(getDrawable(R.drawable.border_ring_selector));
    }

    //search logic
    private void searchCourse() {
        switch (DEGREE_LEVEL) {
            case "P":
                for (Course course : courses) {
                    if (!course.getId().contains("P")) {

                    }
                }

                break;
            case "D":
                break;
        }
    }


}
