package com.example.tvusagerecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.tvusagerecord.manager.Manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * receive the TV boot up broadcast to start the usage recording app and make track of
 * apps and activities happening
 */
public class MBroadcastReceiver extends BroadcastReceiver {

    /** unique tag for the class */
    private static final String TAG = "MBroadcastReceiver";
    /** manager regulating file of duration, app time stamp, etc. */
    Manager manager = new Manager();
    private static final String durationFileName = "duration.csv";
    private static final String ratingFileName = "rating.csv";


    /**
     * when receive boot up intent, start the main service
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "get action " + intent.getAction());
        Toast.makeText(context, "On Boot", Toast.LENGTH_LONG).show();
        // if detected TV boot up
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, MainService.class);

            try {
                File file = new File(Environment.getExternalStorageDirectory(), durationFileName);
                if (!file.exists()) {
                    manager.createDurationFile(durationFileName);
                    Log.d(TAG, "duration file created");
                } else {
                    Scanner scan = new Scanner(file);
                    if (!scan.hasNext()) {
                        manager.createDurationFile(durationFileName);
                        Log.d(TAG, "duration file created 2");
                    }
                }

            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found when creating the duration file");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "unsupported encoding when creating the duration file");
            }

            try {
                File file2 = new File(Environment.getExternalStorageDirectory(), ratingFileName);
                if (!file2.exists()) {
                    manager.createEmptyRatingFile(ratingFileName);
                    Log.d(TAG, "app rating file created");
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found exception when creating app rating file");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "unsupported encoding when creating app rating file");
            }
            context.startService(serviceIntent);
        }
    }
}