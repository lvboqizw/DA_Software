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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class FileUtils {

    private final Context context;

    public FileUtils(Context context) {
        this.context = context;
    }

    public Uri getUri(String fileName) {
        Uri externalUri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = {fileName, Environment.DIRECTORY_DOCUMENTS + "/Remote Control"};

        Uri uri;
        try (Cursor cursor = context.getContentResolver()
                .query(externalUri,
                        projection,
                        selection,
                        selectionArgs,
                        null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                uri = Uri.withAppendedPath(externalUri, String.valueOf(id));
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"application/json");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + "/Remote Control");

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

    public void appendToUri(Uri uri, String message) {
        if (uri != null) {
            try (ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "rw")) {
                if (pfd != null) {
                    try (
                            FileOutputStream fileOutputStream =
                                    new FileOutputStream(pfd.getFileDescriptor());
                            OutputStreamWriter osw =
                                    new OutputStreamWriter(fileOutputStream);
                            BufferedWriter writer =
                                    new BufferedWriter(osw)) {

                        fileOutputStream.getChannel().position(
                                fileOutputStream.getChannel().size());

                        writer.write(message);
                        writer.newLine();
                        writer.flush();

                        Toast.makeText(context,
                                "File saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
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
