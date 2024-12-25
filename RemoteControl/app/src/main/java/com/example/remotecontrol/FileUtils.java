package com.example.remotecontrol;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class FileUtils {

    private final Context context;

    public FileUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public Uri getUri(String inputFileName) {
        Uri externalUri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = {inputFileName,
                Environment.DIRECTORY_DOCUMENTS + "/Remote Control/"};

        Uri uri;
        try (Cursor cursor = context.getContentResolver().query(
                externalUri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                uri = Uri.withAppendedPath(externalUri, String.valueOf(id));
            } else {
                ContentValues contentValues = getContentValues(inputFileName);
                uri = context.getContentResolver().insert(
                        MediaStore.Files
                                .getContentUri("external"), contentValues);
                }
        }

        if (uri == null) {
            Toast.makeText(context, "File created Failed", Toast.LENGTH_SHORT).show();
            return null;
        }

        return uri;
    }

    private static @NonNull ContentValues getContentValues(String inputFileName) {
        int lastDot = inputFileName.lastIndexOf(".");
        String fileName = inputFileName.substring(0, lastDot);
        String extension = inputFileName.substring(lastDot + 1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        switch (extension) {
            case "txt":
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"text/plain");
                break;
            case "json":
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"application/json");
                break;
            default:
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"text/octet-stream");
        }
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/Remote Control");
        return contentValues;
    }

    public void appendToUri(Uri uri, String message) {
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver()
                    .openOutputStream(uri, "wa")){
                if (outputStream != null) {
                    outputStream.write((message + '\n').getBytes());
                }
            }
            catch (Exception e) {
                Toast.makeText(context,
                        "Failed to save file: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("FileError", "Failed to save file", e);
            }
        } else {
            Toast.makeText(context,
                    "Uri is null", Toast.LENGTH_SHORT).show();
        }
    }
}
