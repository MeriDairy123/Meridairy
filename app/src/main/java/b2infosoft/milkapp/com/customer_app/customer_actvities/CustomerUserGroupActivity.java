package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerDairyListActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.user_group_id;

public class CustomerUserGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSeller, btnBuyer;
    SessionManager sessionManager;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_user_group);

        mContext = CustomerUserGroupActivity.this;
        sessionManager = new SessionManager(mContext);
        btnSeller = findViewById(R.id.btnSeller);
        btnBuyer = findViewById(R.id.btnBuyer);
        btnSeller.setOnClickListener(this);
        btnBuyer.setOnClickListener(this);
        if (Constant.LangLoaded.equals("")) {
            if (sessionManager != null) {
                if (sessionManager.getValueSesion(SessionManager.SessionLang).length() != 0) {
                    String lang = sessionManager.getValueSesion(SessionManager.SessionLang);
                    setLocale(lang);
                }
            }
        }

    }

    public void setLocale(String lang) {
        Constant.LangLoaded = "Loaded";
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, CustomerDeshBoardActivity.class);
        startActivity(refresh);
        finish();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSeller:
                user_group_id = "3";
                sessionManager.setValueSession(SessionManager.Key_UserGroupID, user_group_id);
                startActivity(new Intent(mContext, CustomerDeshBoardActivity.class));
                break;
            case R.id.btnBuyer:
                user_group_id = "4";
                sessionManager.setValueSession(SessionManager.Key_UserGroupID, user_group_id);
                startActivity(new Intent(mContext, CustomerBuyerDairyListActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //System.exit(0);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(getString(R.string.Exyt_App))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        System.exit(0);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel) + "", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
