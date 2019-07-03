package com.example.tvusagerecord;

import java.text.SimpleDateFormat;
import android.content.Context;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import java.util.Calendar;
import java.util.List;
import android.util.Log;
import java.util.Comparator;
import java.lang.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UStats {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UsageStats.class.getSimpleName();

    /**
     * Retrieve the list of usage stats in the past period of time given by parameters
     * @param context
     * @param weekNum
     * @param dayNum
     * @return
     */
    public static List<UsageStats> getUsageStatsList(Context context, int weekNum, int dayNum){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_MONTH, -1 * weekNum);
        calendar.add(Calendar.DAY_OF_WEEK, -1 * dayNum);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }


    /**
     * print the given list of usage stats with its total duration in the range
     * @param usageStatsList
     */
    public static void printUsageStats(List<UsageStats> usageStatsList){
        for (UsageStats u : usageStatsList){
            Log.d(TAG, "Pkg: " + u.getPackageName() +  "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground()) ;
        }
    }


    /**
     * get list of string of given usage stats list
     * @param usageStatsList
     * @return
     */
    public static List<String> getUsageStatsListStr(List<UsageStats> usageStatsList) {
        List<String> strList = new ArrayList<>();
        for (UsageStats u: usageStatsList) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(u.getTotalTimeInForeground());
            if (minutes > 0) {
                strList.add("Package name: " + u.getPackageName() + "\t" + "ForegroundTime: " + minutes + " min");
            }
        }
        return strList;
    }


    /**
     * Sort usage stats list given by parameter in terms of total time in foreground
     * @param usageStatsList
     * @return
     */
    public static List<UsageStats> sortUsageStatsList(List<UsageStats> usageStatsList) {
        List<UsageStats> newList = usageStatsList;
        newList.sort(new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats u1, UsageStats u2) {
                if(u1.getTotalTimeInForeground() > u2.getTotalTimeInForeground()){
                    return -1;
                } else if (u1.getTotalTimeInForeground() < u2.getTotalTimeInForeground()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return newList;
    }


    /**
     * get the sorted list of package names from given sorted UsageStats list
     * @param sortedList
     * @return
     */
    public List<String> getSortedPackageNames(List<UsageStats> sortedList) {
        List<String> nameList = new ArrayList<String>();
        for (int i = 0; i < sortedList.size(); i++) {
            nameList.add(i, sortedList.get(i).getPackageName());
        }
        return nameList;
    }
}