package com.example.user.suivezbouddhaandroid;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lucas on 13/01/17.
 */

public class Utils {

    /**
     * Write to a file in download directory
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void writeToFile(String data, String fileName) throws IOException {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path, fileName);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data.getBytes());
            stream.close();
            Log.i("Utils", "Written : " + file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Read the file in download directory
     * @param FileName
     * @return
     */
    public static String readFile(String FileName) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        final File file = new File(path, FileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Utils", "Read : " + text.toString());

        return text.toString();
    }
}