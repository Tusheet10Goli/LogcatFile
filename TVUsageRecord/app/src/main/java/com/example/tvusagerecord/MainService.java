package com.example.tvusagerecord;

import android.content.Intent;
import android.os.Environment;
import com.example.tvusagerecord.manager.Manager;

import android.os.IBinder;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.app.AlarmManager;
import android.content.Context;
import android.app.PendingIntent;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStats;
import com.example.tvusagerecord.object.AppTimeStamp;
import com.example.tvusagerecord.io.StartTimeRecorder;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import android.app.Service;

/**
 * main service
 */
public class MainService extends Service {

    /** class unique tag */
    private static final String TAG = "MainService";
    /** manager for interacting with files */
    Manager manager;
    /** convert long (time stamp) to string */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    /** file name of app time stamp file */
    public static final String fileName = "app_timestamp.csv";
    /** file name of duration (periods) file */
    public static final String durationFileName = "duration.csv";
    /** file name of app rating file */
    public static final String ratingFileName = "rating.csv";
    /** start time recorder */
    StartTimeRecorder recorder;
    /** constant of a day in milliseconds */
    private static final double DAY_IN_MILLISECONDS = 86400000;
    /** last update time for duration file */
    long lastUpdateTime = 0;
    /** last update time for app rating file */
    long lastUpdateRating = 0;
    /** last update time for logcat file */
    long lastUpdateTimeLogcat = 0;
    /** last pkg name added to apps rating count */
    UsageStats lastApp;
    String lastPkgName;

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "OnCreate => ");
        manager = new Manager();
        recorder = new StartTimeRecorder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Check for running apps and alarm 10 seconds to make sure the service keep running
     * @param intent
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        try {
            manager.clearTimeStampFile(fileName);
        } catch (IOException e) {
            //nothing
        }

        //store the first boot time to file
        if (recorder.isExternalStorageWritable()) {
            try {
                recorder.storeFirstTimeToFile();
                Log.d(TAG, "file created successfully - first_time.csv");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "start time file not found when storing");
            } catch (IOException e) {
                Log.e(TAG, "start time file io exception");
            }
        }

        //get the first boot time from file
        long firstTime = 0;
        try {
            firstTime = recorder.getFirstTimeLong();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "start time file not found when reading");
        } catch (ParseException e) {
            Log.e(TAG, "parse exception when retrieving start time");
        }

        //tell whether the current week is week1 or week2
        int week = 1;
        long current = System.currentTimeMillis();
        double daysOld = ((current - firstTime) / DAY_IN_MILLISECONDS);

        double weekDouble = daysOld / 7.0;
        week = (int) weekDouble + 1;

        //TO DO: 1. Continuously query usage stats
        //       2. Refer to file, add new item to file if it is not duplicate with the last item from the file
        Log.d(TAG, "start running on start command of main service");
        Context context = getApplicationContext();
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, current - 60 * 60 * 1000, current);
        Log.d(TAG, "queried past history for an hour");
        //for a 10 seconds running, we only need the newest one app, which is the index 0
        UsageStats app = null;
        try {
            for(UsageStats a : applist){
                Log.e(TAG, "app is: " + a.getPackageName());
            }
            app = applist.get(0);
        } catch (IndexOutOfBoundsException e) {
            return START_STICKY;
        }

        //get the launch time for this app
        long launchTime = app.getLastTimeStamp();
        String pkgName = app.getPackageName();
        String timeStr = dateFormat.format(launchTime);

        String lastPkgName = "";
        //check for the last item from file, add it if not duplicate
        try {
            lastPkgName = manager.getNewestPkgName(fileName);
            Log.d(TAG, "the last package item from file got");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "app timestamp file not existed");
        } catch (IndexOutOfBoundsException e) {
            return START_STICKY;
        }

        Log.d(TAG, "package name is: " + pkgName);

        applist.sort(new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats u1, UsageStats u2) {
                if(u1.getLastTimeStamp() > u2.getLastTimeStamp()){
                    return 1;
                } else if (u1.getLastTimeStamp() < u2.getLastTimeStamp()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        if (!pkgName.equals(lastPkgName)) {
            //add to file
            try {
                for (int i = 0; i < applist.size(); i++) {
                    manager.updateAppTimeStampFile(fileName, new AppTimeStamp(dateFormat.format(applist.get(i).getLastTimeStamp()), applist.get(i).getPackageName()));
                }
                Log.d(TAG, "package names added to file");
            } catch (IOException e) {
                Log.e(TAG, "io exception: cannot update the file");
            }
        } else {
            try {
                for (int j = 1; j < applist.size(); j++) {
                    manager.updateAppTimeStampFile(fileName, new AppTimeStamp(dateFormat.format(applist.get(j).getLastTimeStamp()), applist.get(j).getPackageName()));
                }
                Log.d(TAG, "package names added to file");
            } catch (IOException e) {
                Log.e(TAG, "io exception: cannot update the file");
            }
        }


        /** The following is for duration rating file */
        //get the hour from current time
        String currentStr = dateFormat.format(current);

        String hourStr = currentStr.split(" ")[1].split(":")[0];

        String period = "";

        if (hourStr.equals("06") || hourStr.equals("07") || hourStr.equals("08") || hourStr.equals("09") || hourStr.equals("10")) {
            //morning
            //add ten seconds to file
            //in final UI, convert those seconds from file to hours for view by users
            period = "morning";
        } else if (hourStr.equals("11") || hourStr.equals("12") || hourStr.equals("13")) {
            //noon
            //add ten seconds
            period = "noon";
        } else if (hourStr.equals("14") || hourStr.equals("15") || hourStr.equals("16") || hourStr.equals("17")) {
            //afternoon
            period = "afternoon";
        } else if (hourStr.equals("18") || hourStr.equals("19") || hourStr.equals("20") || hourStr.equals("21") || hourStr.equals("22")) {
            //evening
            period = "evening";
        } else {
            //late night
            //add ten seconds to file
            period = "night";
        }

        try {
            manager.updateDurationFile(durationFileName, week, period, 60);
            manager.updateDurationFile(durationFileName, week, "total", 60);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "unsupported encoding exception while updating duration file");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "duration file not found");
        }

        //update the lastUpdateTime variable
        lastUpdateTime = current;
        Log.d(TAG, "updated duration.csv");

        /** Above is for duration file */


        /** The following is for app rating file */
        long current2 = System.currentTimeMillis();
        //if (current2 - lastUpdateRating > 60 * 1000) {
        List<UsageStats> apps = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, current - 60 * 1000, current);


        for (UsageStats u : apps) {
            try {
                manager.updateApp(u, ratingFileName);
            } catch (IOException e) {
                Log.e(TAG, "IO Exception when updating app rating file");
            }
        }
        lastUpdateRating = current2;
        //}
        /** Above is for app rating file */


        /** The following is for logcat file */
        long current3 = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String currentTime = df.format(current3);
            String requiredTime = currentTime.substring(5,15);

            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if (line.contains(requiredTime) && !line.contains("app is:")) {
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
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        lastUpdateTimeLogcat = current3;
        /** Above is for logcat file */


        //use alarm to ensure the service is running every 10 seconds
        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent someIntent = new Intent(this, MainService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 1111, someIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +  60 * 1000, alarmIntent);
        Log.e(TAG, "Start the service alarm set");

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            //nothing
        }

        return START_STICKY;
    }
}