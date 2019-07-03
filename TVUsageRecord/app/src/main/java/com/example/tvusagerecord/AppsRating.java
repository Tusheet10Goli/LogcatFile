package com.example.tvusagerecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.example.tvusagerecord.io.AppRatingFileManager;
import com.example.tvusagerecord.object.AppTimeStamp;

public class AppsRating extends Activity {

    public static final String TAG = AppsRating.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_rating);
        final ListView listview = (ListView) findViewById(R.id.listview);

        List<String> strList = new ArrayList<>();
        try {
            List<AppTimeStamp> list = AppRatingFileManager.getUsageStatsListFromFile("rating.csv");
            strList = AppRatingFileManager.sortAndConvertToStr(list);
        } catch (FileNotFoundException e) {

        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strList);
        listview.setAdapter(adapter);

        //pie chart graphing
        Button btPie = (Button) findViewById(R.id.btPie);
        btPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppsRating.this, "Most Used Apps", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AppsRating.this, TopAppRating.class));
            }
        });
    }
}
