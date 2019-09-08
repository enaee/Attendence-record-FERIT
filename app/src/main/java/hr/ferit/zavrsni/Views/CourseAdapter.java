package hr.ferit.zavrsni.Views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private ProgressBar progressBar;
    private EnrolledCourse course;
    private TextView courseNameTextView, mPercentage;

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
        progressBar = convertView.findViewById(R.id.courseOverviewProgressBarRound);
        course = getItem(position);

        if (course != null) {
            courseNameTextView.setText(course.getName());
            countPercentage();
        }

        return convertView;
    }

    private void countPercentage() {
        Map<String, Float> p = course.getP();
        Map<String, Float> a = course.getA();
        Map<String, Float> l = course.getL();
        Map<String, Float> k = course.getK();
        float total = p.get(TOTAL) + a.get(TOTAL) + l.get(TOTAL) + k.get(TOTAL);
        float present = p.get(PRESENT) + a.get(PRESENT) + l.get(PRESENT) + k.get(PRESENT);
        float signed = p.get(SIGNED) + a.get(SIGNED) + l.get(SIGNED) + k.get(SIGNED);
        float absent = p.get(ABSENT) + a.get(ABSENT) + l.get(ABSENT) + k.get(ABSENT);
        float presentAndSigned = present + signed;
        float percentage = (presentAndSigned / total) * 100;
        String perc = Integer.toString((int) percentage);
        progressBar.setSecondaryProgress((int) percentage);
        mPercentage.setText(perc + "%");
    }
}
