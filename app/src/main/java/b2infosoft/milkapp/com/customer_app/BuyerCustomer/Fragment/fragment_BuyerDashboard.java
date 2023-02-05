package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.AdvBannerPojo;
import b2infosoft.milkapp.com.Notification.NotificationFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.DeliveredMilkPlan_Item_adapter;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.MyPlan_Item_adapter;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomPagerAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDashboardCalenderItem;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDeliveredMilkPlan;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
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
import static b2infosoft.milkapp.com.appglobal.Constant.customerMonthDataAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getDairyAdsAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_CustomerUserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_ProductAddDate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getEmojiByUnicode;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragment_BuyerDashboard extends Fragment implements View.OnClickListener {
    View view;
    SessionManager sessionManager;
    ArrayList<AdvBannerPojo> bannerList = new ArrayList<>();
    Calendar calendar;
    List<EventDay> events = new ArrayList<>();
    List<BeanDashboardCalenderItem> calenderItemList = new ArrayList<>();
    BeanDashboardCalenderItem beanViewCalenderItem;
    Fragment fragment;
    private Context mContext;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private Timer timer;
    private View layoutNotification;
    private ImageView imgNotification, imgMoreThreeDots;
    private TextView tvNotificationCount;
    private CalendarView mCalendarView;
    private TextView tvAddVacation;
    private Button btnVacation;
    private int notif_count = 0, year = 0, currentMonth = 0, date = 0, webMonth = 0;
    private BroadcastReceiver receiver;
    private String userGroupId = "4", milkStatus = "none", strproductDate = "";
    private Calendar caltodayDate = Calendar.getInstance();

    private boolean upcomingPlan = false;
    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_customer_deshboard, container, false);
        mContext = getActivity();
        db = DatabaseHandler.getDbHelper(mContext);
        initView();

        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        sessionManager = new SessionManager(mContext);
        userGroupId = sessionManager.getValueSesion(Key_UserGroupID);
        timer = new Timer();
        toolbar = view.findViewById(R.id.toolbar);
        tvAddVacation = toolbar.findViewById(R.id.tvAddVacation);
        layoutNotification = toolbar.findViewById(R.id.layoutNotification);
        imgNotification = toolbar.findViewById(R.id.imgNotification);
        imgMoreThreeDots = toolbar.findViewById(R.id.imgDot);
        tvNotificationCount = toolbar.findViewById(R.id.tvNotificationCount);
        mCalendarView = view.findViewById(R.id.calendarView);
        btnVacation = view.findViewById(R.id.btnVacation);

        tvAddVacation.setVisibility(View.VISIBLE);
        layoutNotification.setVisibility(View.VISIBLE);
        imgMoreThreeDots.setVisibility(View.VISIBLE);
        imgMoreThreeDots.setOnClickListener(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT)
        );
        notificationCountUpdate();
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("from", "customerdashboard");
                fragment = new NotificationFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();
            }
        });
        tvAddVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("from", "dashboard");
                fragment = new fragment_Vacation();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.Home);
        getCustomerAdsBanners();
        getBanners();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        mCalendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                if (webMonth == 12) {
                    webMonth = 0;
                }
                webMonth++;
                year = calendar.get(Calendar.YEAR);
                getUserdata();
            }
        });
        mCalendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                if (webMonth == 0) {
                    webMonth = 12;
                }

                webMonth--;
                year = calendar.get(Calendar.YEAR);

                getUserdata();
            }
        });

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                previewNote(eventDay);
            }
        });

    }


    private void setCalanderEvent() {
        events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR, year);
        int viewMonth = webMonth - 1;
        System.out.println(
                "Current=" + "Month==>> " + currentMonth +
                        "viewMonth==>> " + viewMonth +
                        "Year==>>  " + year);

        for (int i = 0; i < calenderItemList.size(); i++) {
            milkStatus = calenderItemList.get(i).getStatus();
            int day = calenderItemList.get(i).getDay();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, viewMonth);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            if (milkStatus.equalsIgnoreCase("none") && calendar.after(caltodayDate)) {
                events.add(new EventDay(calendar, R.drawable.ic_dot_blue));
            } else if (milkStatus.equalsIgnoreCase("1")) {
                events.add(new EventDay(calendar, R.drawable.ic_dot_green));
            } else if (milkStatus.equalsIgnoreCase("0")) {
                events.add(new EventDay(calendar, R.drawable.ic_dot_red));
            } else {
                events.add(new EventDay(calendar));
            }
        }
        mCalendarView.setEvents(events);

    }

    private void previewNote(EventDay eventDay) {
        if (!calenderItemList.isEmpty()) {
            Calendar calendar = eventDay.getCalendar();
            int position = calendar.get(Calendar.DAY_OF_MONTH);
            position = position - 1;
            beanViewCalenderItem = calenderItemList.get(position);
            String eventStatus = beanViewCalenderItem.getStatus();
            int month = calendar.get(Calendar.MONTH) + 1;
            if (eventStatus.equalsIgnoreCase("none")) {

                strproductDate = calendar.get(Calendar.YEAR) + "-" +
                        month + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                if (calendar.after(caltodayDate)) {
                    upcomingPlan = true;
                    dialogSelectOption();
                }
            } else {
                dialogEventView();
            }
        } else {
            showToast(mContext, "No Event details ");
        }


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


    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT)
        );
        notificationCountUpdate();
        initCurrentDate();

    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    private void initCurrentDate() {

        mCalendarView = view.findViewById(R.id.calendarView);
        // setcalanderMindDate();
        calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DATE);
        year = calendar.get(Calendar.YEAR);
        caltodayDate = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        try {
            mCalendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        webMonth = calendar.get(Calendar.MONTH) + 1;

        getUserdata();
    }

    private void dialogSelectOption() {
        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);
        ImageView imgClosed;
        TextView tvBuyproduct, tvViewEvent;
        tvBuyproduct = dialog.findViewById(R.id.tvBuyproduct);
        tvViewEvent = dialog.findViewById(R.id.tvViewEvent);
        imgClosed = dialog.findViewById(R.id.imgClosed);

        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvBuyproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteBuyerCartTable();
                dialog.dismiss();
                sessionManager.setValueSession(KEY_ProductAddDate, strproductDate);
                Bundle bundle = new Bundle();
                bundle.putString("from", "dashboard");
                fragment = new fragment_BuyProduct();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();

            }
        });

        tvViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogEventView();
            }
        });

        dialog.show();
    }

    public void dialogEventView() {


        List<BeanMilkPlan> milkPlans = new ArrayList<>();
        List<BeanDeliveredMilkPlan> deliveredMilkPlanList = new ArrayList<>();
        DeliveredMilkPlan_Item_adapter adapter = null;
        MyPlan_Item_adapter planItemAdapter = null;
        String eventStatus = beanViewCalenderItem.getStatus();


        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_view_event);
        ImageView imgClosed;
        RecyclerView recyclerView;
        TextView tvDialogTitle, tv_emty;


        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tv_emty = dialog.findViewById(R.id.tv_emty);
        recyclerView = dialog.findViewById(R.id.recyclerView);

        imgClosed = dialog.findViewById(R.id.imgClosed);

        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (eventStatus.equalsIgnoreCase("1")) {
            tvDialogTitle.setText("Milk Delivered");
            deliveredMilkPlanList = beanViewCalenderItem.getMilkPlanList();
            recyclerView.setVisibility(View.VISIBLE);
            tv_emty.setVisibility(View.GONE);

            adapter = new DeliveredMilkPlan_Item_adapter(mContext, deliveredMilkPlanList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        } else if (eventStatus.equalsIgnoreCase("none")) {
            if (upcomingPlan) {
                tvDialogTitle.setText("Milk Upcoming Plan");

            }
            recyclerView.setVisibility(View.VISIBLE);
            tv_emty.setVisibility(View.GONE);
            //milkPlans = db.getMyPlan();


            planItemAdapter = new MyPlan_Item_adapter(mContext, milkPlans);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(planItemAdapter);
        } else {
            tvDialogTitle.setText(mContext.getString(R.string.milk_Vacation));
            recyclerView.setVisibility(View.GONE);
            tv_emty.setVisibility(View.VISIBLE);

            String text = mContext.getString(R.string.This_is_Vacation) + getEmojiByUnicode(0x1F60A);
            tv_emty.setText(text);
        }

        dialog.show();
    }

    private void getUserdata() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Fetching...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        events = new ArrayList<>();
                        calenderItemList = new ArrayList<>();
                        if (jsonObject.getBoolean("success")) {

                            String date = jsonObject.getString("month").trim();
                            String[] split = date.split("-");
                            String strmonth = split[0];
                            String stryear = split[1];

                            webMonth = Integer.parseInt(strmonth);
                            year = Integer.parseInt(stryear);
                            System.out.println(" date==>> " + date +
                                    " month==>> " + webMonth +
                                    " year==>>  " + year);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++) {

                                List<BeanDeliveredMilkPlan> milkPlanList = new ArrayList<>();
                                JSONObject jsonObj = jsonData.getJSONObject(i);

                                if (jsonObj.has("plans")) {

                                    JSONArray jsonPlanArray = jsonObj.getJSONArray("plans");

                                    for (int j = 0; j < jsonPlanArray.length(); j++) {

                                        JSONObject planObj = jsonPlanArray.getJSONObject(j);

                                        milkPlanList.add(new BeanDeliveredMilkPlan(
                                                planObj.getString("dairy_id"),
                                                planObj.getString("plan_id"),
                                                planObj.getString("plan_name"),
                                                planObj.getString("shift"),
                                                planObj.getInt("qty"),
                                                planObj.getString("milk_entry_date"),
                                                planObj.getString("milk_wt"),
                                                planObj.getDouble("milk_perkg_price"),
                                                planObj.getDouble("milk_total_price")
                                        ));

                                    }
                                }
                                calenderItemList.add(new BeanDashboardCalenderItem(jsonObj.getInt("day"),
                                        jsonObj.getString("status"), milkPlanList

                                ));

                            }

                            setCalanderEvent();

                        } else {
                            setCalanderEvent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            String apiMonth = getMonthFormat(webMonth) + "-" + year;

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("customer_id", sessionManager.getValueSesion(KEY_CustomerUserID))
                    .addEncoded("month", apiMonth)
                    .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                    .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                    .build();
            caller.addRequestBody(body);


            caller.execute(customerMonthDataAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }

    private void getBanners() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please Wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObj = new JSONObject(response);
                    events = new ArrayList<>();
                    calenderItemList = new ArrayList<>();
                    if (jsonObj.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bannerList.add(new AdvBannerPojo(
                                    jsonObject.getString("id")
                                    , jsonObject.getString("date_from"), jsonObject.getString("date_to")
                                    , jsonObject.getString("title"), jsonObject.getString("discription")
                                    , jsonObject.getString("location"), jsonObject.getString("latitude")
                                    , jsonObject.getString("longitude"), jsonObject.getString("adlogo")
                            ));
                        }
                        swipeLayout(bannerList);
                    }
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
                    String staus = jsonObj.getString("status");

                    if (staus.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        String imgPath = jsonObj.getString("path");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bannerList.add(new AdvBannerPojo(
                                    jsonObject.getString("id")
                                    , jsonObject.getString("start_date")
                                    , jsonObject.getString("start_date")
                                    , jsonObject.getString("title")
                                    , jsonObject.getString("description")
                                    , jsonObject.getString("cities"), "", ""
                                    , imgPath + jsonObject.getString("image")
                            ));
                        }

                        swipeLayout(bannerList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(getDairyAdsAPI + "userid=" + sessionManager.getValueSesion(KEY_UserID));
    }

    private void swipeLayout(final ArrayList<AdvBannerPojo> bannerList) {

        timer = new Timer();
        viewPager = view.findViewById(R.id.pager);
        CustomPagerAdapter adapter = new CustomPagerAdapter((Activity) mContext, bannerList, mContext);
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
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgDot) {
            changLanguage(v);
        }
    }

    private void changLanguage(View view) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, imgMoreThreeDots);
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
        Resources res = mContext.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, CustomerBuyerMainActivity.class);
        startActivity(refresh);
        ((Activity) mContext).finish();
    }
}
