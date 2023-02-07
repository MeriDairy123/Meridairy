package b2infosoft.milkapp.com.Dairy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import b2infosoft.milkapp.com.BuyPlan.FragmentMembershipUpgrade;
import b2infosoft.milkapp.com.BuyPlan.FragmentMessagePlan;
import b2infosoft.milkapp.com.Dairy.Bhugtan.SallerBhugtanFragment;
import b2infosoft.milkapp.com.Dairy.BulkMilkSale.BMCDashboard;
import b2infosoft.milkapp.com.Dairy.Customer.BuyerSellerCustomerListFragment;
import b2infosoft.milkapp.com.Dairy.Customer.CustomerSallerListFragment;
import b2infosoft.milkapp.com.Dairy.FatSnf.ChartTabFragment;
import b2infosoft.milkapp.com.Dairy.FatSnf.SnfFatChartFragment;
import b2infosoft.milkapp.com.Dairy.Invoice.InvoiceTabFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentTabFragment;
import b2infosoft.milkapp.com.Dairy.PaymentVoucher.VoucherTabFragment;
import b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Fragment.PlantCollectionAndOutLetFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment.ProductDashboard;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFullScreenFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleEntryDateTimeFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleMilkEntryFullScreenFragment;
import b2infosoft.milkapp.com.Dairy.SendNotification.DairyAllCustomerlist;
import b2infosoft.milkapp.com.Dairy.Setting.SettingTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewEntryBothShiftTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewMilkEntryTabFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.BeanOfferBanerList;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.Notification.NotificationFragment;
import b2infosoft.milkapp.com.QRCodeScanner.QrScannerActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.ShoppingFragment;
import b2infosoft.milkapp.com.adapter.BannerDasboardPagerAdapter;
import b2infosoft.milkapp.com.adapter.DashboardItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.FatSnf.SnfFatChartFragment.getBonusPrice;
import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus;
import static b2infosoft.milkapp.com.Database.DatabaseHandler.getDbHelper;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.setMessageSetting;
import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.Model.BeanVehicleDairyItem.getVehicleDairyList;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.get20daysdata;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.matchcountdata;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadPlantMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadPlantSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere2;
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
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.getcount30days;
import static b2infosoft.milkapp.com.appglobal.Constant.smstendayssettingget;
import static b2infosoft.milkapp.com.appglobal.Constant.smstendayssettingsave;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AdvtStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_PlantId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_User_Status;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.appInstalledOrNot;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getShiftAccordingTime;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.GET_TASK;

import org.json.JSONException;
import org.json.JSONObject;


public class DairyDeshboardFragment extends Fragment implements View.OnClickListener,
        OnClickInDashboardAdapter, milkEntryUploadListner {
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final String TAG = "DairyDeshboardFragment";
    Context mContext;
    Toolbar toolbar;
    TextView tvDairyCenter;
    ImageView imgDot, imgWhatsApp, imgHelp;

    SessionManager sessionManager;
    String dairyid = "", plantId = "", name = "", mobileNo = "", dairyCenter = "";
    //Banner
    ViewPager viewPager;
    CirclePageIndicator indicator;
    BannerDasboardPagerAdapter bannerDasboardPagerAdapter;
    // Product
    List<Dashboard_item> dashboard_items = new ArrayList<>();
    DashboardItemAdapter dashboard_itemAdapter;
    RecyclerView recyclerView;
    View view, viewBanner, viewComputerLogin;
    Timer swipeTimer;
    View layoutNotification, viewRefresh;
    ImageView imgNotification;
    TextView tvNotificationCount, tvLastRefresh;
    int listViewCount = 3, notif_count = 0, currentPage = 0, NUM_PAGES = 0;
    BroadcastReceiver receiver;
    Fragment fragment;
    int userRemainingDay = 0;
    String userRemainingSMS = "0";
    private ArrayList<BeanOfferBanerList> banerLists = new ArrayList<BeanOfferBanerList>();
    DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dairy_dashboard, container, false);

        mContext = getActivity();

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

      //hasManageExternalStoragePermission(getActivity());

        dashboard_items = new ArrayList<>();



        listViewCount = 3;
        db = getDbHelper(mContext);
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
        userRemainingSMS = sessionManager.getValueSesion(SessionManager.Key_BalancewWebSMS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                notificationCountUpdate();
            }
        };
        firstTimeLoadSetting();
        toolbar = view.findViewById(R.id.toolbar);
        imgDot = toolbar.findViewById(R.id.imgDot);
        imgWhatsApp = toolbar.findViewById(R.id.imgWhatsApp);
        imgHelp = toolbar.findViewById(R.id.imgHelp);
        layoutNotification = toolbar.findViewById(R.id.layoutNotification);
        imgNotification = toolbar.findViewById(R.id.imgNotification);
        tvNotificationCount = toolbar.findViewById(R.id.tvNotificationCount);
        viewComputerLogin = view.findViewById(R.id.viewComputerLogin);
        tvDairyCenter = view.findViewById(R.id.tvDairyCenter);
        viewBanner = view.findViewById(R.id.viewBanner);
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        recyclerView = view.findViewById(R.id.recyclerview);
        viewRefresh = view.findViewById(R.id.viewRefresh);
        tvLastRefresh = view.findViewById(R.id.tvLastRefresh);
        imgDot.setVisibility(View.VISIBLE);
        imgWhatsApp.setVisibility(View.VISIBLE);
        imgHelp.setVisibility(View.VISIBLE);
        layoutNotification.setVisibility(View.VISIBLE);
        imgWhatsApp.setOnClickListener(this);
        imgHelp.setOnClickListener(this);
        imgDot.setOnClickListener(this);

        toolbar.setTitle(mContext.getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer_primary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        viewComputerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromWhere = "weblogin";
                Intent intent = new Intent(mContext, QrScannerActivity.class);
                intent.putExtra("from", "weblogin");
                startActivity(intent);
            }
        });
        viewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackBar;
                if (isNetworkAvaliable(mContext)) {
                    syncWebAndAppData();

                    Calendar c = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
                    String formattedDate = df.format(c.getTime());

                    get20daysdata(mContext,true,formattedDate);

                    checkUserPlanExpiryStatus(mContext);
                    snackBar = Snackbar.make(v, mContext.getString(R.string.syncMilkData) + "...", Snackbar.LENGTH_LONG);
                    snackBar.setActionTextColor(Color.BLUE);
                    View snackBarView = snackBar.getView();
                    TextView textView = snackBarView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackBar.show();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                          //  db.deleteAllBuyMilkEntryRecord();
                            db.deleteAllSaleMilkEntry();
                            userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
                        }
                    }, 3000);
                } else {
                    snackBar = Snackbar.make(v, mContext.getString(R.string.you_are_not_connected_to_internet), Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }
        });
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("from", "dairyDashboard");
                fragment = new NotificationFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.dairy_container, fragment).addToBackStack(null).commit();
            }
        });

        name = sessionManager.getValueSesion(KEY_Name);
        mobileNo = sessionManager.getValueSesion(KEY_Mobile);
        if (sessionManager.getValueSesion(KEY_AdvtStatus).equals("0")) {
            listViewCount = 2;
            viewBanner.setVisibility(View.GONE);
        } else {
            viewBanner.setVisibility(View.VISIBLE);
        }
        dairyCenter = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_center_name));
        if (sessionManager.getValueSesion(KEY_PlantId) != null) {
            plantId = nullCheckFunction(sessionManager.getValueSesion(KEY_PlantId));
        }
        dashboardRecyclerViewUI();
        imageBannerSlide();
        tvDairyCenter.setText(dairyCenter);
        getShiftAccordingTime();
        syncWebAndAppData();
        getSmsStatus();
        return view;

    }

    public void syncWebAndAppData() {

        if (isNetworkAvaliable(mContext)) {
            // Buy Milk
            uploadEntryToServer(mContext, "Deshboard", this);
            //   Sale Milk
            uploadSaleMilkEntryToServer(mContext, "Deshboard", this);
            // Plant Buy Milk
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uploadPlantMilkEntryToServer(mContext, "Deshboard");
                    // Plant Sale Milk
                    uploadPlantSaleMilkEntryToServer(mContext, "Deshboard");
                    SnfFatChartFragment.getSnfStatus(mContext);





                    ArrayList<CustomerEntryListPojo> mList = db.getMilkBuyEntryRecordsOffline();
                    if (mList.size()==0){
                       // matchcountdata(mContext,false);


                    }
                }
            });
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        tvLastRefresh.setText(mContext.getString(R.string.lastRefreshOn) + " : " + formattedDate);
        tvLastRefresh.setSelected(true);
    }


    private void getSmsStatus() {
        @SuppressLint("StaticFieldLeak")
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait Checking SMS status..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    sessionManager.setValueSession(SessionManager.Key_SendTotaLYES, jsonObject.getString("setting_status"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
               // .addEncoded("setting_status", sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(smstendayssettingget);
    }



    @SuppressLint("SetTextI18n")
    private void notificationCountUpdate() {
        notif_count = sessionManager.getIntValueSesion(KEY_Notification);
        if (notif_count > 0) {
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(Integer.toString(notif_count));
        } else {
            tvNotificationCount.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FIREBASE_REQ_ACCEPT));
        imageBannerSlide();
        notificationCountUpdate();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    public void firstTimeLoadSetting() {
        if (ConnectivityReceiver.isConnected()) {
            if (FirstTime.equals("Yes")) {
                SnfFatChartFragment.getBonusPrice(mContext);
                getDairyAllSNF_FATChart(mContext, "buy", true);
                getDairyAllSNF_FATChart(mContext, "sale", true);
                FirstTime = "";

                Calendar c = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
                String formattedDate = df.format(c.getTime());
                get20daysdata(mContext,true,formattedDate);
            }
        }
    }


    private void imageBannerSlide() {
        if (!sessionManager.getValueSesion(KEY_AdvtStatus).equals("0")) {
            banerLists = new ArrayList<BeanOfferBanerList>();
            banerLists = db.getBannerOfferList();
            bannerDasboardPagerAdapter = new BannerDasboardPagerAdapter(mContext, banerLists);
            viewPager.setAdapter(bannerDasboardPagerAdapter);

            indicator.setViewPager(viewPager);
            float density = getResources().getDisplayMetrics().density;
            //Set circle indicator radius
            indicator.setRadius(5 * density);
            NUM_PAGES = banerLists.size();
            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 150000, 5000);

            // Pager listener over indicator
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;

                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int pos) {

                }
            });
        }

    }

    private void openWhatsApp() {
        boolean installed = appInstalledOrNot(mContext, "com.whatsapp");

        if (installed) {
            Uri uri = Uri.parse("smsto:" + "9772196777");
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");

            startActivity(Intent.createChooser(i, "Hello Meri Dairy"));
        } else {
            showToast(mContext, "Whatsapp not installed.");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    showToast(mContext, "Unable to get Permission");
                }
            }
        }
    }


    public void onBackPressed() {
        AlertDialog.Builder builder;
//     requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(getString(R.string.Exyt_App))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            /*   (Activity) mContext.finishAffinity();*/
                        } else {
                            ActivityCompat.finishAffinity((Activity) mContext);
                        }
                        // Close all activites
                        System.exit(0);  // Releasing resources

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgDot:
                changLanguage(v);
                break;
            case R.id.imgWhatsApp:

                openWhatsApp();
                break;
            case R.id.imgHelp:
                Intent i = new Intent(mContext, Help_activity.class);
                mContext.startActivity(i);
                break;
            default:
        }
    }


    private void dashboardRecyclerViewUI() {
        dashboard_items = new ArrayList<>();
        Integer intplantId = 0;
        if (plantId.length() > 0) {
            intplantId = Integer.valueOf(plantId);
        }
        dashboard_items.add(new Dashboard_item("buy_Milk", mContext.getResources().getString(R.string.Milk_Buy), "", R.drawable.add_entry, "#0698D4"));
        dashboard_items.add(new Dashboard_item("sale_milk", mContext.getResources().getString(R.string.MILK_Sale), "", R.drawable.sale_milk, "#F67570"));
        dashboard_items.add(new Dashboard_item("customer", mContext.getResources().getString(R.string.Customers), "", R.drawable.ic_menu_customer, "#935E88"));
        dashboard_items.add(new Dashboard_item("payment_register", mContext.getResources().getString(R.string.payment) + "\n" + mContext.getResources().getString(R.string.Register), "", R.drawable.customer_entry, "#F39357"));
        dashboard_items.add(new Dashboard_item("new_payment_register", mContext.getResources().getString(R.string.New) + " " + mContext.getResources().getString(R.string.payment) + "\n" + mContext.getResources().getString(R.string.Register), mContext.getResources().getString(R.string.New), R.drawable.customer_entry, "#F39357"));
        dashboard_items.add(new Dashboard_item("view_entry", mContext.getResources().getString(R.string.View_All_Entry), "", R.drawable.ic_view_all_entry, "#35CE8E"));
        dashboard_items.add(new Dashboard_item("view_entry_by_date", mContext.getResources().getString(R.string.viewEntry) + "\n" + mContext.getResources().getString(R.string.byDate), "", R.drawable.ic_view_entry, "#35CE8E"));
        dashboard_items.add(new Dashboard_item("transaction", mContext.getResources().getString(R.string.transactions) + "\n" + mContext.getResources().getString(R.string.history), "", R.drawable.ic_wallet, "#5687BA"));
        dashboard_items.add(new Dashboard_item("sale_product", mContext.getResources().getString(R.string.Sale) + "\n" + mContext.getResources().getString(R.string.Products), "", R.drawable.ic_grain_bag, "#0698D4"));
        dashboard_items.add(new Dashboard_item("sale_purchase", mContext.getResources().getString(R.string.product_SaleAndBuy), "", R.drawable.ic_product_sale_buy, "#0698D4"));
        dashboard_items.add(new Dashboard_item("snf_chart", mContext.getResources().getString(R.string.Fat_SNF) + "\n" + mContext.getResources().getString(R.string.chart), "", R.drawable.ic_snf_chart, "#35CE8E"));
        dashboard_items.add(new Dashboard_item("invoice", mContext.getResources().getString(R.string.milkBill), "", R.drawable.ic_invoice, "#5687BA"));
        dashboard_items.add(new Dashboard_item("khata", mContext.getResources().getString(R.string.khata), mContext.getResources().getString(R.string.New), R.drawable.ic_book_ledger_khata, "#5687BA"));
        dashboard_items.add(new Dashboard_item("bulk_milk", mContext.getResources().getString(R.string.bulk) + " " + mContext.getResources().getString(R.string.milk) + "\n" + mContext.getResources().getString(R.string.history), "", R.drawable.ic_vehicle, "#935E88"));

        dashboard_items.add(new Dashboard_item("member_ship", mContext.getResources().getString(R.string.membership), userRemainingDay + "", R.drawable.ic_membership, "#FF0800"));
        dashboard_items.add(new Dashboard_item("message", mContext.getResources().getString(R.string.message), userRemainingSMS + "", R.drawable.ic_message, "#5687BA"));
        dashboard_items.add(new Dashboard_item("notification", mContext.getResources().getString(R.string.Send) + " " + mContext.getResources().getString(R.string.notification), "", R.drawable.ic_notifications, "#5687BA"));

        dashboard_items.add(new Dashboard_item("shopping", mContext.getResources().getString(R.string.Shopping), "", R.drawable.ic_shop, "#45464B"));
        if (intplantId > 0) {
            dashboard_items.add(new Dashboard_item("plant_milk", mContext.getResources().getString(R.string.plantMilkCollection), "", R.drawable.ic_milk_home_marker, "#F67570"));
        }
        dashboard_items.add(new Dashboard_item("setting", mContext.getResources().getString(R.string.Setting), "", R.drawable.ic_settings, "#F67570"));

        dashboard_itemAdapter = new DashboardItemAdapter(mContext, dashboard_items, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, listViewCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(listViewCount, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dashboard_itemAdapter);
        if (FirstTime.equals("Yes")) {
            dashboardRecyclerViewUI();
        }

    }

    @Override
    public void onClickEditInAdapter(int position) {
        Dashboard_item dashboard_item = dashboard_items.get(position);
        String title = dashboard_item.getId();
        Fragment fragment = null;
        Bundle bundle = null;
        String userStatus = sessionManager.getValueSesion(Key_User_Status);
        System.out.println("title===>>" + title);
        switch (title) {
            case "buy_Milk":
                if (isNetworkAvaliable(mContext)) {
                    checkUserPlanExpiryStatus(mContext);
                    getSMSBalance(mContext, GET_TASK);
                }
                if (checkUserIsActive()) {
                    if (sessionManager.getValueSesion(Key_BuyMilkScreen).equalsIgnoreCase(ONE)) {
                        SelectedDate = getSimpleDate();
                       // sessionManager.db.deleteMilkEntryShiftWise("buy", SelectedDate, strSession);
                        fragment = new PurchaseMilkEntryFullScreenFragment();
                    } else {
                        fragment = new PurchaseMilkDateTimeFragment();
                    }
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "sale_milk":
                if (isNetworkAvaliable(mContext)) {
                    getBonusPrice(mContext);
                    checkUserPlanExpiryStatus(mContext);
                    setMessageSetting(mContext, GET_TASK);
                    getSMSBalance(mContext, GET_TASK);
                }
                if (checkUserIsActive()) {
                    if (sessionManager.getValueSesion(Key_SaleMilkScreen).equals(ONE)) {
                        sessionManager.db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
                        SelectedDate = getSimpleDate();
                        fragment = new SaleMilkEntryFullScreenFragment();

                    } else {
                        fragment = new SaleEntryDateTimeFragment();
                    }

                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "customer":
                FromWhere = "btnCustomer";
                FromWhere2 = "";
                fragment = new BuyerSellerCustomerListFragment();
                bundle = new Bundle();
                bundle.putString("FromWhere", "Dashboard");
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "payment_register":
                FromWhere = "Dashboard";
                fragment = new SallerBhugtanFragment();
                bundle = new Bundle();
                bundle.putString("fromWhere", "Dashboard");
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "new_payment_register":
                FromWhere = "Dashboard";
                if (checkUserIsActive()) {
                    fragment = new PaymentTabFragment();
                    bundle = new Bundle();
                    bundle.putString("fromWhere", "Dashboard");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                    break;
                }
            case "view_entry":
                bundle = new Bundle();
                bundle.putString("FromWhere", "Dashboard");
                fragment = new ViewMilkEntryTabFragment();
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "view_entry_by_date":
                fragment = new ViewEntryBothShiftTabFragment();
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "transaction":
                if (checkUserIsActive()) {
                    FromWhere = "Dashboard";
                    fragment = new CustomerSallerListFragment();
                    bundle = new Bundle();
                    bundle.putString("FromWhere", "Dashboard");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "sale_product":
                if (checkUserIsActive()) {
                    FromWhere = "ExistUser";
                    fragment = new BuyerSellerCustomerListFragment();
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "sale_purchase":
                if (checkUserIsActive()) {
                    fragment = new ProductDashboard();
                    bundle = new Bundle();
                    bundle.putString("FromWhere", "Dashboard");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "bulk_milk":
                fragment = new BMCDashboard();
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "shopping":
                fragment = new ShoppingFragment();
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "plant_milk":
                if (checkUserIsActive()) {
                    getVehicleDairyList(mContext, "3", true);
                    getVehicleDairyList(mContext, "2", true);
                    fragment = new PlantCollectionAndOutLetFragment();
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "snf_chart":
                if (checkUserIsActive()) {
                    fragment = new ChartTabFragment();
                    bundle = new Bundle();
                    bundle.putString("from", "dashboard");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "member_ship":
                fragment = new FragmentMembershipUpgrade();
                bundle = new Bundle();
                bundle.putString("from", "dashboard");
                bundle.putString("type", "membership");
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "message":
                fragment = new FragmentMessagePlan();
                bundle = new Bundle();
                bundle.putString("from", "dashboard");
                bundle.putString("type", "sms");
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case "notification":
                if (checkUserIsActive()) {
                    fragment = new DairyAllCustomerlist();
                    bundle = new Bundle();
                    bundle.putString("from", "dashboard");
                    bundle.putString("type", "notification");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "invoice":
                if (checkUserIsActive()) {
                    fragment = new InvoiceTabFragment();
                    bundle = new Bundle();
                    bundle.putString("from", "dashboard");
                    bundle.putString("type", "invoice");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "khata":
                if (checkUserIsActive()) {
                    fragment = new VoucherTabFragment();
                    bundle = new Bundle();
                    bundle.putString("from", "dashboard");
                    bundle.putString("type", "voucher");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
            case "setting":
                if (checkUserIsActive()) {
                    fragment = new SettingTabFragment();
                    bundle = new Bundle();
                    bundle.putString("from", "dashboard");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
                break;
        }

    }

    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public boolean checkUserIsActive() {
        boolean isActive = true;
        userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
        if (userRemainingDay <= 0) {
            isActive = false;
            fragment = new FragmentMembershipUpgrade();
            Bundle bundle = new Bundle();
            bundle.putString("from", "dashboard");
            bundle.putString("type", "membership");
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }


        return isActive;
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
        ((Activity) mContext).finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (swipeTimer != null) {
            swipeTimer.cancel();
        }
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

    @Override
    public void onMilkEntryUploaded(String from) {

    }
}
