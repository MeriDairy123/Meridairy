package b2infosoft.milkapp.com.useful;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

import b2infosoft.milkapp.com.Interface.OnDownLoadListener;

import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;

public class MyDownloadListener implements DownloadListener {
    //  private DownloadManager.Request mRequest;
    String folderName = "", fileName = "";
    ProgressDialog progressDialog;
    OnDownLoadListener onDownLoadListener;
    File pdfFile;
    private Context mContext;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;

    public MyDownloadListener(Context context, OnDownLoadListener onDownLoadListener) {
        mContext = context;
        this.onDownLoadListener = onDownLoadListener;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading...");

    }

    @Override
    public void onDownloadStart(String url, String folderName, String
            fileName, String mimetype, long contentLength) {
        progressDialog.show();
        mimetype = getMimeFromFileName(fileName);
        this.folderName = folderName;
        this.fileName = fileName;
        try {
            pdfFile = createPDFFile(mContext, folderName, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Function is called once download completes.
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Prevents the occasional unintentional call. I needed this.
                progressDialog.dismiss();
                if (mDownloadedFileID == -1)
                    return;
                onDownLoadListener.onDownLoadComplete(folderName, pdfFile);
                mDownloadedFileID = -1;
            }
        };
        // Registers function to listen to the completion of the download.
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName + " Downloaded");
        request.setDescription(fileName + "Downloading file...");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
      /*  String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("Cookie", cookie);*/
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//set the local destination for download file to a path within the application's external files directory
        Uri selectedUri = Uri.fromFile(pdfFile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // selectedUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
        }
        System.out.println("Dm  ===selectedUri>>>>" + selectedUri);
        request.setDestinationUri(selectedUri);
        // request.setDestinationInExternalFilesDir(mContext, Environment.getExternalStorageState(), folderName+fileName);
        request.setMimeType("application/pdf");

        request.setVisibleInDownloadsUi(true);
        mDownloadManager.enqueue(request);
        // Adds the request to the DownloadManager queue to be executed at the next available opportunity.
        mDownloadedFileID = mDownloadManager.enqueue(request);
        mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private String getMimeFromFileName(String fileName) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileName);
        System.out.println("ext===>>>" + ext);
        System.out.println("mimetype====ext>>>" + map.getMimeTypeFromExtension(ext));

        return map.getMimeTypeFromExtension(ext);
    }
}
