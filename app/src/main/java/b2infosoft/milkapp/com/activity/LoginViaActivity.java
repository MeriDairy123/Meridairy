package b2infosoft.milkapp.com.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.Result;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.LangLoaded;
import static b2infosoft.milkapp.com.appglobal.Constant.LoadToLang;
import static b2infosoft.milkapp.com.appglobal.Constant.UserID;
import static b2infosoft.milkapp.com.appglobal.Constant.loginByQRCodeAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.SessionLang;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkPermissionAndroidR;
import static b2infosoft.milkapp.com.useful.UtilityMethod.enableSMSReciver;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class LoginViaActivity extends AppCompatActivity implements View.OnClickListener {

    private static SessionManager sessionManager;
    private static String user_group_id = "", user_id = "", user_tokens = "";
    LinearLayout ll_signup, ll_QrSacn, ll_DairyLogin;
    TextView tv_lang, tvlegalPolicies;
    String Language = "";
    Intent intent;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_via);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        user_group_id = "";
        user_id = "";
        user_tokens = "";

        mContext = LoginViaActivity.this;
        enableSMSReciver(mContext);
        sessionManager = new SessionManager(this);
        tv_lang = findViewById(R.id.tv_lang);
        ll_signup = findViewById(R.id.ll_signup);
        ll_QrSacn = findViewById(R.id.ll_QrSacn);
        ll_DairyLogin = findViewById(R.id.ll_DairyLogin);
        tvlegalPolicies = findViewById(R.id.tvlegalPolicies);

        if (Constant.LangLoaded.equals("")) {
            if (sessionManager != null) {
                if (sessionManager.getValueSesion(SessionLang).length() != 0) {
                    String lang = sessionManager.getValueSesion(SessionLang);
                    setLocale(lang);
                } else {
                    setLocale("en");
                }
            }
        } else {
            if (sessionManager.getValueSesion(SessionLang).length() != 0) {
                String lang = sessionManager.getValueSesion(SessionLang);
                setTextLang(lang);
            }
        }
        tv_lang.setOnClickListener(this);
        ll_QrSacn.setOnClickListener(this);
        ll_DairyLogin.setOnClickListener(this);
        tvlegalPolicies.setOnClickListener(this);


        ll_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivitySelectUserType.class);
                startActivity(intent);
            }
        });

        //  DialogWebViewDairyVideo();
        setAnimationEffect();
        checkPermissionAndroidR(mContext);
    }

    private void setAnimationEffect() {
        //Animation ll_signup
        ll_signup.setTranslationX(-(10 * 100));
        ll_signup.setAlpha(0.5f);
        ll_signup.animate().alpha(1f).translationX(0).setDuration(1500).start();
        //Animation ll_CustomerLogin
        ll_DairyLogin.setTranslationX(10 * 100);
        ll_DairyLogin.setAlpha(0.5f);
        ll_DairyLogin.animate().alpha(1f).translationX(0).setDuration(1500).start();
        //Animation ll_DairyLogin
        ll_QrSacn.setTranslationX(-(10 * 100));
        ll_QrSacn.setAlpha(0.5f);
        ll_QrSacn.animate().alpha(1f).translationX(0).setDuration(1500).start();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_lang:
                dialogLang();
                break;
            case R.id.ll_QrSacn:
                Constant.FromWhere = "loginvia";
                /*  intent = new Intent(LoginViaActivity.this, QrScannerActivity.class);
                intent.putExtra("from", "loginvia");
                startActivity(intent);*/
                break;
            case R.id.ll_DairyLogin:
                tempMobileNumber = "";
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            case R.id.tvlegalPolicies:
                tempMobileNumber = "";
                intent = new Intent(LoginViaActivity.this, TermsConditionActivity.class);
                intent.putExtra("FromWhere", "loginvia");
                startActivity(intent);
                break;
        }

    }

    public void onScanResult(Result result, Context mContext) {
        SessionManager sessionMange = new SessionManager(mContext);
        System.out.println("result=loginvia==" + result.getText());
        try {
            //converting the data to json
            String scanOutput = result.getText();
            JSONObject obj = new JSONObject(scanOutput);
            user_group_id = obj.getString("user_group_id");
            user_id = obj.getString("user_id");
            user_tokens = obj.getString("user_token");

            sessionMange.setValueSession(SessionManager.user_token, user_tokens);
            if (isConnected()) {
                LoginUsingQrCode(mContext, user_group_id, user_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void dialogLang() {
        final CharSequence[] option2 = new CharSequence[]{"  English", "  española", "  हिंदी", "  ગુજરાતી", "  मराठी", "  ਪੰਜਾਬੀ", "  తెలుగు", "  தமிழ்", "  ಕನ್ನಡ"};
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle(R.string.change_language);
        builder1.setItems(option2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (option2[item].equals("  English")) {
                    LoadToLang = "English";
                    sessionManager.setValueSession(SessionManager.SessionLang, "en");
                    setLocale("en");
                } else if (option2[item].equals("  española")) {
                    LoadToLang = "española";
                    sessionManager.setValueSession(SessionManager.SessionLang, "es");
                    setLocale("es");
                } else if (option2[item].equals("  हिंदी")) {
                    LoadToLang = "Hindi";
                    sessionManager.setValueSession(SessionManager.SessionLang, "hi");
                    setLocale("hi");
                } else if (option2[item].equals("  ગુજરાતી")) {
                    LoadToLang = "Gujrati";
                    sessionManager.setValueSession(SessionManager.SessionLang, "gu");
                    setLocale("gu");
                } else if (option2[item].equals("  मराठी")) {
                    LoadToLang = "Marathi";
                    sessionManager.setValueSession(SessionManager.SessionLang, "mr");
                    setLocale("mr");
                } else if (option2[item].equals("  ਪੰਜਾਬੀ")) {
                    LoadToLang = "Punjabi";
                    sessionManager.setValueSession(SessionManager.SessionLang, "pa");
                    setLocale("pa");
                } else if (option2[item].equals("  తెలుగు")) {
                    LoadToLang = "Telugu";
                    sessionManager.setValueSession(SessionManager.SessionLang, "te");
                    setLocale("te");
                } else if (option2[item].equals("  தமிழ்")) {
                    LoadToLang = "Tamil";
                    sessionManager.setValueSession(SessionManager.SessionLang, "ta");
                    setLocale("ta");
                } else if (option2[item].equals("  ಕನ್ನಡ")) {
                    LoadToLang = "Kannad";
                    sessionManager.setValueSession(SessionManager.SessionLang, "ta");
                    setLocale("kn");
                }
            }
        });
        builder1.show();
    }

    public void setLocale(String lang) {

        setTextLang(lang);
        sessionManager.setValueSession(SessionLang, lang);

        LangLoaded = "Loaded";
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, LoginViaActivity.class);
        startActivity(refresh);
        finish();
        setAnimationEffect();
    }

    private void setTextLang(String lang) {
        if (lang.equalsIgnoreCase("en")) {
            Language = mContext.getString(R.string.english);

        } else if (lang.equalsIgnoreCase("hi")) {
            Language = mContext.getString(R.string.hindi);
        } else if (lang.equalsIgnoreCase("gu")) {
            Language = mContext.getString(R.string.gujrati);
        } else if (lang.equalsIgnoreCase("mr")) {
            Language = mContext.getString(R.string.marathi);
        } else if (lang.equalsIgnoreCase("pa")) {
            Language = mContext.getString(R.string.punjabi);
        } else if (lang.equalsIgnoreCase("te")) {
            Language = mContext.getString(R.string.telugu);
        } else if (lang.equalsIgnoreCase("ta")) {
            Language = mContext.getString(R.string.tamil);
        } else if (lang.equalsIgnoreCase("ka")) {
            Language = mContext.getString(R.string.kannad);
        }
        tv_lang.setText(mContext.getString(R.string.lang) + " " + Language);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(getString(R.string.Exyt_App))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void LoginUsingQrCode(Context context, String userGroupId, String userId) {

        SessionManager sessionManager = new SessionManager(context);
        user_tokens = sessionManager.getValueSesion(SessionManager.user_token);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, context,
                context.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {
                //fail,success
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("login_status");
                    if (success.equals("success")) {
                        String user_group_id = jsonObject.getString("user_group_id");
                        if (user_group_id.equals("2")) {
                            Constant.FirstTime = "Yes";
                            UserID = jsonObject.getString("id");
                            sessionManager.createLoginDairyJson(jsonObject, "yes");
                            sessionManager.checkDairyLoginAccount(jsonObject);
                        }

                    } else {
                        showToast(context, success);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_id", userId)
                .addEncoded("user_group_id", userGroupId).build();
        caller.addRequestBody(body);
        caller.execute(loginByQRCodeAPI);

    }

}
