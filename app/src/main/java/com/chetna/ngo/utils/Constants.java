package com.chetna.ngo.utils;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Constants {

    public static final String BLOOD_GROUP = "blood_group";
    public static final String LOGIN = "login";
    public static final String YES = "yes";
    public static final String E_NUMBER = "ephone";
    public static final String PAN_CARD = "pan_card_number";
    private static ProgressDialog progressDialog;


    private static final String SHARED_PREF_DB_NAME = "NGO_USER";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String LANG = "LANG";
    public static final String PHONE = "phone";
    public static final String NUMBER = "number";
    public static final String IFSC = "ifsc";
    public static final String AC_NO = "ac_no";
    public static final String BANK_NAME = "bank_name";
    public static final String NAME_ON_BANK = "name_on_bank";
    public static final String USER_ID = "user_id";
    public static final String USER_TYPE = "user_type";
    public static final String USER_TYPE_ADMIN = "0";
    public static final String USER_TYPE_FRONT_LINE_WORKER = "3";
    public static final String USER_TYPE_PROJECT_CO_ORDINATOR = "1";
    public static final String USER_TYPE_STREET_CO_ORDINATOR = "2";
    public static final String USER_TYPE_PUBLIC = "4";
    public static final String USER_TYPE_BALAKNAMA = "5";
    public static final String USER_TYPE_SUB_ADMIN = "6";
    public static OkHttpClient client;
    public static int CONNECTION_TIMEOUT = 1000 * 20;
    public static int OKK_HTTP_TIMEOUT = 20;
    public static boolean isReporting = false;

    public static synchronized OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .callTimeout(OKK_HTTP_TIMEOUT, TimeUnit.MINUTES)
                    .readTimeout(OKK_HTTP_TIMEOUT, TimeUnit.MINUTES)
                    .writeTimeout(OKK_HTTP_TIMEOUT, TimeUnit.MINUTES)
                    .connectTimeout(OKK_HTTP_TIMEOUT, TimeUnit.MINUTES)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return client;
    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.e("URI", "getPath: " + uri);
                final String id = DocumentsContract.getDocumentId(uri);
                Log.e("ID", "IDPATH: " + id.substring(id.lastIndexOf("/") + 1));


//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                try {
                    final Uri contentUri = Uri.parse(Environment.DIRECTORY_DOWNLOADS + id.substring(id.lastIndexOf("/") + 1));
                    return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    return "";
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static void showProgressDialog(String title, String message, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TAG", "showToast: " + e);
        }
    }


    public static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, value);
        myEdit.commit();
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt(key, value);
        myEdit.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sh = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        return sh.getString(key, "");
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sh = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        return sh.getInt(key, 0);
    }

    public static boolean isStoragePermissionGranted(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                return false;
            }
        } else {
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    public static void savePosts(ArrayList<String> savePostArrayList, Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();


        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(savePostArrayList);
        Log.e("TAG", "savePosts: " + json);
        // below line is to save data in shared
        // prefs in the form of string.
        myEdit.putString("post_ids", json);
        myEdit.commit();
        // below line is to apply changes
        // and save data in shared prefs.
        myEdit.apply();

    }

    public static ArrayList<String> getSavedPostList(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_DB_NAME, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        Gson gson = new Gson();
        ArrayList<String> courseModalArrayList = new ArrayList<>();
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("post_ids", null);
        Log.e("TAG", "getPosts: " + json);
        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        courseModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (courseModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            courseModalArrayList = new ArrayList<>();
        }
        return courseModalArrayList;
    }


    public static void setLanguage(Context context, String language) {
        if (language.equals("")) {
            language = "en";
        }
        Locale locale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.locale = locale;
        res.updateConfiguration(configuration, displayMetrics);
    }


}
