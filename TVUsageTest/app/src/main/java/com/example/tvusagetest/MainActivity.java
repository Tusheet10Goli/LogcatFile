package com.example.tvusagetest;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.tvusagetest.R;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button for loading page of weekly usage stats
        Button durationButton = (Button) findViewById(R.id.durationButton);
        durationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, WeeklyUsageStats.class));
            }
        });

        //button for exporting files to usb folder
        Button export = (Button) findViewById(R.id.exportF);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, UsbActivity.class));
            }
        });

        //button for loading page of apps top-down list
        Button appsRating = (Button) findViewById(R.id.apps_rating_btn);
        appsRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, AppsRating.class));
            }
        });

        //button for loading page of apps running history
        Button appsHistory = (Button) findViewById(R.id.viewHistory);
        appsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, AppUsageHistoryActivity.class));
            }
        });
    }


    /**
     * check for permission status
     * @param permission
     */
    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}
