package b2infosoft.milkapp.com.Dairy.ViewMilkEntry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.Adapter.MilkHistoryAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Model.MilkHistoryPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Microsoft on 17-Nov-17.
 */

public class ViewMilkHistoryFragment extends Fragment implements View.OnClickListener,
        FragmentBackPressListener {

    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvStartDate, tvEndDate;
    SessionManager sessionManager;
    AutoCompleteTextView actv_CustomerID;
    ImageView imgStartDate, imgEndDate;
    RecyclerView milk_history;
    TextView tvTotalWeight, tvAverageFat, tvFatRat, tvTotalAmount;
    Button btnSubmit;
    String formattedDate = "";
    String strStartDate = "";
    String strEndDate = "";
    String formattedDate_start = "";
    String formattedDate_end = "";
    String dairy_id = "";
    long days = 0;
    String  dbCurrentDate = "";
    DatabaseHandler databaseHandler;
    String dbBeforeDays = "";
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    Fragment fragment = null;
    View view;
    ArrayList<MilkHistoryPojo> milkHistoryListPojos = new ArrayList<>();
    private int mYear, mMonth, mDay, mHour, mMinute;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_milk_history, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        initView();
        return view;
    }

    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        toolbarManage();
        toolbar_title.setText(mContext.getString(R.string.MILK_HISTORY));
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvAverageFat = view.findViewById(R.id.tvAverageFat);
        tvFatRat = view.findViewById(R.id.tvFatRat);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        milk_history = view.findViewById(R.id.milk_history);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        dairy_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        strEndDate = df.format(c.getTime());
        dbCurrentDate = df2.format(c.getTime());
        tvEndDate.setText(strEndDate);
        getCalculatedDate();

    }

    public void getCalculatedDate() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -10);
        strStartDate = now.get(Calendar.DATE) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
        Date date = now.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
          strStartDate = dateFormat.format(date);
        dbBeforeDays = now.get(Calendar.YEAR) + "-" + checkDigit((now.get(Calendar.MONTH) + 1)) + "-" + now.get(Calendar.DATE);
        tvStartDate.setText(strStartDate);
        if (ConnectivityReceiver.isConnected()) {
            getMilkHistoryList( );
        } else {
            milkHistoryListPojos = databaseHandler.getMilkHistoryByTwoDate(dbBeforeDays, dbCurrentDate);
            setMilkHistoryList(milkHistoryListPojos);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgStartDate:
            case R.id.tvStartDate:
                getDate("StartDate");

                break;
            case R.id.imgEndDate:
            case R.id.tvEndDate:
                if (!strStartDate.equals("")) {
                    getDate("EndDate");
                } else {
                    showToast(mContext, mContext.getString(R.string.Please_select_start_date_first));
                }
                break;
            case R.id.btnSubmit:
                strStartDate=tvStartDate.getText().toString().trim();
                strEndDate=tvEndDate.getText().toString().trim();
                if (strStartDate.equals("")) {
                    showToast(mContext, mContext.getString(R.string.Please_select_start_date_first));
                } else if (strEndDate.equals("")) {
                    showToast(mContext, mContext.getString(R.string.Please_select_end_date_first) );
                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        getMilkHistoryList( );
                    } else {
                        Log.e("dbCurrentDate", "" + dbCurrentDate);
                        Log.e("dbBeforeDays", "" + dbBeforeDays);
                        milkHistoryListPojos = databaseHandler.getMilkHistoryByTwoDate(dbBeforeDays, dbCurrentDate);

                        setMilkHistoryList(milkHistoryListPojos);
                    }
                }
                break;
        }
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";
                        Log.d("Month>>>", month);
                        day = checkDigit(dayOfMonth);
                        //.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
                        if (from.equals("StartDate")) {
                            formattedDate_start = month + "/" + day + "/" + year;
                            strStartDate = day + "-" + month + "-" + year;
                            tvStartDate.setText(formattedDate);
                            dbBeforeDays = year + "-" + (checkDigit((monthOfYear) + 2) + "-" + day);
                        } else {
                            formattedDate_end = month + "/" + day + "/" + year;
                            dbCurrentDate = year + "-" + (checkDigit((monthOfYear) + 2) + "-" + day);
                            Date startDateValue = new Date(formattedDate_start);   // Date format should be month/days/year
                            Date endDateValue = new Date(formattedDate_end);
                            long diff = endDateValue.getTime() - startDateValue.getTime();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            days = (hours / 24) + 1;

                            if (days <= 10) {

                                strEndDate = day + "-" + month + "-" + year;
                                tvEndDate.setText(strEndDate);
                            } else {
                                formattedDate_end = "";

                                showToast(mContext, mContext.getString(R.string.Date_range_should_be_1_to_10_days) );
                            }
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }




    public void getMilkHistoryList(  ) {
        milkHistoryListPojos = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        JSONArray jsonArray = jsonObject1.getJSONArray("morning");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                            milkHistoryListPojos.add(new MilkHistoryPojo(
                                    nullCheckFunction(jsonObject.getString("total_milk")),
                                    nullCheckFunction(jsonObject.getString("total_price")),
                                    nullCheckFunction(jsonObject.getString("total_fat")),
                                    jsonObject.getString("dairy_id"),
                                    nullCheckFunction(jsonObject1.getString("entry_date")),
                                    "Morning"));
                        }
                        JSONArray jsonArray_ = jsonObject1.getJSONArray("evening");
                        for (int j = 0; j < jsonArray_.length(); j++) {
                            JSONObject jsonObject = jsonArray_.getJSONObject(j);
                            milkHistoryListPojos.add(new MilkHistoryPojo(
                                    nullCheckFunction(jsonObject.getString("total_milk")),
                                    nullCheckFunction(jsonObject.getString("total_price")),
                                    nullCheckFunction(jsonObject.getString("total_fat")),
                                    jsonObject.getString("dairy_id"),
                                    nullCheckFunction(jsonObject1.getString("entry_date")),
                                    "Evening"));
                        }
                    }

                    setMilkHistoryList(milkHistoryListPojos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("date_from", strStartDate)
                .addEncoded("date_to", strEndDate)
                .addEncoded("dairy_id", dairy_id).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMilkHistoryAPI);
    }
    public void setMilkHistoryList(ArrayList<MilkHistoryPojo> milkHistoryListPojos) {

        double Totfat = 0d,totalAmt = 0d,totalWeight = 0.0, avgFat = 0.0, fatRate = 0.0;
        int count = 0;
        for (int i = 0; i < milkHistoryListPojos.size(); i++) {

            if (milkHistoryListPojos.get(i).total_fat.length() != 0 && !milkHistoryListPojos.get(i).total_fat.equals("-")) {
                Totfat = Totfat + Double.parseDouble(milkHistoryListPojos.get(i).total_fat);
                totalWeight = totalWeight + Double.parseDouble(milkHistoryListPojos.get(i).total_milk);
                totalAmt = totalAmt + Double.parseDouble(milkHistoryListPojos.get(i).total_price);
                count++;
            }
        }
        avgFat = Totfat / count;
        tvAverageFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + Math.ceil(avgFat) + "%");
        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalAmt));
        // tvFatRat.setText("Fat Rate\n" + "Rs. " + String.format("%.2f", fatRate));

        MilkHistoryAdapter milkHistoryAdapter = new MilkHistoryAdapter(mContext, milkHistoryListPojos);
        milk_history.setLayoutManager(new GridLayoutManager(mContext, 1));
        milk_history.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        milk_history.setAdapter(milkHistoryAdapter);

    }

    public void toolbarManage() {

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void OnFragmentBackPressListener() {
        fragment = new DairyDeshboardFragment();
        UtilityMethod.goNextFragmentWithBackStack(mContext, fragment);
    }
}
