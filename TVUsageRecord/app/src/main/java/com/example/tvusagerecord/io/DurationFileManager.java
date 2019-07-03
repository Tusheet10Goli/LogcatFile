package com.example.tvusagerecord.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.io.FileInputStream;

import android.os.Environment;
import android.util.Log;

/**
 * class implementing print methods of all statistics
 */
public class DurationFileManager {

    public static final String TAG = DurationFileManager.class.getSimpleName();

    /**
     * Check if the external storage of the device is available for read and write
     * @return if external storage available
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "Yes, file is writable - duration.csv");
            return true;
        }
        Log.d(TAG, "No, file is not writable - duration.csv");
        return false;
    }


    /**
     * create a new file, fill in week number and titles
     * @param fileName to print to
     */
    public void constructFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println("Total,Morning Hours,Noon Hours,Afternoon Hours,Evening Hours,Night Hours");
        for (int i = 0; i < 100; i++) {
            writer.println(0 + "," + 0 + "," + 0 + "," + 0 + "," + 0 + "," + 0);
        }
        writer.close();
    }


    /**
     * Overwrite the file with the whole table containing titles and all Duration statistics
     * @param fileName
     * @param table
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void overwriteFile(String fileName, String[][] table) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        for (int i = 0; i < table.length; i++) {
            writer.println(table[i][0] + "," + table[i][1] + "," + table[i][2] + "," + table[i][3] + "," + table[i][4] + "," + table[i][5]);
        }
        writer.close();
    }


    /**
     * read all content from the file to return a 2D string array
     * @param fileName
     * @return table containing all content from the file
     * @throws FileNotFoundException
     */
    public String[][] readDurationFile(String fileName) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        String[][] table = new String[102][6];
        int row = 0;
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                table[row][i] = values[i];
            }
            row++;
        }
        return table;
    }


    /**
     * get the values for week1 or 2, THE UNIT IS IN SECONDS
     * @param table 2-D Array of the whole table including titles
     * @return single string array of week1/2 values
     *
     * Format:
     * total, morning, noon, afternoon, evening, late night (ALL IN SECONDS)
     *
     */
    public String[] getValuesForCertainWeekInSeconds(String[][] table, int week) {
        String[] row = new String[6];
        row[0] = table[week][0];
        row[1] = table[week][1];
        row[2] = table[week][2];
        row[3] = table[week][3];
        row[4] = table[week][4];
        row[5] = table[week][5];
        return row;
    }


    /**
     * help convert a row containing strings of seconds to float of hours
     * @param row
     * @return row converted to hours
     */
    public float[] convertRowSecondsToHours(String[] row) {
        float[] hours = new float[6];
        for (int i = 0; i < row.length; i++) {
            int seconds = Integer.parseInt(row[i]);
            float secondFloat = Float.valueOf(seconds);
            float divider = 3600.0F;
            float hour = secondFloat / divider;
            hours[i] = hour;
        }
        return hours;
    }


    /**
     * help to return the total usage in a given week
     * @param table
     * @param week
     * @return
     */
    public float getTotalUsage(String[][] table, int week) {
        String total = table[week][0];
        if (total.equals("null")) {
            return 0;
        }
        float totalFloat = Float.valueOf(total);
        return totalFloat;
    }
}