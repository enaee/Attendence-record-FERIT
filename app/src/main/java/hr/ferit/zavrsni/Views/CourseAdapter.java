package hr.ferit.zavrsni.Views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import hr.ferit.zavrsni.Models.EnrolledCourse;
import hr.ferit.zavrsni.R;

public class CourseAdapter extends ArrayAdapter<EnrolledCourse> {

    private static final String TOTAL = "total";
    private static final String PRESENT = "present";
    private static final String ABSENT = "absent";
    private static final String SIGNED = "signed";

    private ProgressBar progressBar, progressBarAbsent;
    private EnrolledCourse course;
    private TextView courseNameTextView, mPercentage;
    private ImageView ivCourseDone;


    public CourseAdapter(Context context, int resource, List<EnrolledCourse> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_course, parent, false);
        }
        courseNameTextView = convertView.findViewById(R.id.tvCourseListTitle);
        mPercentage = convertView.findViewById(R.id.percentage);
        progressBar = convertView.findViewById(R.id.courseOverviewProgressbarPresenceSigned);
        progressBarAbsent = convertView.findViewById(R.id.courseOverviewProgressbarAbsent);
        ivCourseDone = convertView.findViewById(R.id.courseDone);
        course = getItem(position);

        if (course != null) {
            courseNameTextView.setText(course.getName());
            int perc = countPercentage();
            int abs = countAbsence();
            int total = perc + abs;

            if (perc < 70) {
                progressBar.setProgress(perc);
                progressBarAbsent.setProgress(perc + abs);
                String percString = Integer.toString(countPercentage());
                mPercentage.setText(percString + "%");
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                progressBarAbsent.setVisibility(View.INVISIBLE);
                mPercentage.setVisibility(View.INVISIBLE);
                ivCourseDone.setVisibility(View.VISIBLE);
            }

        }

        return convertView;
    }


    private int countPercentage() {
        Map<String, Float> p = course.getP();
        Map<String, Float> a = course.getA();
        Map<String, Float> l = course.getL();
        Map<String, Float> k = course.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float present = p.get(PRESENT) + a.get(PRESENT) + l.get(PRESENT) + k.get(PRESENT);
        float signed = p.get(SIGNED) + a.get(SIGNED) + l.get(SIGNED) + k.get(SIGNED);
        //float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float presentAndSigned = present + signed;
        float percentage = (presentAndSigned / total) * 100;
        return (int) percentage;
    }

    private int countAbsence() {
        Map<String, Float> p = course.getP();
        Map<String, Float> a = course.getA();
        Map<String, Float> l = course.getL();
        Map<String, Float> k = course.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float percentage = (absent / total) * 100;
        return (int) percentage;
    }
}
