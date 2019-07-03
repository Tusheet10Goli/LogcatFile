package com.example.tvusagerecord.io;

import android.os.Environment;
import android.util.Log;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.example.tvusagerecord.object.AppTimeStamp;
import java.io.File;

/**
 * class of file manager of reading, updating, printing the csv. file of app time stamps
 */
public class AppTimeStampFileManager {

    public static final String TAG = AppTimeStampFileManager.class.getSimpleName();

    /**
     * Check if the external storage of the device is available for read and write
     * @return if external storage available
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "Yes, file is writable - app_timestamp.csv");
            return true;
        }
        Log.d(TAG, "No, file is not writable - app_timestamp.csv");
        return false;
    }


    /**
     * Read content from the file, as a list containing of each line as a string
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> readFile(String fileName) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        ArrayList<String> strList = new ArrayList<>();
        while (scan.hasNextLine()) {
            strList.add(scan.nextLine());
        }
        return strList;
    }


    /**
     * return the pkg name of the last (newest) item from the file
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public String getLastItemName(String fileName) throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        ArrayList<String> strList = new ArrayList<>();
        int lineCount = 0;
        while (scan.hasNextLine()) {
            lineCount++;
            String nextLine = scan.nextLine();
            String[] values = nextLine.split(",");
            String name = values[1];
            strList.add(name);
        }
        if (lineCount == 0)
            return "";
        String lastItem = strList.get(strList.size() - 1);
        return lastItem;
    }


    /**
     * update the file: add a new AppTimeStamp item to it
     * @param fileName
     * @param app
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void updateFile(String fileName, AppTimeStamp app) throws IOException {
        boolean append = true;
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileWriter csvWriter = new FileWriter(file, append);
        FileInputStream stream = new FileInputStream(file);
        Scanner scan = new Scanner(stream, "UTF-8");

        String item = app.getTimeStamp() + "," + app.getAppName();
        if(scan.hasNext()) {
            csvWriter.append("\n");
        }
        csvWriter.append(item);
        csvWriter.close();
    }


    /**
     * clear the file app_timestamp.csv if lines reached 5000
     * @param fileName
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void clearFile(String fileName) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);

        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
        lineNumberReader.skip(Long.MAX_VALUE);
        int lines = lineNumberReader.getLineNumber();
        lineNumberReader.close();
        if (lines > 5000) {
            FileWriter csvWriter = new FileWriter(file, false);
            csvWriter.close();
            Log.d(TAG, "cleared data from app_timestamp.csv");
        }
    }
}