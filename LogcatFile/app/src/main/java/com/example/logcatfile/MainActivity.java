package com.example.logcatfile;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnWriteLogCat = (Button) findViewById(R.id.btnWriteLogCat);
        btnWriteLogCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLogCat();
            }
        });

    }

    protected void writeLogCat()
    {
        try
        {
            //test
            String currentTime = dateFormat.format(System.currentTimeMillis());
            Log.d(TAG, "current time: " + currentTime);

            String requiredTime = currentTime.substring(5,15);
            Log.d(TAG, "required time: " + requiredTime);

            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if (line.contains(requiredTime)) {
                    log.append(line);
                    log.append("\n");
                }
            }

            //Convert log to string
            final String logString = new String(log.toString());

            //Create txt file in SD Card
            File file = new File(Environment.getExternalStorageDirectory(), "logcat.txt");

            //To write logcat in text file
            PrintWriter writer = new PrintWriter(file);

            //Writing the string to file
            writer.write(logString);
            writer.flush();
            writer.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}