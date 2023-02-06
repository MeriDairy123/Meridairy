package b2infosoft.milkapp.com.useful;


import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.FontsContract.Columns.RESULT_CODE;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static b2infosoft.milkapp.com.appglobal.Constant.AppUpdateServerUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.PICK_CONTACT;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.BuildConfig;
import b2infosoft.milkapp.com.Model.MachineData;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import okio.Buffer;


/**
 * Created by Choudhary on 09/21/2018.
 */
public class UtilityMethod {

    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;
    public static final int REQUEST_IMAGE = 100;
    public static int PERMISSION_ALL = 999;
    public static int PERMISSION_AndroidR = 999;
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    @ColorInt
    static int caretBackground = 0xff666666;

    public static void checkPermissionAndroidR(Context mContext) {
        /*if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Environment.isExternalStorageManager();
                if (!Environment.isExternalStorageManager()){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",mContext.getPackageName())));
                ((Activity) mContext).startActivityForResult(intent, PERMISSION_AndroidR);}
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                ((Activity) mContext).startActivityForResult(intent, PERMISSION_AndroidR);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, PERMISSION_ALL);
        }*/
    }

    public static String[] PERMISSIONSGR_S = {android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.BLUETOOTH_CONNECT};

    public static String[] PERMISSIONSLO_S = {android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};

    public static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET, android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA, READ_CONTACTS, android.Manifest.permission.BLUETOOTH_ADMIN, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static String[] SMSPERMISSIONS = {android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE, android.Manifest.permission.BLUETOOTH_ADMIN, READ_CONTACTS, WRITE_EXTERNAL_STORAGE};

    public static String[] ADD_ENTRY_PERMISSION = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.CAMERA, READ_CONTACTS, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static boolean hasPermissionsAddEntry(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


//    private void requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
//                startActivityForResult(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 2296);
//            }
//        } else {
//            //below android 11
//            ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
//    }
//



    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.forLanguageTag("hi"));

        return formatter.format(d);
    }

    public static String doubleToDecimalString(double number) {
        DecimalFormat dformat = new DecimalFormat("0.00");
        String decimalNo = (number < 10 ? " 0" : "") + dformat.format(number);
        return decimalNo;
    }

    public static String convertNumInGujrati(String number) {
        StringBuilder sb = new StringBuilder();
        String[] sArray = number.split("");
        for (int i = 0; i < sArray.length; i++) {
            String s = sArray[i];

            switch (s) {
                default:
                    sb.append(s);
                    break;
                case "0":
                    sb.append("૦");
                    break;
                case "1":
                    sb.append("૧");
                    break;
                case "2":
                    sb.append("૨");
                    break;
                case "3":
                    sb.append("૩");
                    break;
                case "4":
                    sb.append("૪");
                    break;
                case "5":
                    sb.append("૫");
                    break;
                case "6":
                    sb.append("૬");
                    break;
                case "7":
                    sb.append("૭");
                    break;
                case "8":
                    sb.append("૮");
                    break;
                case "9":
                    sb.append("૯");
                    break;


            }
        }


        return sb.toString();


    }

    public static void openFolder(Context context, String filePath) {

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(filePath);
//
//        intent.setDataAndType(uri, "*/*");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        ((Activity) context).startActivity(Intent.createChooser(intent, "Meri Dairy"));



        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri mydir = Uri.parse("file://"+filePath);
        intent.setDataAndType(mydir,"application/*");    // or use */*
        context.startActivity(intent);

    }

    //1FEB set it to old
    public static void openFolderOld(Context mContext, String filePath) {
        printLog("Folder Path===>>>", filePath);
        File file = ContextCompat.getExternalFilesDirs(mContext, (String) null)[0];
        Uri parse = Uri.parse(filePath);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(parse, "resource/folder");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.setFlags(1073741825);
        ((Activity) mContext).startActivity(Intent.createChooser(intent, "Meri Dairy"));

    }

    public static String getDeviceId(Context mContext) {
        String deviceId = md5(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)).toUpperCase();
        deviceId = md5(deviceId).toUpperCase();
        return deviceId;
    }

    public static void openPdfFileOLD(Context mContext, File pdfFile) {
        // Uri selectedUri = Uri.fromFile(pdfFile);
        System.out.println("pdfFile==========>>>>>" + pdfFile.getAbsoluteFile());
        Uri selectedUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        /*if (SDK_INT > Build.VERSION_CODES.P) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.setData(selectedUri);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, selectedUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else*/
        if (SDK_INT >= Build.VERSION_CODES.R) {

            if (Environment.isExternalStorageManager()) {

                intent.setData(selectedUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(intent);
//                intent.setDataAndType(Uri.parse(pdfFile.getAbsolutePath()), "application/pdf");
//                intent = Intent.createChooser(intent, "Open File");
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Intent intentt = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setDataAndType(Uri.parse(pdfFile.getAbsolutePath()), "application/pdf");
                intentt = Intent.createChooser(intent, "Open File");
               // intentt.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intentt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentt.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(intentt);
            }

        } else {
            intent.setDataAndType(Uri.parse(pdfFile.getAbsolutePath()), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                ((Activity) mContext).startActivity(intent);
            }

        }


    }


    public static void openPdfFile(Context mContext, File pdfFile) {
        // Uri selectedUri = Uri.fromFile(pdfFile);
        System.out.println("pdfFile==========>>>>>" + pdfFile.getAbsoluteFile());
        Uri selectedUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", pdfFile);


       // Uri path = Uri.fromFile(pdfFile.getAbsoluteFile());
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
       // Intent pdfOpenintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pdfOpenintent.setDataAndType(selectedUri, "application/pdf");
     //   pdfOpenintent = Intent.createChooser(pdfOpenintent, "Open File");
        try {
            mContext.startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e){

        }

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//
//            intent.setDataAndType(Uri.parse(pdfFile.getAbsolutePath()), "application/pdf");
//            intent = Intent.createChooser(intent, "Open File");
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
//                ((Activity) mContext).startActivity(intent);
//            }
//
//        }


    }

    public static void printLog(String tag, String message) {
        Log.e(tag + "===>>>>", message);
    }

    public static void printLogln(String message) {
        Log.e(">>>>", message);
    }

    private void scheduleNotification(Notification notification, int delay) {
      /*  Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;*/
    }

    private Notification getNotification(String content, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "");
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_pdf);
        builder.setAutoCancel(true);
        builder.setChannelId("");
        return builder.build();
    }

    public static void showSettingsDialog(Activity context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.dialog_permission_title));
        builder.setMessage(context.getResources().getString(R.string.dialog_permission_message));
        builder.setPositiveButton(context.getResources().getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(context);
            }
        });
        builder.setNegativeButton(context.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public static void shareApp(Context context) {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.InstallMeriDairy));
        String app_url = " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        printLog("app_url", app_url);
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.InstallMeriDairy) + "  " + app_url);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public static void createFileOnSDCard(Context context, String folder, String sFileName, String sBody) {
        try {

            File root = new File(Environment.DIRECTORY_DOWNLOADS + "/MeriDairy/" + folder + "/");
            root.mkdirs();

            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            showToast(context, "Saved file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getLocationPoint(double latitude, double longtitude) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longtitude);
        return startPoint;
    }

    public static Float getFloatValuFromInputText(String text) {
        float result = 0;
        try {
            result = Float.parseFloat(text);
        } catch (NumberFormatException e) {
            //Log it if needed
            result = 0;
        }
        return result;
    }

    public static Intent getCameraIntent(Context mContext) {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerAcitvity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        // startActivityForResult(intent, REQUEST_IMAGE);

        return intent;
    }

    public static Intent getGalleryIntent(Context mContext) {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_GALLERY_IMAGE);
        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);
        //startActivityForResult(intent, REQUEST_IMAGE);
        return intent;
    }


    public static void openSettings(Activity context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, 101);
    }

    public static void getShiftAccordingTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 15) {
            strSession = "morning";
        } else if (timeOfDay >= 15 && timeOfDay < 24) {
            strSession = "evening";
        }

    }


    public static String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 1000 + r.nextInt(1000);

        return orderId;
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb) return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb) return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra) return df.format(size / sizeGb) + " Gb";

        return "";
    }

    public static boolean whatsappInstalledOrNot(Context mContext, String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            System.out.println("orientation: %s===" + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    //method for get Image PATH
    public static String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static boolean isNetworkAvaliable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //showToast(context, context.getResources().getString(R.string.oops_denied_permission));
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }

    public static void goNextClass(Context context, Class className) {

        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        // ((Activity) context).finish();
    }

    public static void sendMessageTOAnotherApp(Context mContext, String mobileNo, String smsContent) {
        PackageManager packageManager = mContext.getPackageManager();
        if (isPackageInstalled("com.b2infosof.meridairysms", packageManager)) {
            final Intent intent = new Intent();
            intent.setAction(mContext.getPackageName());
            intent.putExtra("meridairy_broadcast", mContext.getPackageName());
            intent.putExtra("mobile_no", mobileNo);
            intent.putExtra("smsContent", smsContent);
            intent.setComponent(new ComponentName("com.b2infosof.meridairysms", "com.b2infosof.meridairysms.SmsReceiver"));
            mContext.sendBroadcast(intent);
            showToast(mContext, mContext.getString(R.string.Message_Sent_Successfully));
        } else {
            installMeridairySMSApp(mContext);
        }
    }

    public static boolean installMeridairySMSApp(Context mContext) {
        boolean isInstalled = false;
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        PackageManager packageManager = mContext.getPackageManager();

        if (isPackageInstalled("com.b2infosof.meridairysms", packageManager)) {
            isInstalled = true;
        } else {
            showToast(mContext, "Please install" + mContext.getString(R.string.app_name) + " " + mContext.getString(R.string.message) + " " + mContext.getString(R.string.app));
            InstallAPK downloadAndInstall = new InstallAPK();
            downloadAndInstall.setContext(mContext);
            downloadAndInstall.execute(AppUpdateServerUrl);
        }
        return isInstalled;
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean isInstall = false;
        try {
            packageManager.getPackageInfo(packageName, 0);
            isInstall = true;
        } catch (PackageManager.NameNotFoundException e) {
            if (e.equals("com.b2infosof.meridairysms"))

                isInstall = false;
            printLog("sms app isInstall ", isInstall + "");
        }

        return isInstall;
    }

    public static void sendMultiMessageTOAnotherApp(Context mContext, List<String> mobileNo, String smsContent) {

        final Intent intent = new Intent();
        intent.setAction(mContext.getPackageName());

        intent.putExtra("meridairy_broadcast", mContext.getPackageName());
        intent.putExtra("mobile_no", mobileNo.toString());
        intent.putExtra("smsContent", smsContent);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName("com.b2infosof.meridairysms", "com.b2infosof.meridairysms.SmsReceiver"));
        mContext.sendBroadcast(intent);
    }

    public static void goNextFragmentReplace(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.dairy_container, fragment);
        fragmentTransaction.commit();
    }

    public static void goNextFragmentAdd(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.add(R.id.dairy_container, fragment);
        fragmentTransaction.commit();
    }

    public static void goNextFragmentWithBackStack(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.dairy_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void goNextFragmentFromDeliveryBoy(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void goNextFragmentAddBackStack(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.add(R.id.dairy_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void downloadFile(Context mContext, String url, String folderName) {
        System.out.println("url>>>>" + url);
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading...");
        progressDialog.show();

        String path = Environment.getExternalStorageDirectory().getPath() + "/" + "MeriDairy" + "/" + folderName + "/";
        System.out.println("path==" + path);
        File folder = new File(path);
        System.out.println("folder.exists()==" + folder.exists());


        if (!folder.exists()) {
            folder.mkdirs();
        }
        String filename = "sample" + ".xlsx";
        printLog("folder>>>", folder.getAbsolutePath());

        printLog("fileName==", filename);


        // Create request for android download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(folderName + " Downloaded");
        request.setDescription(folderName + "Exporting data...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//set the local destination for download file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MeriDairy/ChartSample/" + filename);
        //  request.setDestinationInExternalPublicDir(path, filename);
        request.setMimeType("*/*");
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
        progressDialog.dismiss();

        // showToast(mContext, folderName + " " + filename + " Download successfully!");

    }

    public static String convertUTF8ToString(String s) {
        String out = "";
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            out = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return out;
    }

    public static String getDateFromTimeStamp(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    // convert internal Java String format to UTF-8
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String convertStringToUTF8(String s) {
        String out = "";
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        return out;
    }

    public static void goBuyerNextFragment(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }

    public static void goBuyerNextFragmentWithBackStack(Context context, Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static String decimalFormat(double amount) {
        String formatted = "";
        if (amount >= 10000) {
            DecimalFormat formatter = new DecimalFormat("##,###");
            formatted = formatter.format(amount);

        } else if (amount <= 10000) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            formatted = formatter.format(amount);

        }
        return formatted;

    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        canvas.setBitmap(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static JSONObject bundleToJson(Bundle bundle) {
        JSONObject object = new JSONObject();
        if (bundle == null) {
            return null;
        }

        for (String key : bundle.keySet()) {

            try {
                object.put(key, bundle.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return object;
    }

    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    public static String nullCheckFunction(String text) {
        String value = "";
        if (text == null || text.trim().length() == 0 || text.trim().equalsIgnoreCase("null") || TextUtils.isEmpty(text) || text.isEmpty() || TextUtils.equals(text, "null")) {
            text = "";
            return text;
        }

        try {
            value = text;

        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }

        return value;
    }

    public static float nullCheckFloatNumber(String text) {
        System.out.println("nullCheckFloatNumber>>>>" + text);

        float value = 0;
        if (text == null || text.trim().length() == 0 || text.trim().equalsIgnoreCase("null") || TextUtils.isEmpty(text) || text.isEmpty() || TextUtils.equals(text, "null")) {
            value = 0;

        } else {
            value = Float.parseFloat(text);
        }

        return value;
    }

    public static double checkNumberFormat(String text) {
        System.out.println("checkNumberFormat>>>>" + text);

        double value = 0;
        if (text == null || text.trim().length() == 0 || text.trim().equalsIgnoreCase("null") || TextUtils.isEmpty(text) || text.isEmpty() || TextUtils.equals(text, "null")) {
            value = 0;

        } else {
            value = Float.parseFloat(text);
        }

        return value;
    }

    public static boolean appInstalledOrNot(Context mContext, String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static boolean checkImageResource(Context ctx, ImageView imageView, int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources().getDrawable(imageResource, ctx.getTheme()).getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource).getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }
        return result;
    }
    //Comapare Imageview and Drawable

    public static void showAlertBox(Context mContext, String msg) {

        Dialog dialog;
        dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.layout_alert_dialog);

        TextView text = dialog.findViewById(R.id.textViewMsg);
        text.setText(msg);


        if (!((Activity) mContext).isFinishing()) {

            dialog.show();
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog!=null){
                        dialog.dismiss();
                    }
                }
            }, 2000);

        }


    }

    public static void showAlertWithButton(Context context, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false).setPositiveButton(context.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                // do nothing
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();


    }

    public static void showToast(Context context, String message) {
       /* ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });*/

        Runnable runnable = new Runnable() {
        @Override
        public void run() {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });


        }
    };
 new Thread(runnable).start();


//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            public void run() {
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

    }


    public static void stopSMSReciver(Context context) {
        // Stop SMS Reciver
        ComponentName receiver = new ComponentName(context, SMSReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static void enableSMSReciver(Context context) {

        ComponentName receiver = new ComponentName(context, SMSReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    //method for click images or get from gallary
    public static void selectImage(final Activity activity) {
        final CharSequence[] option1 = {"Take Photo", "Choose from Gallery", "Take selfie", "Select Multiple", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle("Add Photo!");
        builder.setItems(option1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (option1[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(intent, 101);
                } else if (option1[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*"); // intent.setType("video/*"); to select videos to upload
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 201);
                } else if (option1[item].equals("Take selfie")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT,0);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    activity.startActivityForResult(intent, 301);

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static void sendWhatsUpMessage(Context context, String phone, String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PackageManager packageManager = context.getPackageManager();
                try {
                    String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    //   i.setPackage("com.whatsapp");
                    //  i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        context.startActivity(i);
                    }


                } catch (Exception e) {
                    System.out.println("whatsup error=====>>>>>" + e.toString());
                    e.printStackTrace();


                }
            }
        });


    }

    public static String nameFirstLatterCapitalize(String name) {
        String name1 = "";
        if (!name.equals("")) {
            System.out.println("name===>>" + name);
            name1 = name;
            name1 = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            System.out.println("nameCapitalized>>" + name1);
        }

        return name1;
    }

    public static String getOneMonthBackDate() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String formattedDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());


        return formattedDate;
    }

    public static String getSimpleDate() {
        String formattedDate = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static ArrayList<String> getMonthList() {
        ArrayList<String> monthList = new ArrayList<>();

        monthList.add("Jan");
        monthList.add("Feb");
        monthList.add("Mar");
        monthList.add("Apr");
        monthList.add("May");
        monthList.add("Jun");
        monthList.add("Jul");
        monthList.add("Aug");
        monthList.add("Sep");
        monthList.add("Oct");
        monthList.add("Nov");
        monthList.add("Dec");
        return monthList;
    }

    public static ArrayList<String> getMonthNameList() {
        ArrayList<String> monthList = new ArrayList<>();

        monthList.add("January");
        monthList.add("Febuary");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("Jun");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
        return monthList;
    }

    public static void getCalanderDate(Context mContext, final TextView tvdate) {


        Calendar c2 = Calendar.getInstance();
        int mYear, mMonth, mDay;
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String formattedDate = "";
                ArrayList<String> monthList = getMonthList();

                String month = "";
                for (int i = 0; i < monthList.size(); i++) {
                    if (monthOfYear == i) {
                        month = monthList.get(i);
                    }
                }
                String day = "";
                //  month = checkDigit(monthOfYear + 1);
                day = checkDigit(dayOfMonth);

                formattedDate = day + "-" + month + "-" + year;
                tvdate.setText(formattedDate);
                System.out.println("formattedDate====>>" + formattedDate);
                Constant.SelectedDate = formattedDate;
            }

        }, mYear, mMonth, mDay);

        datePickerDialog2.show();

    }

    public static void getDate(Context mContext,final TextView tvdate) {

        Calendar c2 = Calendar.getInstance();
        int mYear, mMonth, mDay;
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                 String formattedDate = "";
                ArrayList<String> monthList = getMonthList();

                String month = "";
                for (int i = 0; i < monthList.size(); i++) {
                    if (monthOfYear == i) {
                        month = monthList.get(i);
                    }
                }
                String day = "";
                //  month = checkDigit(monthOfYear + 1);
                day = checkDigit(dayOfMonth);

                formattedDate = day + "-" + month + "-" + year;
                tvdate.setText(formattedDate);
                System.out.println("formattedDate====>>" + formattedDate);
            }

        }, mYear, mMonth, mDay);

        datePickerDialog2.show();
    }

    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public static boolean isExternalStorage() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String getAppVersionInfo(Context mContext) {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String removeNonNumeric(String text) {

        if (text == null || text.trim().length() == 0 || text.trim().equalsIgnoreCase("null") || TextUtils.isEmpty(text) || text.isEmpty() || TextUtils.equals(text, "null")) {
            text = "";
        }
        text = text.replaceAll("[^\\d.]", "");
        return text;


    }

    //method for get Image URI
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public static void loadGoogleInterstitialAd(Context mContext) {
//        InterstitialAd adView;
//
//        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
//        String deviceId = md5(android_id).toUpperCase();
//        Log.i("device id====", deviceId);

//        //   MobileAds.initialize(mContext, mContext.getString(R.string.app_id));
//        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        //  MobileAds.initialize(mContext,"33BE2250B43518CCDA7DE426D04EE231");
//
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        InterstitialAd.load(mContext, mContext.getString(R.string.interstitialAd_unit_id), adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                // The mInterstitialAd reference will be null until
//                // an ad is loaded.
//                interstitialAd.show((Activity) mContext);
//                Log.i(TAG, "onAdLoaded");
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                // Handle the error
//                Log.i(TAG, loadAdError.getMessage());
//
//            }
//        });
//    }

    public static void pickContactList(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, PICK_CONTACT);

    }

//    public static void LoadGoogleVideoAd(final Context mContext, RewardedInterstitialAd mRewardedVideoAd) {
//
//       /* final RewardedInterstitialAd finalMRewardedVideoAd = mRewardedVideoAd;
//        mRewardedVideoAd = MobileAds.getRequestConfiguration(mContext);
//        MobileAds.initialize(mContext,
//                mContext.getString(R.string.admob_app_id));
//        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int errorCode) {
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdLoaded() {
//                if (finalMRewardedVideoAd.isLoaded()) {
//                    finalMRewardedVideoAd.show();
//                }
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
//            }
//        });
//
//        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
//                new AdRequest.Builder().build());
//*/
//    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void showAlertBoxwithIntent(Context context, String msg, Class class1) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setNegativeButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.dismiss();
                Intent intent = new Intent(context, class1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

        alertDialogBuilder.show();

    }

    public static void showToastIntent(Context context, String msg, Class class1) {
        showToast(context, msg);
        Intent intent = new Intent(context, class1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    // Method to show alert dialog
    public static void showAlert(String message, Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false).setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                // do nothing
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    // Method to show alert dialog
    public static void showAlertWithTitle(String message, Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(context.getString(R.string.bluetooth_connection)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        if (!((Activity) context).isFinishing()) {
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public static Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
            }
        }

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }

    public static void reverseTimer(int Seconds, TextView tv) {
        CountDownTimer cTimer = new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", seconds));
                if (seconds == 1) {
                    cancel();
                    tv.setText("00");
                }

            }

            public void onFinish() {
                cancel();
                tv.setText("00");
            }
        };
        cTimer.start();


    }

    public static void captureImage(Bitmap bitmap, Intent data, ImageView imageView, String imagepath) {
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            /*Uri tempUri =getImageUri(activity.getApplicationContext(), bitmap);
            imagepath =getPath(tempUri, activity);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSnackBar(final View coordinatorLayout, String msg) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

// Changing message text color
        snackbar.setActionTextColor(Color.RED);

// Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void hideKeyboardForFocusedView(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    // Create First Word Capital

    public static String timeFormat12Hour(String strdate) {
        String newdate = "";


        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss a");
            Date dateObj = sdf.parse(strdate);
            newdate = sdf.format(dateObj);
            System.out.println(dateObj);
            SimpleDateFormat parseFormat = new SimpleDateFormat("K:mm:ss a");

            String formatdate = parseFormat.format(dateObj);
            Date datenew = new Date();
            //   Log.d("formatdate====",formatdate);
            datenew = parseFormat.parse(formatdate);
            newdate = parseFormat.format(datenew);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return newdate;
    }

    public static String dateDDMMYY(String strdate) {
        String newdate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = new Date();
            parsedDate = sdf.parse(strdate);
            newdate = sdf.format(parsedDate);
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String formatdate = parseFormat.format(parsedDate);
            date = parseFormat.parse(formatdate);
            newdate = parseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return newdate;
    }

    public static String dateFormat(String strdate) {
        String newdate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = new Date();
            parsedDate = sdf.parse(strdate);
            newdate = sdf.format(parsedDate);
            //  Log.d("newdate====",newdate);
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd MMMM,yyyy");
            Date date = new Date();


            String formatdate = parseFormat.format(parsedDate);

            //   Log.d("formatdate====",formatdate);
            date = parseFormat.parse(formatdate);
            newdate = parseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //  return new SimpleDateFormat("MM/dd/yyyy").format(d).toString();
        return newdate;
    }

    public static String convertJsonDate(String jsondate) {
        jsondate = jsondate.replace("/Date(", "").replace(")/", "");
        long time = Long.parseLong(jsondate);
        Date d = new Date(time);

        //  return new SimpleDateFormat("MM/dd/yyyy").format(d).toString();
        return new SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    public static String strDateToTimeStrempDDMMMYY(String strdate) {


        String timeStrmp = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date date2 = sdf.parse(strdate);
            timeStrmp = String.valueOf(date2.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("strdate====>>>" + strdate + "      timeStrmp====>>>" + timeStrmp);
        return timeStrmp;
    }

    public static String currentTimeDateToTimeStremp() {

        Long tsLong = System.currentTimeMillis() / 1000;
        String strToTime = tsLong.toString();
        return strToTime;
    }

    public static String strDateToTimeStampDDMMYY(String strdate) {


        String timeStrmp = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date2 = sdf.parse(strdate);
            timeStrmp = String.valueOf(date2.getTime() / 1000);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("strdate====>>>" + strdate + "      timeStrmp====>>>" + timeStrmp);
        return timeStrmp;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dataFormat(String strdate) {
        String newdate = "";
        try {
            // 2019-03-09 12:16:13
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = sdf.parse(strdate);
            newdate = sdf.format(parsedDate);
            SimpleDateFormat parseFormat = new SimpleDateFormat("E dd MMMM,yyyy hh:mm:ss");
            String formatdate = parseFormat.format(parsedDate);
            Date date = parseFormat.parse(formatdate);
            newdate = parseFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newdate;
    }

    public static Bitmap textAsBitmap(String text, float textSize, Context mContext) {

        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);

        paint.setAntiAlias(true);
        // paint.setTextAlign(Paint.Align.LEFT);

        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.colorBlack));
        // paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(7f);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        float baseline = (int) (-paint.ascent() + 3f); // ascent() is negative

        StaticLayout staticLayout = new StaticLayout(text, 0, text.length(), paint, 400, Layout.Alignment.ALIGN_CENTER, 1.0f, 1.0f, false);
        Bitmap image = Bitmap.createBitmap(staticLayout.getWidth(), staticLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        staticLayout.draw(canvas);
        return image;

    }

    //Edited ImageCapture
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        return BitmapFactory.decodeFile(path, options);

    }

    public static Bitmap decodeFile(String path, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeFile(path, options);

        return unscaledBitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    public static Bitmap ShrinkBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

        final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

        if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("serif", customFontTypeface);
            try {
                final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                System.out.println("Can not set custom font== " + customFontFileNameInAssets + " instead of =" + defaultFontNameToOverride);
            }
        }
    }

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static String getMonthFormat(int monthInput) {
        String strMonth = "";
        if (monthInput < 10) {
            strMonth = "0" + monthInput;
            System.out.println("strMonth====" + strMonth);

        } else {
            strMonth = String.valueOf(monthInput);
        }
        return strMonth;
    }

    public static String getWebDateYY_MM_DD() {
        String formattedDate = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public static Bitmap getBitmapFromURL(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            System.out.println("Exception while downloading url====" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void createFolder(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void sendWhatsAppMessage(Context context, String whatsappMsg) {
        String smsNumber = "+919166900279";
        boolean isWhatsappInstalled = whatsappInstalledOrNot(context, "com.whatsapp");
        if (isWhatsappInstalled) {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra(Intent.EXTRA_TEXT, whatsappMsg);
            //   sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
            context.startActivity(sendIntent);
        } else {

        }
    }

    public enum ScalingLogic {
        CROP, FIT
    }

    public static CharSequence toCaretString(CharSequence s, boolean keepNewline, int length) {
        boolean found = false;
        for (int pos = 0; pos < length; pos++) {
            if (s.charAt(pos) < 32 && (!keepNewline || s.charAt(pos) != '\n')) {
                found = true;
                break;
            }
        }
        if (!found) return s;
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int pos = 0; pos < length; pos++)
            if (s.charAt(pos) < 32 && (!keepNewline || s.charAt(pos) != '\n')) {
                sb.append('^');
                sb.append((char) (s.charAt(pos) + 64));
                sb.setSpan(new BackgroundColorSpan(caretBackground), sb.length() - 2, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                sb.append(s.charAt(pos));
            }
        return sb;
    }

    public static MachineData getMachineData(String readMessage) {
        MachineData machineData = new MachineData();
        readMessage = readMessage.replace(" ", "");
        printLog("json", readMessage);

        try {
            JSONObject jsonObject = new JSONObject(readMessage.replace(" ", ""));
            String fat = "", weight = "", snf = "", clr = "", den = "";
            if (jsonObject.has("fat")) {
                fat = jsonObject.getString("fat").replace(" ", "");
                float fatD = Math.round(Float.parseFloat(fat));
                fat = String.valueOf(fatD);
                printLog("fatD", fatD + "");
                printLog("fatstr", fat + "");

            }
            if (jsonObject.has("Weight")) {
                weight = jsonObject.getString("Weight").replace(" ", "");
            }
            if (jsonObject.has("snf")) {
                snf = jsonObject.getString("snf").replace(" ", "");
                float snfD = Math.round(Float.parseFloat(snf));
                snf = String.valueOf(snfD);
                printLog("snfD", snfD + "");
            }
            if (jsonObject.has("den")) {
                den = jsonObject.getString("den");

            }
            if (jsonObject.has("clr")) {
                clr = jsonObject.getString("clr").replace(" ", "");

            }
            machineData = new MachineData(weight, fat, snf, clr, den);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return machineData;

    }

}
