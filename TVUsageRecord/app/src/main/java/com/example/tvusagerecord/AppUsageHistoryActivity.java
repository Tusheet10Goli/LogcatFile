package com.example.tvusagerecord;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.PrintWriter;
import com.example.tvusagerecord.manager.Manager;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import android.util.Log;

public class AppUsageHistoryActivity extends Activity {

    public static final String TAG = AppUsageHistoryActivity.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String fileName = "app_timestamp.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage_history);
        final ListView listview = (ListView) findViewById(R.id.list);

        //get list of strings from file
        Manager manager = new Manager();
        List<String> lines = new ArrayList<>();
        try {
            lines = manager.getAppsRunningHistory("app_timestamp.csv");
        } catch (FileNotFoundException e) {
            Log.e("AppUsageHistoryActivity", "time stamp file not found");
        }

        //order the display list in a reverse way
        for (int i = 0, j = lines.size() - 1; i < j; i++) {
            lines.add(i, lines.remove(j));
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lines);
        listview.setAdapter(adapter);

        //sort the app_timestamp.csv file
        Button sortFile = (Button) findViewById(R.id.sortFile);
        sortFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sort button clicked");
                sortCSV();
            }
        });
    }


    /**
     * sort app_timestamp.csv file
     */
    public void sortCSV() {

        Manager manager = new Manager();
        List<String> fileLines = new ArrayList<>();
        try {
            fileLines = manager.getAppsRunningHistory("app_timestamp.csv");
        } catch (FileNotFoundException e) {
            Log.e("AppUsageHistoryActivity", "time stamp file not found");
        }

        fileLines.sort(new Comparator<String>() {
            @Override
            public int compare(String l1, String l2) {

                String[] l1Values = l1.split(",");
                String l1TimeStr = l1Values[0];
                String[] l2Values = l2.split(",");
                String l2TimeStr = l2Values[0];
                long time1 = 0;
                long time2 = 0;
                try {
                    time1 = dateFormat.parse(l1TimeStr).getTime();
                    time2 = dateFormat.parse(l2TimeStr).getTime();
                } catch (ParseException e) {
                    Log.e(TAG, "parse exception when parse time stamp string to long");
                }

                if (time1 > time2){
                    return -1;
                } else if (time1 < time2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            for (int i = 0; i < fileLines.size(); i++) {
                if(i == (fileLines.size() - 1)) {
                    writer.print(fileLines.get(i));
                } else {
                    writer.println(fileLines.get(i));
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found when writing to file");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "unsupported encoding when writing to file");
        }
    }
}
