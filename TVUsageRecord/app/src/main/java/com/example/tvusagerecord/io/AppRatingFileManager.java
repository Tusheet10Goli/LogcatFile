package com.example.tvusagerecord.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.io.FileInputStream;
import android.app.usage.UsageStats;
import android.os.Environment;
import android.util.Log;

import com.example.tvusagerecord.object.AppTimeStamp;

import java.util.ArrayList;
import java.io.FileWriter;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class AppRatingFileManager {

    public static final String TAG = AppRatingFileManager.class.getSimpleName();

    /**
     * create a new apps rating file, empty
     * @param fileName to print to
     */
    public void constructFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print("");
        writer.close();
    }


    /**
     * update a usage stats to rating file
     * If no such line, store it in a new line
     * If running time changes larger, update the original with the updated running time
     * If running time changes smaller, add the new one and original one
     * @param fileName
     * @param u
     */
    public void updateAppRatingFile(String fileName, UsageStats u) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        //first get content from the file to be an array
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        String[][] table = new String[500][2];
        int row = 0;
        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            if (line.contains(",")) {
                String[] values = line.split(",");
                table[row][0] = values[0];
                table[row][1] = values[1];
            }
            row++;
        }

        String pkgName = u.getPackageName();
        long foregroundTime = u.getTotalTimeInForeground();

        boolean matched = false;

        for (int i = 0; i < table.length; i++) {
            if (pkgName.equals(table[i][1])) {
                matched = true;
                long originalTime = Long.parseLong(table[i][0]);
                if (u.getLastTimeStamp() + 60000 >= System.currentTimeMillis()) {
                    table[i][0] = Long.toString(originalTime + 30000);
                }
            }
        }
        if (matched == true) {
            //print table to file
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            for (int i = 0; i < table.length; i++) {
                if (!(table[i][0] == null)) {
                    writer.println(table[i][0] + "," + table[i][1]);
                }
            }
            writer.close();
        } else {
            //append a new line to file
            FileWriter fileWriter = new FileWriter(file, true);
            String appendStr = foregroundTime + "," + pkgName;
            if (!appendStr.equals("null")) {
                Log.d(TAG, foregroundTime + "," + pkgName);
                fileWriter.append(foregroundTime + "," + pkgName);
                fileWriter.append("\n");
                fileWriter.close();
            }
        }
    }


    /**
     * get a list of time stamp and apps from the file
     * @param fileName
     * @return
     */
    public static List<AppTimeStamp> getUsageStatsListFromFile(String fileName) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        ArrayList<AppTimeStamp> list = new ArrayList<>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.contains(",")) {
                String[] values = line.split(",");
                AppTimeStamp app = new AppTimeStamp(values[0], values[1]);
                list.add(app);
            }
        }
        return list;
    }


    /**
     * sort a list of app time stamps and convert the list to string list
     * @param list
     * @return
     */
    public static List<String> sortAndConvertToStr(List<AppTimeStamp> list) {

        List<AppTimeStamp> newList = list;
        newList.sort(new Comparator<AppTimeStamp>() {
            @Override
            public int compare(AppTimeStamp u1, AppTimeStamp u2) {
                if(Long.parseLong(u1.getTimeStamp()) > Long.parseLong(u2.getTimeStamp())){
                    return -1;
                } else if (Long.parseLong(u1.getTimeStamp()) < Long.parseLong(u2.getTimeStamp())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        ArrayList<String> strList = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            strList.add("Foreground time: " + TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(newList.get(i).getTimeStamp())) + " min, " + "Package name: " + newList.get(i).getAppName());
        }
        return strList;
    }
}