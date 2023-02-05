package b2infosoft.milkapp.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import b2infosoft.milkapp.com.BuildConfig;
import b2infosoft.milkapp.com.DeliveryBoy.DeliveryBoyMainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerDairyListActivity;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDeshBoardActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerUserGroupActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.ForceUpdateChecker;
import b2infosoft.milkapp.com.useful.InstallAPK;

import static b2infosoft.milkapp.com.appglobal.Constant.AppGooglePlayStoreUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class SplashActivity extends Activity implements ForceUpdateChecker.OnUpdateNeededListener {
    private static final int REQUEST_WRITE_PERMISSION = 555;
    Context mContext;
    float currentVersion = 0, latestVersion = 0;
    boolean isConnect;
    boolean isConnectedToInternet = false;
    SessionManager sessionManager;
    InstallAPK downloadAndInstall;
    String userGroupId = "";
    int waitTime = 1000;
    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(mContext.getString(R.string.app_name));
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        ImageView imgLogo = findViewById(R.id.imgLogo);

        VideoView videoView = findViewById(R.id.video);
        //  videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.meri_dairy_video));
        //  videoView.setVisibility(View.VISIBLE);
        //   videoView.start();
        //  videoView.setVisibility(View.VISIBLE);
        sessionManager = new SessionManager(mContext);
        isConnectedToInternet = ConnectivityReceiver.isConnected();
        currentVersion = Float.parseFloat(getCurrentVersion());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (!isConnectedToInternet) {
                            showOfflineDailog();
                        } else {

//                            appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
//                            checkUpdate();

                            new checkPlayStoreVersion().execute();
                        }
                    }
                }, 100);
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private String getCurrentVersion() {
        PackageManager manager = mContext.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = info.versionName;
        System.out.println("CurrentVersion---===>>>" + currentVersion);
        return currentVersion;
    }

    public void showOfflineDailog() {
        final Dialog pDlg = new Dialog(SplashActivity.this, R.style.MyAlertDialogStyle);
        pDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDlg.setContentView(R.layout.custom_splash_dialog);
        Button splash_btn_retry;
        TextView splash_msg, splash_btn_exit;
        splash_msg = pDlg.findViewById(R.id.splash_dialog_msg);
        splash_btn_retry = pDlg.findViewById(R.id.splash_btn_retry);
        splash_btn_exit = pDlg.findViewById(R.id.splash_btn_exit);
        splash_msg.setText(getString(R.string.you_are_not_connected_to_internet) + "\n" + getString(R.string.Are_you_want_to_work_offline));

        splash_btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDlg.dismiss();
                nextActivity();
            }
        });
        splash_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDlg.dismiss();
                finish();
            }
        });
        pDlg.setCancelable(false);
        pDlg.show();
        if (isConnect) {
            pDlg.dismiss();
        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(mContext.getString(R.string.newUpdateAvailable))
                .setMessage(mContext.getString(R.string.pleaseUpdateApp))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.UPDATE),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                redirectStore(updateUrl);
                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {

        if (updateUrl.equalsIgnoreCase(AppGooglePlayStoreUrl)) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
            downloadAndInstall = new InstallAPK();
            downloadAndInstall.setContext(SplashActivity.this);
            downloadAndInstall.execute(updateUrl);
        }
    }

    public void nextActivity() {

        if (sessionManager.isLoggedIn()) {
            userGroupId = nullCheckFunction(sessionManager.getValueSesion(Key_UserGroupID));

            if (userGroupId.equals("2")) {
                goNextClass(mContext, PinCodeActivity.class);
            } else if (userGroupId.equals("5")) {
                goNextClass(mContext, DeliveryBoyMainActivity.class);
            } else {
                List<String> userGroupIDList = Arrays.asList(sessionManager.getValueSesion(SessionManager.Key_all_user_group_id).split(","));
                String allUserGroupId = sessionManager.getValueSesion(SessionManager.Key_all_user_group_id);

                System.out.println("userGroupIDList===>>" + userGroupIDList.size());
                if (userGroupIDList.size() == 1) {
                    if (userGroupIDList.get(0).equals("3")) {
                        System.out.println("ID's====>>" + userGroupIDList.get(0));
                        goNextClass(mContext, CustomerDeshBoardActivity.class);
                    } else {
                        goNextClass(mContext, CustomerBuyerDairyListActivity.class);
                    }
                } else if (userGroupIDList.size() >= 2 && allUserGroupId.contains("3") && allUserGroupId.contains("4")) {

                    System.out.println("ID's==>>" + userGroupIDList.get(0) + "::" + userGroupIDList.get(1));
                    goNextClass(mContext, CustomerUserGroupActivity.class);
                } else if (userGroupIDList.size() == 2 && allUserGroupId.contains("3")) {
                    System.out.println("ID's==>>" + userGroupIDList.get(0) + "::" + userGroupIDList.get(1));
                    goNextClass(mContext, CustomerDeshBoardActivity.class);
                } else if (userGroupIDList.size() == 2 && allUserGroupId.contains("4")) {
                    System.out.println("ID's==>>" + userGroupIDList.get(0) + "::" + userGroupIDList.get(1));
                    goNextClass(mContext, CustomerBuyerMainActivity.class);
                }
            }
        } else {
            goNextClass(mContext, LoginViaActivity.class);
        }
    }

    public void goNextClass(Context context, Class className) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(context, className);
                context.startActivity(intent);
                finish();
            }
        }, waitTime);

    }


    private void showUpdateDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(mContext.getString(R.string.newUpdateAvailable))
                .setMessage(mContext.getString(R.string.pleaseUpdateApp))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.UPDATE),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                startActivity(new Intent("android.intent.action.VIEW", Uri.parse
                                        (AppGooglePlayStoreUrl)));
                            }
                        }).create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
    }


    @SuppressLint("StaticFieldLeak")
    class checkPlayStoreVersion extends AsyncTask<String, String, JSONObject> {

        ProgressDialog progressDialog;
        boolean isupdateavailable = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("checking updates...");
            progressDialog.setCancelable(false);
            // progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String newVersion = null;
                String packagename = getPackageName();

                PackageInfo packageInfo = getPackageManager().getPackageInfo(packagename,0);

                newVersion = packageInfo.versionName;


//                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName()).get();
//                latestVersion = Float.parseFloat(doc.getElementsByClass("htlgb").get(6).text());
                latestVersion = Float.parseFloat(newVersion);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressDialog.dismiss();
            System.out.println("currentVersion==" + currentVersion);
            System.out.println("latestVersion==" + latestVersion);

           // if (currentVersion < latestVersion) {
            if (isupdateavailable) {
                FirstTime = "Yes";
                if (!isFinishing()) {
                    showUpdateDialog();
                }
            } else {

            }
            super.onPostExecute(jsonObject);
        }






    }




    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if  (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                startUpdateFlow(appUpdateInfo);
            }else{
                nextActivity();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }
}
