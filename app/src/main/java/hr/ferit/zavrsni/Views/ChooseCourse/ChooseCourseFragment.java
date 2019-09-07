package hr.ferit.zavrsni.Views.ChooseCourse;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Models.Course;
import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCourseFragment extends Fragment {

    public static final String USER_ID = "userID";
    //fragment.xml
    private RecyclerView recyclerView;
    private FloatingActionButton mFABChooseCoures;
    //recycler_item.xml
    private View rootView;
    private String mUserID;

    private FirebaseRecyclerAdapter adapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEnrolledCoursesReference;

    private List<EnrolledCourse> enrolledCourses = new ArrayList<>();
    private Map<String, Course> chosenCourses = new HashMap<>();
    private Map<String, Object> coursesToEnroll = new HashMap<>();
    private addCoursesListener mCheckIfShouldAddCourses;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setExitTransition(new Fade());
        setEnterTransition(new Fade());
        // Inflate the layout for this fragment
        coursesToEnroll.clear();
        enrolledCourses.clear();
        rootView = inflater.inflate(R.layout.fragment_choose_course, container, false);
        if (getArguments().containsKey(USER_ID)) {
            mUserID = getArguments().getString(USER_ID);
        }
        Animation changeTo = AnimationUtils.loadAnimation(getContext(), R.anim.fab_change_to);
        Animation changeFrom = AnimationUtils.loadAnimation(getContext(), R.anim.fab_change_from);
        mFABChooseCoures = rootView.findViewById(R.id.fabChooseCourses);
        mFABChooseCoures.setAnimation(changeFrom);
        setRecycler();
        return rootView;
    }

    private void setRecycler() {
        recyclerView = rootView.findViewById(R.id.choose_course_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetch();
        recyclerView.setAdapter(adapter);
    }

    private void fetch() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEnrolledCoursesReference = mFirebaseDatabase.getReference().child("enrolledCourses").child(mUserID);
        mEnrolledCoursesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Iterable<DataSnapshot> childSnapshot = dataSnapshot.getChildren();
                    for (DataSnapshot child : childSnapshot) {
                        EnrolledCourse course = child.getValue(EnrolledCourse.class);
                        enrolledCourses.add(course);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setUpAdapter();
    }

    private void setUpAdapter() {
        Query query = mFirebaseDatabase.getReference().child("Courses");
        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class).build();
        adapter = new FirebaseRecyclerAdapter<Course, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Course course) {
                holder.setCourseName(course);
                //Provjerava ima li već upisanih kolegija
                if (enrolledCourses.isEmpty()) {
                    setOnItemClickListener(holder, course);
                } else {
                    //Provjerava je li kolegij koji se ubacije na listu već upisan
                    for (EnrolledCourse enrolledCourse : enrolledCourses) {
                        //ako nije upisan omogućava da ga se izabere ili ako se predomisli da ga se odznači
                        if (!enrolledCourse.getId().equals(course.getId())) {
                            setOnItemClickListener(holder, course);
                        }
                        //ako je kolegij upisan korisnik ga na ovoj listi ne može odznačiti i dobiva poruku da je kolegij već upisan
                        else {
                            holder.courseName.setChecked(true);
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
                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_course_checkable, viewGroup, false);
                return new ViewHolder(view);
            }
        };
    }

    private void setOnItemClickListener(final ViewHolder holder, final Course course) {
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.courseName.isChecked()) {
                    holder.courseName.toggle();
                    holder.courseName.setBackgroundColor(Color.parseColor("#4e7ca9"));
                    holder.courseName.setTextColor(Color.WHITE);
                    Toast.makeText(getContext(), "checked", Toast.LENGTH_SHORT).show();
                    //odabrani kolegij se ubacijue u HashMap koji kasnije predajemo bazi
                    coursesToEnroll.put(course.getId(), prepareCourse(course));
                } else if (holder.courseName.isChecked()) {
                    holder.courseName.toggle();
                    holder.courseName.setBackgroundColor(Color.TRANSPARENT);
                    Toast.makeText(getContext(), "UNchecked", Toast.LENGTH_SHORT).show();
                    coursesToEnroll.remove(course.getId());
                }
                if (!coursesToEnroll.isEmpty()) {
                    mCheckIfShouldAddCourses.checkIfShouldAddCourses(true, coursesToEnroll);
                }
            }

        });
    }

    private Map<String, Object> prepareCourse(final Course courseData) {
        Map<String, Object> mapP = new HashMap<String, Object>() {
            {
                put("total", courseData.getP());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapA = new HashMap<String, Object>() {
            {
                put("total", courseData.getA());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapL = new HashMap<String, Object>() {
            {
                put("total", courseData.getL());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> mapK = new HashMap<String, Object>() {
            {
                put("total", courseData.getK());
                put("present", 0);
                put("absent", 0);
                put("signed", 0);
            }
        };
        Map<String, Object> course = new HashMap<>();
        course.put("name", courseData.getName());
        course.put("id", courseData.getId());
        course.put("p", mapP);
        course.put("a", mapA);
        course.put("l", mapL);
        course.put("k", mapK);
        return course;
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
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public interface addCoursesListener {
        void checkIfShouldAddCourses(Boolean shouldCoursesBeAdded, Map<String, Object> courses);
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
