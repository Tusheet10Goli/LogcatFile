package com.example.tvusagerecord;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tvusagerecord.io.DurationFileManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class WeeklyUsageBarGraph extends Activity {

    public static final String TAG = WeeklyUsageBarGraph.class.getSimpleName();
    public static final String durationFileName = "duration.csv";
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_usage_bar_graph);

        //getting plotting data for the bar graph
        DurationFileManager durationFileManager = new DurationFileManager();
        final List<String> weekArray = new ArrayList<>();
        ArrayList<Float> total = new ArrayList<>();

        String[][] table = new String[0][];
        try {
            table = durationFileManager.readDurationFile(durationFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 100; i++) {
            float totalUsage = durationFileManager.getTotalUsage(table, (i  + 1));
            if (totalUsage > 0) {
                total.add(totalUsage);
                weekArray.add("Week " + (i + 1));
                Log.d(TAG, "weekArray: "+ (i + 1));
            }
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < total.size(); i++) {
            int xValue = Integer.parseInt(weekArray.get(i).split(" ")[1]);
            float totalHours = total.get(i)/3600;
            barEntries.add(new BarEntry(xValue - 1, totalHours));
            Log.d(TAG, "xValue - 1: " + (xValue - 1));
        }

        barChart = (BarChart) findViewById(R.id.bargraph);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Weeks");
        BarData theData = new BarData(barDataSet);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.setData(theData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        theData.setBarWidth(0.9f);

        if (total.size() == 1) {
            barChart.setFitBars(false);
        } else {
            barChart.setFitBars(true);
        }

        barChart.setDrawValueAboveBar(true);
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeeklyUsageBarGraph.this, "Cumulative Week Stats", Toast.LENGTH_SHORT).show();
            }
        });

        barChart.invalidate();
        barChart.setFocusable(true);
    }
}
