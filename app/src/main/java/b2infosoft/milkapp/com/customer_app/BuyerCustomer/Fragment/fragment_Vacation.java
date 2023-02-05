package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
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

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanViewCalenderItem;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.customerAddVacationAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.customerMonthDataAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_CustomerUserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_Vacation extends Fragment implements CompoundButton.OnCheckedChangeListener {


    Context mContext;
    View view;
    Calendar calendar;
    Toolbar toolbar;
    CalendarView mCalendarView;
    Button btnVacation;

    List<EventDay> events = new ArrayList<>();
    List<BeanViewCalenderItem> calenderItemList = new ArrayList<>();
    List<String> webVacactionList = new ArrayList<>();
    int year, currentMonth, date;
    List<Calendar> selecteddate;
    List<String> tempSelectedList;
    int webMonth = 0;
    String milkStatus = "none";
    String strMorning = "1", strEvening = "1";
    boolean clickDate = false;
    List<String> cancelVacList = new ArrayList<>();
    List<String> updateVacList = new ArrayList<>();
    SessionManager sessionManager;
    CheckBox checkBoxMorning, checkBoxEvening;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_vacation, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        webVacactionList = new ArrayList<>();
        selecteddate = new ArrayList<>();
        tempSelectedList = new ArrayList<>();
        cancelVacList = new ArrayList<>();
        updateVacList = new ArrayList<>();
        toolbar = view.findViewById(R.id.toolbar);
        checkBoxMorning = view.findViewById(R.id.checkBoxMorning);
        checkBoxEvening = view.findViewById(R.id.checkBoxEvening);
        btnVacation = view.findViewById(R.id.btnVacation);

        checkBoxMorning.setOnCheckedChangeListener(this);
        checkBoxEvening.setOnCheckedChangeListener(this);


        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.Vacation);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        initCurrentDate();
        checkCalenderFunctionality("click");
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                clickDate = true;
                checkCalenderFunctionality("click");
            }
        });


        getUserdata();

        mCalendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                if (webMonth == 12) {
                    webMonth = 0;
                }
                webMonth++;
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
                getUserdata();
            }
        });

        btnVacation.setOnClickListener(v -> {
            //    mCalendarView.getFirstSelectedDate();

            checkCalenderFunctionality("btn");

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initCurrentDate();
    }

    private void initCurrentDate() {
        mCalendarView = view.findViewById(R.id.mCalendarView);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, 0);

        mCalendarView.setMinimumDate(min);
        calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DATE);
        year = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        webMonth = calendar.get(Calendar.MONTH) + 1;


    }

    private void checkCalenderFunctionality(String from) {
        int webVacation = webVacactionList.size();
        cancelVacList = new ArrayList<>();
        updateVacList = new ArrayList<>();
        int localVac = 0;
        if (clickDate) {
            btnVacation.setVisibility(View.VISIBLE);
            tempSelectedList = new ArrayList<>();
            String day = "", month = "", year = "";

            for (Calendar calendar : mCalendarView.getSelectedDates()) {
                System.out.println("Date==" + calendar.getTime().toString());
                System.out.println("Day==" + calendar.get(Calendar.DAY_OF_MONTH));
                day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                int vacMonth = calendar.get(Calendar.MONTH) + 1;
                if (vacMonth < 10) {
                    month = "0" + vacMonth;
                } else {
                    month = String.valueOf(vacMonth);
                }

                year = String.valueOf(calendar.get(Calendar.YEAR));
                tempSelectedList.add(day + "-" + month + "-" + year);
            }

            if (!tempSelectedList.isEmpty()) {
                String listwebString = TextUtils.join(", ", webVacactionList);
                String listString = TextUtils.join(", ", tempSelectedList);

                System.out.println("webVacactionList==array===" + listwebString);
                System.out.println("tempSelectedList==array======" + listString);
                updateVacList = new ArrayList<>();

                for (int i = 0; i < webVacactionList.size(); i++) {

                    if (!tempSelectedList.contains(webVacactionList.get(i))) {

                        updateVacList.add(webVacactionList.get(i));
                    }
                }

                for (int j = 0; j < tempSelectedList.size(); j++) {

                    if (!webVacactionList.contains(tempSelectedList.get(j))) {

                        updateVacList.add(tempSelectedList.get(j));
                    }

                }

                String updatedList = TextUtils.join(", ", updateVacList);
                System.out.println("set filter==updated===" + updatedList);

            } else {
                updateVacList.addAll(webVacactionList);
            }

            if (webVacation == 0) {
                btnVacation.setText(mContext.getString(R.string.AddVacation));
            } else {
                btnVacation.setText(mContext.getString(R.string.UpdateVacation));
            }
        } else {
            btnVacation.setVisibility(View.GONE);
        }


        if (from.equalsIgnoreCase("btn")) {
            if (updateVacList.size() == 0 && webVacactionList.size() == 0) {
                showToast(mContext, "Please Select Date");
            } else {

                if (tempSelectedList.size() == 0 && webVacactionList.size() > 0) {
                    addVacation(webVacactionList);

                } else {
                    addVacation(updateVacList);
                }

            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBoxMorning:
                if (isChecked) {
                    strMorning = "1";
                } else {
                    strMorning = "0";
                }
                break;
            case R.id.checkBoxEvening:
                if (isChecked) {
                    strEvening = "1";
                } else {
                    strEvening = "0";
                }
                break;
        }
    }

    private void setCalanderEvent() {
        webVacactionList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        System.out.println("Current==>>>" + "Month==:- " + currentMonth + " Year==:-" + year);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR, year);
        int vieMonth = webMonth - 1;
        List<Calendar> selectedDate = new ArrayList<>();
        for (int i = 0; i < calenderItemList.size(); i++) {
            milkStatus = calenderItemList.get(i).getStatus();
            int day = calenderItemList.get(i).getDay();

            calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, vieMonth);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            if (milkStatus.equalsIgnoreCase("0")) {
                String strday = "", month = "", year = "";
                strday = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                int vacMonth = calendar.get(Calendar.MONTH) + 1;
                if (vacMonth < 10) {
                    month = "0" + vacMonth;
                } else {
                    month = String.valueOf(vacMonth);
                }
                year = String.valueOf(calendar.get(Calendar.YEAR));
                webVacactionList.add(strday + "-" + month + "-" + year);

                selectedDate.add(calendar);
            }
        }

        mCalendarView.setSelectedDates(selectedDate);
        checkCalenderFunctionality("Event");

    }

    private void getUserdata() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Fetching...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            calenderItemList = new ArrayList<>();
                            String date = jsonObject.getString("month").trim();
                            String[] split = date.split("-");
                            String strmonth = split[0];
                            String stryear = split[1];
                            webMonth = Integer.parseInt(strmonth);
                            year = Integer.parseInt(stryear);
                            System.out.println(" date==>> " + date + " month==>> " + webMonth + " year==>>  " + year);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonObj = jsonData.getJSONObject(i);
                                calenderItemList.add(new BeanViewCalenderItem(jsonObj.getInt("day"),
                                        jsonObj.getString("status")));
                            }

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
                    .addEncoded("month", apiMonth).build();
            caller.addRequestBody(body);


            caller.execute(customerMonthDataAPI);

        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }

    private void addVacation(List<String> tempSelectedList) {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Updating...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            String listString = TextUtils.join(", ", tempSelectedList);
            System.out.println("listString==array===" + listString);

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("customer_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .addEncoded("entry_date", listString).build();
            caller.addRequestBody(body);

            caller.execute(customerAddVacationAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }
}



