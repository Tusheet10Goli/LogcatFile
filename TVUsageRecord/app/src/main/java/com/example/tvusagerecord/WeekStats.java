package com.example.tvusagerecord;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tvusagerecord.io.DurationFileManager;
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

public class WeekStats extends Activity {

    private String TAG = WeekStats.class.getSimpleName();
    private String durationFileName = "duration.csv";
    private float[] yData = new float[5];
    private String[] xData = {"Morning", "Noon", "Afternoon", "Evening", "Night"};
    float total;
    static int weekNumber = 1;
    PieChart pieChart;

    /**
     * setter method for the weekNumber
     * @param weekNumber
     */
    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        Log.d(TAG, "weekNumber: " + this.weekNumber);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_stats);
        Log.d(TAG, "onCreate: starting main activity");

        Log.d(TAG, "weekNumber: " + this.weekNumber);

        //getting the yData
        DurationFileManager durationFileManager = new DurationFileManager();
        try {
            String[][] table = durationFileManager.readDurationFile(durationFileName);
            String[] row = durationFileManager.getValuesForCertainWeekInSeconds(table, weekNumber);
            float[] time = durationFileManager.convertRowSecondsToHours(row);
            total = time[0];

            for (int i = 0; i < yData.length; i++) {
                yData[i] = time[i + 1];
            }

            Log.d(TAG, "yData passed");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "yData failed");
            e.printStackTrace();
        }

        String totalTime = String.format("%.5f", total);

        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Week " + weekNumber + " Usage Stats");
        pieChart.setCenterTextSize(12);
        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("(sum): ");
                String time = e.toString().substring(pos1 + 7);

                for (int i = 0; i < yData.length; i++){
                    if(yData[i] == Float.parseFloat(time)) {
                        pos1 = i;
                        break;
                    }
                }

                String period = xData[pos1];
                Toast.makeText(WeekStats.this, "Period: " + period + "\n"
                        + "Hours: " + time, Toast.LENGTH_SHORT).show();
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

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i], i));
        }

        for(int i = 0; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Hours Used");
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
        legend.setTextColor(Color.WHITE);
        legend.setExtra(colorArray, xData);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
