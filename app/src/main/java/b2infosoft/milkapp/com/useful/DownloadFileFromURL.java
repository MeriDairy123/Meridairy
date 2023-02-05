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


public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    File outPutFile;
    Context mContext;
    ProgressDialog pDialog;
    String path = "";
    String folderName;

    OnDownLoadListener onDownLoadListener;


    public DownloadFileFromURL(Context context, OnDownLoadListener onDownLoadListener2) {
        this.mContext = context;
        this.onDownLoadListener = onDownLoadListener2;
    }

    public void initURL(String folderName, String filePath) throws IOException {
        this.folderName = folderName;
        File createPDFFile = PDFUtills.createPDFFile(this.mContext, folderName, filePath);
        this.outPutFile = createPDFFile;
        this.path = createPDFFile.getAbsolutePath();
        System.out.println("outPutFile>>>>" + this.path);
        ProgressDialog progressDialog = new ProgressDialog(this.mContext);
        pDialog = progressDialog;
        progressDialog.setCancelable(false);
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
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();
        System.out.println("outPut Folder path>>>"+outPutFile.getParent());
        System.out.println("outPutFilemohit>>>"+outPutFile.toString());
        onDownLoadListener.onDownLoadComplete(this.outPutFile.getParent(), this.outPutFile);
    }

}
