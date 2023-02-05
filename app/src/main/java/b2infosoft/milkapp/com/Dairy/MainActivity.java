package b2infosoft.milkapp.com.Dairy;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.FatSnf.SnfFatChartFragment;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleEntryDateTimeFragment;
import b2infosoft.milkapp.com.Navigation.FragmentDrawer;
import b2infosoft.milkapp.com.Notification.NotificationFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.SplashActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static android.os.Build.VERSION.SDK_INT;

import static b2infosoft.milkapp.com.Dairy.Setting.CLRSettingFragment.geCLRSetting;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.Model.BeanOfferBanerList.getBannerOfferList;
import static b2infosoft.milkapp.com.Model.CustomerListPojo.addCustomerListInDatabase;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_DESCRIPTION;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_NAME;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere2;
import static b2infosoft.milkapp.com.appglobal.Constant.LangLoaded;
import static b2infosoft.milkapp.com.appglobal.Constant.buyMilkRateSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyBuyMilkRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_AutoFatStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuffMinFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_CowMaxFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.user_token;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_AndroidR;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkPermissionAndroidR;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hideKeyboard;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    public static DrawerLayout drawer;
    public static boolean isDashboard = false;
    Context mContext;
    SessionManager sessionManager;
    FragmentDrawer drawerFragment;
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout LLHeader;
    Fragment fragment;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        if (SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        FromWhere2 = "";
        mContext = MainActivity.this;
        sessionManager = new SessionManager(mContext);


        notificationChannelPermission();
        if (sessionManager.getValueSesion(user_token).length() == 0) {
            sessionManager.logoutUser();
            Intent i = new Intent(mContext, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if (LangLoaded.equals("")) {
            if (sessionManager.getValueSesion(SessionManager.SessionLang).length() != 0) {
                String lang = sessionManager.getValueSesion(SessionManager.SessionLang);
                setLocale(lang);
            }
        }
        checkPermissionAndroidR(mContext);
        init();
    }

    private void notificationChannelPermission() {
        if (SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(FCMCHANNEL_ID, FCMCHANNEL_NAME, importance);
            mChannel.setDescription(FCMCHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void init() {
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        drawer = findViewById(R.id.drawer_layout);
        LLHeader = findViewById(R.id.LLHeader);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                firstTimeLoadSetting();
                getBannerOfferList(mContext, false);
                addCustomerListInDatabase(mContext, false);
                getSMSBalance(mContext, NetworkTask.GET_TASK);
                // Check User Plan Expiry Status
                PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus(mContext);

            }
        });

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawer, null);
        drawerFragment.setDrawerListener(this);
        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            fragment = new DairyDeshboardFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.dairy_container, fragment).commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("from", "dairyMain");
            fragment = new NotificationFragment();
            fragment.setArguments(bundle);
            goNextFragmentReplace(mContext, fragment);

        }

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerFragment.refreshItems(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerFragment.refreshItems(false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public void firstTimeLoadSetting() {
        if (FirstTime.equals("Yes")) {

            SnfFatChartFragment.getBonusPrice(mContext);
            //get Sale Fat Milk Price
            PurchaseMilkDateTimeFragment.getBuyFatMilkPrice(mContext);

            SaleEntryDateTimeFragment.getSaleFatMilkPrice(mContext);
            // set Message Setting
            //get Sell MilkPrice
            SaleEntryDateTimeFragment.getSellMilkFixPrice(mContext);
            geCLRSetting(mContext);
            getBuyMilkRateTypeSetting(mContext);


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void onDrawerItemSelected(View view, int position) {

    }


    public void openDrawer() {
        drawer.openDrawer(Gravity.RIGHT);
    }

    public void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }

    public void logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Logout))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), null)
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void setLocale(String lang) {
        LangLoaded = "Loaded";
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    public void onBackPressed() {

        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

            if (backStackEntryCount > 0) {
                isDashboard = false;
                getFragmentManager().popBackStackImmediate();

                if (backStackEntryCount == 1) {
                    super.onBackPressed();

                } else {
                    super.onBackPressed();
                }

            } else {

                if (!isDashboard) {
                    isDashboard = true;
                    fragment = new DairyDeshboardFragment();
                    goNextFragmentReplace(mContext, fragment);
                }
                if (doubleBackToExitPressedOnce) {
                    //super.onBackPressed();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(R.string.Exyt_App)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MainActivity.this.finish();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == PERMISSION_AndroidR) {
            /*if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                   showToast(mContext, "Allow permission for storage access!" );
                }
            }*/
        }
    }

    public static void getBuyMilkRateTypeSetting(Context mContext) {
        SessionManager session = new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        session.setValueSession(KeyBuyMilkRateType, jsonObject.getString("entry_type"));
                        session.setValueSession(Key_BuyMilkScreen, jsonObject.getString("screen_type"));
                        session.setValueSession(Key_AutoFatStatus, jsonObject.getString("auto_fat_status"));
                        session.setFloatValueSession(Key_CowMaxFat, getFloatValuFromInputText(jsonObject.getString("cow")));
                        session.setFloatValueSession(Key_BuffMinFat, getFloatValuFromInputText(jsonObject.getString("buffalo")));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(buyMilkRateSetting + "?dairy_id=" + session.getValueSesion(SessionManager.KEY_UserID));

    }
}

