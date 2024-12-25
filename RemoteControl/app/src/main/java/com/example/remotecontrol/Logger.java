package com.example.remotecontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String TAG = "Logger";
    private static final String LOG_FILE_NAME = "logfile.txt";
    private static Logger instance;
    private static File logFile;
    private static Uri uri;
    private static FileUtils fileUtils;

    private Logger(Context context) {
        fileUtils = new FileUtils(context);
        uri = fileUtils.getUri("Logger.txt");
    }

    public static Logger getInstance(Context context) {
        if (instance == null) {
            instance = new Logger(context);
        }
        return instance;
    }

    private static void writeLog(String message) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentDateTime.format(formatter);
        String logMessage = "[" + formattedTime + "] " + message;
        fileUtils.appendToUri(uri, logMessage);
    }

    public static void d(String message) {
        Log.d(TAG, message);
        writeLog("DEBUG: " + message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
        writeLog("ERROR: " + message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
        writeLog("INFO: " + message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
        writeLog("WARN: " + message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
        writeLog("VERBOSE: " + message);
    }
}
