package com.ee.portfolio.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ee.portfolio.R;
import com.ee.portfolio.activity.FragmentsActivity;
import com.ee.portfolio.classes.SubsciptionData;
import com.ee.portfolio.interfaces.AlertDialogInterface;
import com.ee.portfolio.interfaces.DownloadDialogInterface;
import timber.log.Timber;

import static com.ee.portfolio.common.Constants.PERMISSION_REQUEST_CODE;
import static com.ee.portfolio.common.Constants.haveConnectedMobile;
import static com.ee.portfolio.common.Constants.haveConnectedWifi;

/**
 * Created by sayalimane on 24/10/16.
 */

public class PortfolioUtils {

    /**
     * The listner.
     */
    static AlertDialogInterface mListner;

    /**
     * The Constant app_version.
     */
    public static final String app_version = "1.4";
    private static String TAG = "Portfolio";
    /**
     *  Taking Bitmap and adjust brightness
     *  as per requirement by
     *  changing "value"
     * @param src
     * @return
     */
    public static Bitmap makeShadowedBitmap(Bitmap src) {
        // image size
        int value=-150;
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
    /**
     * Checks if is network available.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {

        Context mContext = context;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED
                                || info[i].getState() == NetworkInfo.State.CONNECTING) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Show network error dialog.
     *
     * @param context the context
     */
    public static void showNetworkErrorDialog(Context context) {

        PortfolioUtils.CustomDialog(context, context.getString(R.string.app_name),
                context.getString(R.string.check_internet),
                context.getString(R.string.ok));

    }

    /**
     * Custom dialog.
     *
     * @param context
     * @param title
     * @param message
     * @param button
     */
    public static void CustomDialog(Context context, String title,
                                    String message, String button) {

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);

        dialog.setButton(Dialog.BUTTON_NEUTRAL, button,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

        dialog.show();
    }

    /**
     * GetCurrentTimeStamp.
     * <p/>
     * returns current date and time including seconds
     */
    public static String getCurrentDateTimeStamp() {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        month = month + 1;

        String timeStamp = date + "-" + month + "-" + year + "_" + hour + "-"
                + minute + "-" + second;

        PortfolioUtils.showLog("Time Stamp: " + timeStamp);

        return timeStamp;
    }

    /**
     * Gets the progress dialog.
     *
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context,
                                                   String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    /**
     * Gets the downloading progress dialog.
     *
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getDownloadingProgressDialog(Context context,
                                                              String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(message);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setSecondaryProgress(1);
        progressDialog.setMax(1);
        return progressDialog;
    }

    /**
     * Checks if is tablet.
     *
     * @param context the context
     * @return true, if is tablet
     */
    public static boolean isTablet(Context context) {

        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Get Screen Orientation.
     *
     * @param context the context
     * @return orientation in integer
     */
    public static int getScreenOrientation(Context context) {
        int rotation = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && height > width
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to "
                            + "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to "
                            + "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * Show toast.
     *
     * @param context  the context
     * @param errorMsg
     */
    public static void showToast(Context context, String errorMsg) {

        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show log using context.
     *
     * @param context  the context
     * @param errorMsg
     */
    public static void showLog(Context context, String errorMsg) {

        Log.v(TAG, errorMsg);
    }

    /**
     * Show log without using context.
     *
     * @param errorMsg
     */
    public static void showLog(String errorMsg) {

        Log.v(TAG, errorMsg);
    }

    /**
     * Show error log.
     *
     * @param errorMsg
     */
    public static void showErrorLog(String errorMsg, IOException e) {

        Log.e(TAG, errorMsg, e);
    }

    /**
     * Show alert.
     *
     * @param context the context
     * @param msg
     */
    public static void showalert(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setPositiveButton(
                        context.getResources().getString(R.string.ok), null)
                .show();
    }


    /**
     * Showalert_yes_no.
     *
     * @param context    the context
     * @param msg
     * @param btn_ok     msg
     * @param btn_cancel msg
     */
    public static void showalert_yes_no(final Context context, String msg,
                                        String btn_ok, String btn_cancel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(context.getResources().getString(
                R.string.app_name));
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(btn_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((Activity) context).finish();
                            }
                        })

                .setNegativeButton(btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static  void hideSoftKeyboard(Context context,View view){
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Show alert to download.
     *
     * @param mListner
     * @param context
     * @param videoPath
     * @param clip_id
     * @param word
     * @param package_id
     * @param action
     */
    public static void showAlertToDownload(
            final DownloadDialogInterface mListner, Context context,
            final String videoPath, final String clip_id, final String word,
            final String package_id, final String action) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(context.getResources().getString(
                R.string.app_name));
        alertDialogBuilder
                .setMessage(
                        context.getResources().getString(
                                R.string.about_us))
                .setCancelable(false)
                .setPositiveButton(
                        context.getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mListner.downloadYes(id, videoPath, clip_id,
                                        word, package_id, action);
                                dialog.cancel();
                            }
                        })

                .setNegativeButton(
                        context.getResources().getString(R.string.abort),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mListner.downloadNo(id, action);
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    /**
     * Open keyboard.
     *
     * @param context the context
     */
    public static void openKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Hide keyboard.
     *
     * @param context the context
     */
    public static void hideKeyboard(Context context) {
        ((ActionBarActivity) context).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Hide keyboard.
     *
     * @param context
     * @param userInput
     */
    public static void hideKeyboard_new(Context context, EditText userInput) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
    }

    /**
     * Enum of actions.
     */

    public enum Actions {

        REGISTER("register_device"), /**
         * The register.
         */
        GET_ALL_PACKAGES("get_all_package_list"), /**
         * The get all packages.
         */
        GET_ALL_WORDS_VIDEOS("get_all_words_n_videos"), /**
         * The get all words videos.
         */
        DEVICE_MAP("deviceid_packageid_map"), /**
         * The device map.
         */
        SYNC_PACKAGE("sync_package"); /** The sync package. */

        /**
         * The type.
         */
        private String type;

        /**
         * Instantiates a new Type.
         *
         * @param mType
         */
        private Actions(String mType) {
            type = mType;
        }

        /**
         * Gets the type.
         *
         * @return the type
         */
        public String getValue() {
            return type;
        }
    }

    /**
     * Enum of Download state.
     */

    public enum DownloadState {

        NOT_DOWNLOADED(0), /**
         * The not downloaded.
         */
        DOWNLOADED(1), /**
         * The downloaded.
         */
        WAITING_FOR_DOWNLOAD(2), /**
         * The waiting for download.
         */
        DOWNLOADING(3), /**
         * The downloading.
         */
        CANCELED(4); /** The canceled. */

        /**
         * The state.
         */
        private int state;

        /**
         * Instantiates a new state.
         *
         * @param mState
         */
        private DownloadState(int mState) {
            state = mState;
        }

        /**
         * Gets the state.
         *
         * @return the state
         */
        public int getValue() {
            return state;
        }
    }

    /**
     * Gets the device id.
     *
     * @param context the context
     * @return the device id
     */
    public static String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PortfolioUtils.showLog(context, "Device Id: " + deviceId);

        // LocalPreferences.setDeviceIdValue(deviceId);

        return deviceId;

    }


    /**
     * Checks if is numeric.
     *
     * @param str
     * @return true, if is numeric
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
        // '-' and decimal.
    }

    /**
     * Checks if is string.
     *
     * @param str
     * @return true, if is string
     */
    public static boolean isString(String str) {
        return str.matches("^[a-zA-Z]+"); // match a number with optional '-'
        // and decimal.
    }


    public static void setShowSyncDialogStatus(Context context, boolean flag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ShowSyncDialogStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean("ShowSyncDialogStatus", flag);
        prefsEditor.commit();
    }

    public static boolean getShowSyncDialogStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "ShowSyncDialogStatus", Context.MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean("ShowSyncDialogStatus", true);

        return status;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * @param mailAddress
     * @return
     */
    public static boolean emailValidator(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();

    }
    /**
     * Validate password with regular expression
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public static boolean passwordValidate(final String password){
        Pattern pattern;
        Matcher matcher;
         final String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    /**
     * Progress dialog
     * *
     * * @param context
     * * @param message
     * * @param isCancelble     *
     *
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context, String message, boolean isCancelble) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
        dialog.setCancelable(isCancelble);
        dialog.setMessage(message);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
        return dialog;
    }


    /**
     * network connection
     *
     * @param context
     * @return
     */

    public boolean haveNetworkConnection(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            Timber.w("ErrorDTO connection", "" + haveConnectedWifi + "-" + haveConnectedMobile);
            return haveConnectedWifi || haveConnectedMobile;
        } catch (Exception ex) {
            Toast.makeText(context, "error in network " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return haveConnectedWifi || haveConnectedMobile;
        //return false;
    }


    /**
     * Alert Dialog
     *
     * @param context
     * @param msg
     */
    public static void showAlertDialog(Context context, int msg, int positiveBtn) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.setCancelable(true);

            }
        });
        alertDialog.show();
    }

    /**
     * Alert Dialog
     *
     * @param context
     * @param msg
     */
    public static void showAlertDialogString(Context context, String msg, int positiveBtn) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.setCancelable(true);

            }
        });
        alertDialog.show();
    }
    /**
     * calculate number of days between two Dates
     * @param Created_date_String
     * @param Expire_date_String
     * @return
     */

    public static String get_count_of_days(String Created_date_String, String Expire_date_String) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date Created_convertedDate = null, Expire_CovertedDate = null, todayWithZeroTime = null;
        try {
            Created_convertedDate = dateFormat.parse(Created_date_String);
            Expire_CovertedDate = dateFormat.parse(Expire_date_String);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int c_year = 0, c_month = 0, c_day = 0;

        if (Created_convertedDate.after(todayWithZeroTime)) {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(Created_convertedDate);
            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(todayWithZeroTime);
            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar today_cal = Calendar.getInstance();
    int today_year = today_cal.get(Calendar.YEAR);
    int today = today_cal.get(Calendar.MONTH);
    int today_day = today_cal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar e_cal = Calendar.getInstance();
        e_cal.setTime(Expire_CovertedDate);

        int e_year = e_cal.get(Calendar.YEAR);
        int e_month = e_cal.get(Calendar.MONTH);
        int e_day = e_cal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(c_year, c_month, c_day);
        date2.clear();
        date2.set(e_year, e_month, e_day);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount + "");
    }
    public static void showAlertToRegistrationFailed(final Activity context,String message){


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(context.getResources().getString(
                R.string.app_name));
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(
                        context.getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.finish();
                            }
                        })

                .setNegativeButton(
                        context.getResources().getString(R.string.abort),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public static void showAlertToValidation(final Activity context,String message){


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(context.getResources().getString(
                R.string.app_name));
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(
                        context.getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     *  Permissions are added here
     */
    public static void permissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW)!= PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.SYSTEM_ALERT_WINDOW)) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                        PERMISSION_REQUEST_CODE);

            }
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
