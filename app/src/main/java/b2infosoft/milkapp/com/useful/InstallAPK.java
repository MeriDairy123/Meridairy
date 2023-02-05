package b2infosoft.milkapp.com.useful;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import b2infosoft.milkapp.com.BuildConfig;

import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class InstallAPK extends AsyncTask<String, Void, Void>  {

    ProgressDialog progressDialog;
    int status = 0;
    String path = "";
    File internalStorageDirectory;
    File myDir;
    File outputFile;
    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context;
        internalStorageDirectory = mContext.getFilesDir();
        try {

            if (Build.VERSION.SDK_INT > 29) {
              //  myDir = new File(mContext.getExternalFilesDir(mContext.getExternalFilesDir(null).getAbsolutePath()) + "/MeriDairy");
                myDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/MeriDairy");
            }else{
                myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/MeriDairy");
            }

        //    myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/MeriDairy");

            printLog("outputFile=exist=" ,myDir.exists()+"");
            if (!myDir.exists()){
                myDir.mkdirs();
            }

            printLog("outputFile=exist=" ,myDir.exists()+"");
            path=myDir.getAbsolutePath();
            outputFile = new File(myDir, "/MeriDairySMSApp.apk");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
        } catch (IOException e) {
            printLog("file error",e.getMessage());
            e.printStackTrace();
        }
    }

    public void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading...");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();


            FileOutputStream fos = new FileOutputStream(outputFile);
           /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fos = mContext.openFileOutput(outputFile.getName(), mContext.MODE_PRIVATE);
            } else {
                fos = mContext.openFileOutput(outputFile.getName(), mContext.MODE_WORLD_READABLE| mContext.MODE_WORLD_WRITEABLE);
            }*/
            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.flush();
            fos.close();
            is.close();


        } catch (FileNotFoundException fnfe) {
            status = 1;
            Log.e("File", "FileNotFoundException! " + fnfe);
        } catch (Exception e) {
            Log.e("UpdateAPP", "Exception " + e);
        }
        return null;
    }

    public void onPostExecute(Void unused) {
        progressDialog.dismiss();
        if (status == 1) {
            showToast(mContext, "FileNotFoundException! ");
        } else {
            System.out.println("myDir==sucess====" + myDir.getAbsolutePath());
            System.out.println("outputFile==sucess==" + outputFile.getAbsolutePath());
            showToast(mContext, "Download success  \n" + outputFile.getAbsoluteFile());

            if (outputFile.exists()) {

                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkURI = FileProvider.getUriForFile(mContext,
                            BuildConfig.APPLICATION_ID + ".provider", outputFile.getAbsoluteFile());


                    System.out.println("apkURI==sucess==" + Objects.requireNonNull(apkURI.getPath()));
                    Uri uri = Uri.fromFile(outputFile);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                  //intent.setData(apkURI);


                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                  //  intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    Uri uri = Uri.fromFile(outputFile);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");

                }

                mContext.startActivity(intent);
                ((Activity) mContext).finish();

            }
        }
    }
}