package com.example.tvusagerecord;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tvusagerecord.io.AppRatingFileManager;
import com.example.tvusagerecord.object.AppTimeStamp;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TopAppRating extends Activity {

    public static final String TAG = AppsRating.class.getSimpleName();
    public static final String ratingFileName = "rating.csv";
    ArrayList<Float> time = new ArrayList<>();
    ArrayList<String> packageList = new ArrayList<>();
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_app_rating);

        AppRatingFileManager appRatingFileManager = new AppRatingFileManager();
        List<AppTimeStamp> list = null;
        try {
            list = appRatingFileManager.getUsageStatsListFromFile(ratingFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> stringList = appRatingFileManager.sortAndConvertToStr(list);

        for (int i = 0; i < stringList.size(); i++) {
            if (Float.parseFloat(stringList.get(i).split(": ")[1].split(" min, ")[0]) > 0) {
                time.add(Float.parseFloat(stringList.get(i).split(": ")[1].split(" min, ")[0]));
                packageList.add(stringList.get(i).split(": ")[2]);
                Log.d(TAG, "list: " + time.get(i) + " " + packageList.get(i));
            }
        }

        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Top Used Apps");
        pieChart.setCenterTextSize(12);
        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("(sum): ");
                String timePos = e.toString().substring(pos1 + 7);

                for (int i = 0; i < time.size(); i++){
                    if(time.get(i) == Float.parseFloat(timePos)) {
                        pos1 = i;
                        break;
                    }
                }

                Log.d(TAG, "pos1: " + pos1);
                String packageName = packageList.get(pos1);
                if(pos1 > 3) {
                    packageName = "Others";
                }
                Toast.makeText(TopAppRating.this, "Package: " + packageName + "\n"
                        + "Minutes Used: " + timePos + " mins", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                //nothing
            }
        });
    }


    /**
     * add the data set
     */
    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < time.size(); i++){
            yEntrys.add(new PieEntry(time.get(i), i));
            if(yEntrys.size() >= 4) {
                break;
            }
        }

        for(int i = 0; i < packageList.size(); i++){
            xEntrys.add(packageList.get(i));
            if(xEntrys.size() >= 4) {
                break;
            }
        }

        if(yEntrys.size() == 4 && xEntrys.size() == 4) {
            float otherTime = 0;
            String otherPackage = "Others";
            for (int i = 4; i < time.size(); i++) {
                otherTime += time.get(i);
            }
            yEntrys.add(new PieEntry(otherTime, 5));
            xEntrys.add(otherPackage);
        }

        Log.d(TAG, "yEntry size: " + yEntrys.size());
        Log.d(TAG, "xEntry size: " + xEntrys.size());

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Top Used Apps In Minutes");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to data set
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        int[] colorArray = new int[5];
        for(int i = 0; i < colors.size(); i++){
            colorArray[i] = colors.get(i);
        }

        pieDataSet.setColors(colors);

        //add legend to the chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setTextSize(12);
        String[] names = new String[xEntrys.size()];
        for (int i = 0; i < xEntrys.size(); i++) {
            names[i] = xEntrys.get(i);
        }
        legend.setExtra(colorArray, names);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
