package com.example.remotecontrol;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static final String TAG = "Logger";
    private static final String LOG_FILE_NAME = "logfile.txt";
    private static Logger instance;
    private static File logFile;

    private Logger(Context context) {
        File logDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                context.getPackageName() + "/log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        logFile = new File(logDir, LOG_FILE_NAME);
    }

    public static Logger getInstance(Context context) {
        if (instance == null) {
            instance = new Logger(context.getApplicationContext());
        }
        return instance;
    }

    private static void writeLog(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.append(message).append("\n");
        } catch (IOException e) {
            Log.e(TAG, "Failed to write logger file", e);
        }
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
