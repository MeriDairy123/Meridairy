package b2infosoft.milkapp.com.useful;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import b2infosoft.milkapp.com.Interface.OnDownLoadListener;

import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;


public class DownloadMultipleFileFromURL extends AsyncTask<String, String, String> {
    File outPutFile;
    Context mContext;
    ProgressDialog pDialog;
    String path = "";
    String folderName;
    OnDownLoadListener onDownLoadListener;
    int totalCount = 0, count;

    public DownloadMultipleFileFromURL(Context context, OnDownLoadListener onDownLoadListener) {
        mContext = context;
        this.onDownLoadListener = onDownLoadListener;

    }

    public void initURL(String folderName, String fileName, int totalcount, int count) throws IOException {
        this.folderName = folderName;
        outPutFile = createPDFFile(mContext, folderName, fileName);
        path = outPutFile.getAbsolutePath();
        this.totalCount = totalcount;
        this.count = count;
        pDialog = new ProgressDialog(mContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Downloading...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.show();

    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);


            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        pDialog.dismiss();
        if (totalCount > 1 && count == totalCount) {
            UtilityMethod.showToast(mContext, this.outPutFile.getName() + " File Download Successful Please check in Meri Dairy Folder\n " + this.outPutFile.getAbsolutePath());
            UtilityMethod.openFolder(mContext, outPutFile.getParent());
        } else if (totalCount == 1 & count == 1) {
            UtilityMethod.openPdfFile(this.mContext, outPutFile);
        }


    }


}
