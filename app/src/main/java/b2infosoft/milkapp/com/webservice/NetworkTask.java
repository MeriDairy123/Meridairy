package b2infosoft.milkapp.com.webservice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.SplashActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import okhttp3.logging.HttpLoggingInterceptor;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.user_token;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bodyToString;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public abstract class NetworkTask extends AsyncTask<String, String, String> {

    public static final int GET_TASK = 1;
    public static final int POST_TASK = 2;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static MediaType JSONMediaType = MediaType.parse("application/json; charset=utf-8");
    private static String TAG = "Rocks>>>>";
    private final int SOCKET_TIMEOUT = 120;
    public String url;
    public String json = "";
    public int task;
    public Handler mHandler = new Handler(Looper.getMainLooper());
    OkHttpClient client;
    Request request;
    Context mContext;
    SessionManager sessionManager;
    RequestBody body = new FormEncodingBuilder().build();
    private String processMessage = "Processing...";
    private Dialog pDlg = null;
    private boolean DialogYesNo;

    public NetworkTask(int task, Context context, String message, boolean dialogYesNo) {
        this.mContext = context;
        this.task = task;
        this.DialogYesNo = dialogYesNo;
        processMessage = message;
        sessionManager = new SessionManager(mContext);
        initClient();
    }

    public void initClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        client = new OkHttpClient();
        client.networkInterceptors();
        client.interceptors();
        client.setConnectTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    protected void onPreExecute() {
        // hideKeyboard()
        if (DialogYesNo) {
            if (!((Activity) mContext).isFinishing()){
                showProgressDialog();
            }
        }
    }

    public void addRequestBody(RequestBody requestBody) {
        body = requestBody;
    }


    public String doGetRequest() throws IOException {
        request = new Request.Builder()
                .url(url)
                .header(user_token, sessionManager.getValueSesion(user_token))
                .header("Accept-Charset", "utf-8")
                .header("User-Agent", "PostmanRuntime/7.20.1")
                .header("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String doPostRequest() throws IOException {
        request = new Request.Builder()
                .url(url)
                .header("Accept-Charset", "utf-8")
                .header("User-Agent", "PostmanRuntime/7.20.1")
                .header("Content-Type", "application/json")
                .header(user_token, sessionManager.getValueSesion(user_token))
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String doInBackground(String[] urls) {
        if (isNetworkAvaliable(mContext)) {
            try {
                url = urls[0];
                String response = "";
                switch (task) {
                    case 1:
                        response = doGetRequest();
                        break;
                    case 2:
                        response = doPostRequest();
                        break;
                }
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            return mContext.getString(R.string.you_are_not_connected_to_internet);
        }
        return "";
    }


    @Override
    protected void onPostExecute(String response) {

        if (pDlg != null && DialogYesNo) {
            if (pDlg.isShowing()) {
                pDlg.dismiss();
            }
        }
       TAG = mContext.getClass().getCanonicalName();
       printLog("user-token===>>>" , sessionManager.getValueSesion(SessionManager.user_token));
       printLog(TAG, "=Parms===>>>" + bodyToString(body));
       printLog(TAG, "===response==>>>>>" + url  + "==>>>>>" + response);
        if (response.isEmpty()) {
            showToast(mContext, mContext.getString(R.string.Either_there_was_network_issue_orsomeerror_occurred));
        } else if (response.equalsIgnoreCase(mContext.getString(R.string.you_are_not_connected_to_internet))) {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        } else {
            String struserExpire = "{\"user_status\":\"0\",\"user_status_message\":\"Invalid Token Number\"}";
            if (response.equalsIgnoreCase(struserExpire)) {
                showToast(mContext, mContext.getString(R.string.invalid_login_message));
                sessionManager.logoutUser();
                Intent i = new Intent(mContext, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(i);

            } else {
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            handleResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... text) {


    }



    public abstract void handleResponse(String response) throws JSONException;

    private void showProgressDialog() {

        if (processMessage.equalsIgnoreCase("Please wait...")) {
            processMessage = "Loading...";
        }

        pDlg = new Dialog(mContext, R.style.Theme_AppCompat_Dialog_Alert);
        Drawable d = new ColorDrawable(Color.TRANSPARENT);
        d.setAlpha(200);
        Objects.requireNonNull(pDlg.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        pDlg.getWindow().setBackgroundDrawable(d);
        pDlg.setContentView(R.layout.progress_dialog);
        TextView tvMessage = pDlg.findViewById(R.id.tvMessage);
        tvMessage.setText(processMessage);
        pDlg.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            pDlg.create();
        }
        pDlg.show();
    }
}