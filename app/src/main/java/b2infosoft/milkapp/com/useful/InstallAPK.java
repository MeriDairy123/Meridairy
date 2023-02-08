package b2infosoft.milkapp.com.useful;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static b2infosoft.milkapp.com.useful.UtilityMethod.openFolder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.BuildConfig;

public class InstallAPK extends AsyncTask<String, Integer, String> {

    ProgressDialog progressDialog;
    private ProgressDialog mPDialog;
    int status = 0;
    String path = "";
    File internalStorageDirectory;
    File files;
    File outputFile;
    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPDialog = new ProgressDialog(mContext);
                mPDialog.setMessage("Please wait...");
                mPDialog.setIndeterminate(true);
                mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mPDialog.setCancelable(false);
                mPDialog.show();
            }
        });


//        internalStorageDirectory = mContext.getFilesDir();
//        try {
//
//            if (Build.VERSION.SDK_INT > 29) {
//              //  myDir = new File(mContext.getExternalFilesDir(mContext.getExternalFilesDir(null).getAbsolutePath()) + "/MeriDairy");
//                myDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/MeriDairy");
//            }else{
//                myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/MeriDairy");
//            }
//
//        //    myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/MeriDairy");
//
//            printLog("outputFile=exist=" ,myDir.exists()+"");
//            if (!myDir.exists()){
//                myDir.mkdirs();
//            }
//
//            printLog("outputFile=exist=" ,myDir.exists()+"");
//            path=myDir.getAbsolutePath();
//            outputFile = new File(myDir, "/MeriDairySMSApp.apk");
//            if (outputFile.exists()) {
//                outputFile.delete();
//            }
//            outputFile.createNewFile();
//        } catch (IOException e) {
//            printLog("file error",e.getMessage());
//            e.printStackTrace();
//        }
    }

    public void onPreExecute() {
        if (mPDialog != null)
            mPDialog.show();
    }

    @Override
    protected String  doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int lenghtOfFile = c.getContentLength();

         //   String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
            String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            boolean isCreate = file.mkdirs();
            File outputFile = new File(file, "MeriDairySMSApp.apk");
            if (outputFile.exists()) {
                boolean isDelete = outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1;
            long total = 0;
            while ((len1 = is.read(buffer)) != -1) {
                total += len1;
                fos.write(buffer, 0, len1);
                publishProgress((int) ((total * 100) / lenghtOfFile));
            }
            fos.close();
            is.close();
            if (mPDialog != null)
                mPDialog.dismiss();

            this.files = file;
//            installApk(PATH);
//            OpenNewVersion(PATH);

        } catch (FileNotFoundException fnfe) {
           // status = 1;
            Log.e("File", "FileNotFoundException! " + fnfe);
        } catch (Exception e) {
            Log.e("UpdateAPP", "Exception " + e);
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mPDialog != null) {
            mPDialog.setIndeterminate(false);
            mPDialog.setMax(100);
            mPDialog.setProgress(values[0]);
        }
    }


    public void onPostExecute(String result) {
        if (mPDialog != null)
            mPDialog.dismiss();
        if (result != null){
            Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(mContext, "File Downloaded", Toast.LENGTH_SHORT).show();
            openFolder(mContext, files.getAbsolutePath());
        }


    }


    void OpenNewVersion(String location) {
        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",new File(location+"MeriDairySMSApp.apk"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

    private void installApk(String PATH) {
        try {
            //String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();


            if (checkPermission()) {
                File file = new File(PATH + "/MeriDairySMSApp.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ((Activity) mContext).startActivity(intent);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            } else {
                requestPermission();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(mContext, READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }


}