package com.example.tvusagerecord;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.util.Log;
import android.widget.Button;
import android.view.View;

public class UsbActivity extends Activity {

    /** file name of app time stamp file */
    public static final String fileName = "app_timestamp.csv";
    /** file name of duration (periods) file */
    public static final String durationFileName = "duration.csv";
    /** file name of app rating file */
    public static final String ratingFileName = "rating.csv";
    /** file recording first boot time */
    public static final String startFile = "first_time.csv";
    /** tag of this class */
    public static final String TAG = "UsbActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);

        Button exportBtn = (Button) findViewById(R.id.export);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] files = getFilesFromExternalStorage();
                saveFilesToUSB(files);
            }
        });

        Button email = (Button) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUsageHistoryActivity appUsageHistoryActivity = new AppUsageHistoryActivity();
                try {
                    appUsageHistoryActivity.sortCSV();
                    Log.d(TAG, "sent sorted app_timestamp.csv file - sorting succeeded");
                    openFolder();
                } catch (Throwable e) {
                    Log.d(TAG, "sent unsorted app_timestamp.csv file - sorting failed");
                    openFolder();
                }
            }
        });
    }


    /**
     * get all 4 files from external storage
     * @return
     */
    public File[] getFilesFromExternalStorage() {
        File timestamp = new File(Environment.getExternalStorageDirectory(), fileName);
        File duration = new File(Environment.getExternalStorageDirectory(), durationFileName);
        File rating = new File(Environment.getExternalStorageDirectory(), ratingFileName);
        File start = new File(Environment.getExternalStorageDirectory(), startFile);
        File[] files = new File[4];
        files[0] = timestamp;
        files[1] = duration;
        files[2] = rating;
        files[3] = start;
        return files;
    }


    /**
     * save files to a USB
     * @param files
     */
    public void saveFilesToUSB(File[] files) {
        //first get accurate path of media_rw
        File external = Environment.getExternalStorageDirectory();
        Log.d(TAG, "External storage absolute path: " + external.getAbsolutePath());

        File storage = new File("/storage");
        Log.d(TAG, "Storage path: " + storage.getAbsolutePath());
        File[] list = storage.listFiles();
        File usb = null;
        for (int i = 0; i < list.length; i++) {
            Log.d(TAG, "file name: " + list[i].getName());
            if (list[i].getName().contains("-")) {
                usb = list[i];
            }
        }

        //save files into usb folder
        File newTimeStamp = new File(usb, fileName);
        newTimeStamp = files[0];
        Log.d(TAG, "File saved: " + newTimeStamp.getName());
        Log.d(TAG, "File path: " + newTimeStamp.getAbsolutePath());

        File newDuration = new File(usb, durationFileName);
        newDuration = files[1];
        Log.d(TAG, "File saved: " + newDuration.getName());
        Log.d(TAG, "File path: " + newDuration.getAbsolutePath());

        File newRating = new File(usb, ratingFileName);
        newRating = files[2];
        Log.d(TAG, "File saved: " + newRating.getName());
        Log.d(TAG, "File path: " + newRating.getAbsolutePath());

        File newFirst = new File(usb, startFile);
        newFirst = files[3];
        Log.d(TAG, "File saved: " + newFirst.getName());
        Log.d(TAG, "File path: " + newFirst.getAbsolutePath());

        //delete file test
        File temp = new File(usb ,"testDeletingFile.txt");
        Log.d(TAG, "before delete file");
        if(temp.exists()) {
            Log.d(TAG, "File exists");
            Log.d(TAG, "USB path: " + usb.getAbsolutePath());
            temp.delete();
        }
        Log.d(TAG, "after delete file");

        String state = Environment.getExternalStorageState(usb);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "Yes, file is writable");
        } else {
            Log.d(TAG, "No, file is not writable");
        }

        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "Yes, file is read only");
        } else {
            Log.d(TAG, "No, file is not read only");
        }
    }


    /**
     * email the files to the target email id
     */
    public void openFolder() {
        String csv1 = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/duration.csv");
        String csv2 = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/app_timestamp.csv");
        String csv3 = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/rating.csv");
        String csv4 = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/first_time.csv");
        String csv5 = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/logcat.txt");

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");

        //email to information - can be changed as per need
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"htvusagerecord@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TV Usage App Files");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "1) duration.csv \n 2) app_timestamp.csv \n 3) ratings.csv \n 4) first_time.csv \n 5) logcat.txt");

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file1 = new File(csv1);
        Uri uri1 = Uri.fromFile(file1);

        File file2 = new File(csv2);
        Uri uri2 = Uri.fromFile(file2);

        File file3 = new File(csv3);
        Uri uri3 = Uri.fromFile(file3);

        File file4 = new File(csv4);
        Uri uri4 = Uri.fromFile(file4);

        File file5 = new File(csv5);
        Uri uri5 = Uri.fromFile(file5);

        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(uri1);
        uris.add(uri2);
        uris.add(uri3);
        uris.add(uri4);
        uris.add(uri5);

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);
    }
}
