package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import b2infosoft.milkapp.com.Model.AdvBannerPojo;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.Notification.SellerNotificationActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.activity.SplashActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomPagerAdapter;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerDashboard_Menu_Adapter;
import b2infosoft.milkapp.com.customer_app.customer_adapters.DairyNameWithEntryAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.ForAdvt;
import static b2infosoft.milkapp.com.appglobal.Constant.LangLoaded;
import static b2infosoft.milkapp.com.appglobal.Constant.LoadToLang;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_ENGLISH;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_ENGLISH_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_GUJRATI;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_GUJRATI_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_HINDI;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_HINDI_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_KANNAD;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_KANNAD_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_MARATHI;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_MARATHI_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_PUNJABI;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_PUNJABI_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_SPANISH_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_TAMIL;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_TAMIL_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_TELGU;
import static b2infosoft.milkapp.com.appglobal.Constant.MENU_TELGU_ITEM;
import static b2infosoft.milkapp.com.appglobal.Constant.adsBannerAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getDairyAdsAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.user_token;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCalanderDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.md5;

public class CustomerDeshBoardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Context mContext;
    Toolbar toolbar;
    ViewPager viewPager;
    ImageView imgDot, imgLogout;
    Timer timer;
    SessionManager sessionManager;
    String type = "";
    ArrayList<AdvBannerPojo> bannerList = new ArrayList<>();
    LinearLayout layoutSeller;
    LinearLayout layout;
    RelativeLayout layout_2;
    ScrollView scrollView;
    String from_date="",to_date="";
    RecyclerView recyclerDairyList;
    DairyNameWithEntryAdapter dairyNameWithEntryAdapter;
    List<Dashboard_item> dashboard_items;
    RecyclerView recyclerviewMenu;
    String userGroupId = "3";
    CustomerDashboard_Menu_Adapter dashboard_item_adapter;
    View layoutNotification;
    ImageView imgNotification;
    Button btnsubmit;
    TextView tvNotificationCount,tvDatefrom,tvDateto;
    int notif_count = 0;
    BroadcastReceiver receiver;
    Spinner sp_DairyList;
    public ArrayList<DairyNamePojo> mList = new ArrayList<DairyNamePojo>();
    int dairyposition;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

  //  private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_deshboard_layout);
        mContext = CustomerDeshBoardActivity.this;
        Constant.SessionUserGroupID = "3";
        dashboard_items = new ArrayList<>();

        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        sessionManager = new SessionManager(mContext);

        sessionManager.setValueSession(Key_UserGroupID, userGroupId);
        if (sessionManager.getValueSesion(user_token).length() == 0) {
            sessionManager.logoutUser();
            Intent i = new Intent(mContext, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    //    mAdView = findViewById(R.id.adView);
        if (ForAdvt.equals("Yes")) {

        } else {
        //    mAdView.setVisibility(View.GONE);
        }

        if (LangLoaded.equals("")) {
            if (sessionManager != null) {
                if (sessionManager.getValueSesion(SessionManager.SessionLang).length() != 0) {
                    String lang = sessionManager.getValueSesion(SessionManager.SessionLang);
                    setLocale(lang);
                }
            }
        }
        timer = new Timer();
        toolbar = findViewById(R.id.toolbar);
        sp_DairyList = findViewById(R.id.sp_DairyList);
        tvDatefrom = findViewById(R.id.tvStartDate);
        tvDateto = findViewById(R.id.tvEndDate);
        btnsubmit = findViewById(R.id.btnSubmit);

        layoutNotification = toolbar.findViewById(R.id.layoutNotification);
        imgNotification = toolbar.findViewById(R.id.imgNotification);
        tvNotificationCount = toolbar.findViewById(R.id.tvNotificationCount);
        imgDot = toolbar.findViewById(R.id.imgDot);
        imgLogout = toolbar.findViewById(R.id.imgLogout);
        toolbar.setTitle(sessionManager.getValueSesion(SessionManager.KEY_Name).toUpperCase());
        recyclerviewMenu = findViewById(R.id.recyclerview);

        layoutSeller = findViewById(R.id.layoutSeller);
        layoutNotification.setVisibility(View.VISIBLE);
        imgLogout.setBackground(getResources().getDrawable(R.drawable.ic_logout_customer));
        imgDot.setVisibility(View.VISIBLE);
        imgLogout.setVisibility(View.VISIBLE);


        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView);
        //recyclerDairyList = findViewById(R.id.recyclerDairyList);
        recyclerDairyList = findViewById(R.id.recyclerDairyList1);


        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveNotification();
            }
        });
        dashboardRecyclerViewUI();
        getCustomerAdsBanners();
        getBanners();

        layoutSeller.setVisibility(View.VISIBLE);
        recyclerDairyList.setVisibility(View.VISIBLE);
        imgDot.setOnClickListener(this);
        imgLogout.setOnClickListener(this);
        tvDatefrom.setOnClickListener(this);
        tvDateto.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);
        sp_DairyList.setOnItemSelectedListener(this);
       // sp_DairyList.setOnClickListener(this);
    }

    public void moveNotification() {
        Intent i = new Intent(mContext, SellerNotificationActivity.class);
        startActivity(i);
    }

    private void notificationCountUpdate() {
        if (sessionManager.getIntValueSesion(KEY_Notification).toString().length() == 0 ||
                sessionManager.getIntValueSesion(KEY_Notification) == 0) {
            notif_count = 0;
        } else {
            notif_count = sessionManager.getIntValueSesion(KEY_Notification);
        }
        if (notif_count > 0) {
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(Integer.toString(notif_count));
        } else {
            tvNotificationCount.setVisibility(View.GONE);

        }
    }

    private void getTenDaysData() {
        DairyNamePojo.getDairyNameList(mContext, sessionManager.getValueSesion(KEY_UserID), "CustomerDeshboard");
    }

    private void adMob() {
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        Log.i("device id=", deviceId);


     /*   MobileAds.initialize(this, getString(R.string.customer_app_id));

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //  Toast.makeText(getApplicationContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
                // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Toast.makeText(getApplicationContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
            }
        });

        mAdView.loadAd(adRequest);*/
    }

    @Override
    public void onPause() {
//        if (mAdView != null) {
//            mAdView.pause();
//        }
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }


    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT)
        );
        notificationCountUpdate();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
    }


    private void getBanners() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please Wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bannerList.add(new AdvBannerPojo(
                                    jsonObject.getString("id")
                                    , jsonObject.getString("date_from"), jsonObject.getString("date_to"), jsonObject.getString("title")
                                    , jsonObject.getString("discription"), jsonObject.getString("location")
                                    , jsonObject.getString("latitude"), jsonObject.getString("longitude")
                                    , jsonObject.getString("adlogo")
                            ));
                        }
                    }
                    swipeLayout(bannerList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        serviceCaller.execute(adsBannerAPI);
    }

    private void getCustomerAdsBanners() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        String imgPath = jsonObj.getString("path");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bannerList.add(new AdvBannerPojo(
                                    jsonObject.getString("id"), jsonObject.getString("start_date")
                                    , jsonObject.getString("start_date"), jsonObject.getString("title")
                                    , jsonObject.getString("description"), jsonObject.getString("cities")
                                    , "", "", imgPath + jsonObject.getString("image")
                            ));
                        }
                    }
                    swipeLayout(bannerList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(getDairyAdsAPI + "userid=" + sessionManager.getValueSesion(KEY_UserID));
    }


    private void swipeLayout(final ArrayList<AdvBannerPojo> bannerList) {
        getTenDaysData();


        timer = new Timer();
        viewPager = findViewById(R.id.pager);
        CustomPagerAdapter adapter = new CustomPagerAdapter(CustomerDeshBoardActivity.this, bannerList, mContext);
        viewPager.setAdapter(adapter);
        if (bannerList.size() > 0) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % bannerList.size());
                        }
                    });
                }
            };

            timer.schedule(timerTask, 3000, 5000);
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();

        } else {
        }
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        List<String> userGroupIDList = Arrays.asList(sessionManager.getValueSesion(SessionManager.Key_all_user_group_id).split(","));
        String allUserGroupId = sessionManager.getValueSesion(SessionManager.Key_all_user_group_id);
        if (allUserGroupId.contains("3") && allUserGroupId.contains("4")) {
            UtilityMethod.goNextClass(mContext, CustomerUserGroupActivity.class);
        } else {
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
                            timer.cancel();
                            dialog.dismiss();
                            finish();
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
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgDot:
                changLanguage(view);
                break;
            case R.id.tvStartDate:
                getDate(mContext, tvDatefrom);
                break;
            case R.id.tvEndDate:
                if (tvDatefrom.getText().toString().length()<1){
                    UtilityMethod.showToast(mContext,"Select Start Date First");
                }else{
                    getDate(mContext, tvDateto);
                }
                break;

            case R.id.btnSubmit:
                try {
                    if (!checktenday()){
                        UtilityMethod.showToast(mContext,"Select Date between ten days Period");
                        tvDateto.setText("");
                    }else{
                        setdropdown();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.imgLogout:
                tempMobileNumber = sessionManager.getValueSesion(KEY_Mobile);
                sessionManager.logoutUser();
                UtilityMethod.goNextClass(mContext, LoginWithPasswordActivity.class);
                break;
        }
    }

    private boolean checktenday() throws ParseException {
        String frselected = tvDatefrom.getText().toString();
        final Calendar call = Calendar.getInstance();
        call.setTime(dateFormat.parse(frselected));
        call.add(Calendar.DATE, 12);
        call.set(call.get(Calendar.YEAR),call.get(Calendar.MONTH),call.get(Calendar.DAY_OF_MONTH));
        String aftertenday = dateFormat.format(call.getTime());
        String enddate = tvDateto.getText().toString();

        if (dateFormat.parse(enddate).before(dateFormat.parse(aftertenday))){
           return true;
        }
        return false;
    }


    private void changLanguage(View view) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, imgDot);
        popup.getMenu().add(MENU_ENGLISH, MENU_ENGLISH_ITEM, 0, mContext.getString(R.string.english));
        popup.getMenu().add(MENU_ENGLISH, MENU_SPANISH_ITEM, 1, mContext.getString(R.string.spanish));
        popup.getMenu().add(MENU_HINDI, MENU_HINDI_ITEM, 2, mContext.getString(R.string.hindi));
        popup.getMenu().add(MENU_GUJRATI, MENU_GUJRATI_ITEM, 3, mContext.getString(R.string.gujrati));
        popup.getMenu().add(MENU_MARATHI, MENU_MARATHI_ITEM, 4, mContext.getString(R.string.marathi));
        popup.getMenu().add(MENU_PUNJABI, MENU_PUNJABI_ITEM, 5, mContext.getString(R.string.punjabi));
        popup.getMenu().add(MENU_TELGU, MENU_TELGU_ITEM, 6, mContext.getString(R.string.telugu));
        popup.getMenu().add(MENU_TAMIL, MENU_TAMIL_ITEM, 7, mContext.getString(R.string.tamil));
        popup.getMenu().add(MENU_KANNAD, MENU_KANNAD_ITEM, 8, mContext.getString(R.string.kannad));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_ENGLISH_ITEM:
                        LoadToLang = "English";
                        sessionManager.setValueSession(SessionManager.SessionLang, "en");
                        setLocale("en");
                        break;
                    case MENU_SPANISH_ITEM:
                        LoadToLang = "Spanish";
                        sessionManager.setValueSession(SessionManager.SessionLang, "es");
                        setLocale("es");
                        break;
                    case MENU_HINDI_ITEM:
                        LoadToLang = "Hindi";
                        sessionManager.setValueSession(SessionManager.SessionLang, "hi");
                        setLocale("hi");
                        break;

                    case MENU_GUJRATI_ITEM:
                        LoadToLang = "Gujrati";
                        sessionManager.setValueSession(SessionManager.SessionLang, "gu");
                        setLocale("gu");
                        break;

                    case MENU_MARATHI_ITEM:
                        LoadToLang = "Marathi";
                        sessionManager.setValueSession(SessionManager.SessionLang, "mr");
                        setLocale("mr");
                        break;

                    case MENU_PUNJABI_ITEM:
                        LoadToLang = "Punjabi";
                        sessionManager.setValueSession(SessionManager.SessionLang, "pa");
                        setLocale("pa");
                        break;

                    case MENU_TELGU_ITEM:
                        LoadToLang = "Telugu";
                        sessionManager.setValueSession(SessionManager.SessionLang, "te");
                        setLocale("te");
                        break;
                    case MENU_TAMIL_ITEM:
                        LoadToLang = "Tamil";
                        sessionManager.setValueSession(SessionManager.SessionLang, "ta");
                        setLocale("ta");
                        break;
                    case MENU_KANNAD_ITEM:
                        LoadToLang = "Kannad";
                        sessionManager.setValueSession(SessionManager.SessionLang, "kn");
                        setLocale("kn");
                        break;
                }
                return false;
            }
        });

        popup.show();
    }


    public void setLocale(String lang) {
        LangLoaded = "Loaded";
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

    public void setDairyNameList(ArrayList<DairyNamePojo> dairyNamePojos) {

           this.mList = dairyNamePojos;


        List<String> list = new ArrayList<String>(loadbtpname(dairyNamePojos));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, list);
        sp_DairyList.setAdapter(adapter);

         sp_DairyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String selectedname  = dairyNamePojos.get(position).dairy_name;
                 dairyposition = position;

                 String fromdate="",todate="";
                 String month="",day="";
                 @SuppressLint("SimpleDateFormat")
                 SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");


                 final Calendar c2 = Calendar.getInstance();
                 int mYear = c2.get(Calendar.YEAR);
                 int mMonth = c2.get(Calendar.MONTH);
                 int mDay = c2.get(Calendar.DAY_OF_MONTH);
                 int maxDay = c2.getActualMaximum(Calendar.DAY_OF_MONTH);


                 if (mDay >= 1 && mDay <= 10) {
                     Log.d("Days Data", "getTenDaysData: " + "b/w 1-10");
                     final Calendar cal = Calendar.getInstance();

                     int fr = 10-mDay;
                     fr=10-fr;
                     if (fr==0){
                         cal.add(Calendar.DATE, -9);
                     }else{
                         cal.add(Calendar.DATE, -(fr-1));
                     }

                     cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                     fromdate =  s.format(cal.getTime());




                     final Calendar call = Calendar.getInstance();
                     int tr = 10-mDay;
                     if (tr!=0){
                         call.add(Calendar.DATE, tr);
                     }
                     call.set(call.get(Calendar.YEAR),call.get(Calendar.MONTH),call.get(Calendar.DAY_OF_MONTH));
                     todate = s.format(call.getTime());


                 } else if (mDay >= 11 && mDay <= 20) {
                     Log.d("Days Data", "getTenDaysData: " + "b/w 11-20");


                     final Calendar cal = Calendar.getInstance();
                     int fr = 20-mDay;
                         fr = 10-fr;
                         if (fr == 0) {
                             cal.add(Calendar.DATE, -9);
                         } else {
                             cal.add(Calendar.DATE, -(fr - 1));
                         }


                     cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                     fromdate = s.format(cal.getTime());

                     final Calendar call = Calendar.getInstance();
                     int tr = 20-mDay;
                     if (tr!=0){
                         call.add(Calendar.DATE, tr);
                     }
                     call.set(call.get(Calendar.YEAR),call.get(Calendar.MONTH),call.get(Calendar.DAY_OF_MONTH));
                     todate = s.format(call.getTime());

                 } else if (mDay >= 21 && mDay <= maxDay) {
                     Log.d("Days Data", "getTenDaysData: " + "b/w 21-" + maxDay);

                     final Calendar cal = Calendar.getInstance();
                     int fr = maxDay-mDay;
                     fr = 10-fr;
                     if (fr==0){
                         cal.add(Calendar.DATE, -9);
                     }else{
                         if (maxDay==31){
                             cal.add(Calendar.DATE, -(fr));
                         }else {
                             cal.add(Calendar.DATE, -(fr - 1));
                         }
                     }
                     cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                     fromdate = s.format(cal.getTime());

                     final Calendar call = Calendar.getInstance();
                     int tr = maxDay-mDay;
                     if (tr!=0){
                         call.add(Calendar.DATE, tr);
                     }
                     call.set(call.get(Calendar.YEAR),call.get(Calendar.MONTH),call.get(Calendar.DAY_OF_MONTH));
                     todate = s.format(call.getTime());

                 }

                 tvDatefrom.setText("");
                 tvDateto.setText("");

                 LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                 recyclerDairyList.setLayoutManager(linearLayoutManager);
                 recyclerDairyList.setNestedScrollingEnabled(false);
                 linearLayoutManager.setSmoothScrollbarEnabled(true);
                 dairyNameWithEntryAdapter = new DairyNameWithEntryAdapter(mContext, dairyNamePojos.get(position).customer_id,dairyNamePojos.get(position).id, fromdate,todate, recyclerDairyList);
                 recyclerDairyList.setAdapter(dairyNameWithEntryAdapter);
                 dairyNameWithEntryAdapter.notifyDataSetChanged();
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }

         });


    }

    private void setdropdown() throws ParseException {
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");

        if (tvDatefrom!=null && tvDateto !=null && tvDatefrom.getText().toString().length()>0 && tvDateto.getText().toString().length()>0)
        {
            from_date = tvDatefrom.getText().toString();
            to_date = tvDateto.getText().toString();
            if (s.parse(from_date).before(s.parse(to_date))){
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                recyclerDairyList.setLayoutManager(linearLayoutManager);
                recyclerDairyList.setNestedScrollingEnabled(false);
                linearLayoutManager.setSmoothScrollbarEnabled(true);
                dairyNameWithEntryAdapter = new DairyNameWithEntryAdapter(mContext, mList.get(dairyposition).customer_id,mList.get(dairyposition).id, from_date,to_date, recyclerDairyList);
                recyclerDairyList.setAdapter(dairyNameWithEntryAdapter);
                dairyNameWithEntryAdapter.notifyDataSetChanged();
            }else{
                UtilityMethod.showToast(mContext,"Select Date between ten days Period");

            }
        }else{
            UtilityMethod.showToast(mContext,"Select Date Properly");
        }
    }
    private long getDaysDifference(Date fromDate,Date toDate) {

        if(fromDate == null || toDate == null)
            return 0;

        int a = Integer.parseInt(DateFormat.format("dd",   fromDate)+"");
        int b = Integer.parseInt(DateFormat.format("dd",   toDate)+"");

        if ( b <= a){
           // return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) + b - a;
            return 0;
        }
        return b - a;
    }




    List<String> loadbtpname(ArrayList<DairyNamePojo> dairyNamePojos) {

        List<String> list = new ArrayList<>();
        if (dairyNamePojos.size() > 0) {
            for (int i = 0 ;  i< dairyNamePojos.size() ; i++) {
                list.add(dairyNamePojos.get(i).dairy_name);
            }
        }
        return list;
    }




    private void dashboardRecyclerViewUI() {
        if (userGroupId.equalsIgnoreCase("3")) {
            dashboard_items = getSallerMenu();
        } else {
            dashboard_items = getBuyerMenu();
        }
        int spaceCount = dashboard_items.size();

        dashboard_item_adapter = new CustomerDashboard_Menu_Adapter(mContext, dashboard_items);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, spaceCount);
        recyclerviewMenu.setLayoutManager(mLayoutManager);
        recyclerviewMenu.addItemDecoration(new GridSpacingItemDecoration(spaceCount, dpToPx(0), true));
        recyclerviewMenu.setItemAnimator(new DefaultItemAnimator());
        recyclerviewMenu.setAdapter(dashboard_item_adapter);

    }


    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public List<Dashboard_item> getBuyerMenu() {
        List<Dashboard_item> buyerMenu = new ArrayList<>();

        buyerMenu.add(new Dashboard_item("1", mContext.getResources().getString(R.string.Profile), "",
                R.drawable.profile_icon, "#FF5B24"));
       /* buyerMenu.add(new Dashboard_item("2", mContext.getResources().getString(R.string.Transaction),
                R.drawable.tranasctions_icon, "#00BADB"));
        buyerMenu.add(new Dashboard_item("3", mContext.getResources().getString(R.string.Product),
                R.drawable.products, "#FF8400"));
                 buyerMenu.add(new Dashboard_item("4", mContext.getResources().getString(R.string.Chat),
                R.drawable.send_message, "#2CBF7B"));
                */


        return buyerMenu;
    }

    public List<Dashboard_item> getSallerMenu() {
        List<Dashboard_item> buyerMenu = new ArrayList<>();

        buyerMenu.add(new Dashboard_item("1", mContext.getResources().getString(R.string.Profile), "",
                R.drawable.profile_icon, "#FF5B24"));
        buyerMenu.add(new Dashboard_item("2", mContext.getResources().getString(R.string.Transaction), "",
                R.drawable.tranasctions_icon, "#00BADB"));
        buyerMenu.add(new Dashboard_item("3", mContext.getResources().getString(R.string.entry), "",
                R.drawable.view_entry, "#35CE8E"));

        buyerMenu.add(new Dashboard_item("4", mContext.getResources().getString(R.string.Product), "",
                R.drawable.products, "#FF8400"));
        buyerMenu.add(new Dashboard_item("5", mContext.getResources().getString(R.string.AddAnimal), "",
                R.drawable.ic_cow, "#2CBF7B"));
        return buyerMenu;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
