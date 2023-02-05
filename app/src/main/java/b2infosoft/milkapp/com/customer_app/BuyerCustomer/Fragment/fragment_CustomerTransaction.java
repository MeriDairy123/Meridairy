package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerTransactionAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.UserTransactionByDate;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;

public class fragment_CustomerTransaction extends Fragment implements View.OnClickListener {


    TextView tvStartDate, tvEndDate, tvTotalWeight, tvTotalFat, tvFatRat, tvTotalAmt;
    ImageView imgStartDate, imgEndDate, ivUserIcon;
    Button btnSubmit;//btnVerify;
    RecyclerView recycler_user_transactionList;
    CircularProgressBar yourCircularProgressbar;
    Context mContext;
    String formattedDate = "";
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    TextView tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    View view;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_transaction, container, false);
        mContext = getActivity();

        initView();
        return view;
    }


    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.View_All_Entry));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvTotalFat = view.findViewById(R.id.tvTotalFat);
        tvFatRat = view.findViewById(R.id.tvFatRat);
        tvTotalAmt = view.findViewById(R.id.tvTotalAmt);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        recycler_user_transactionList = view.findViewById(R.id.recycler_user_transactionList);
        yourCircularProgressbar = view.findViewById(R.id.yourCircularProgressbar);
        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvPaidAmount = view.findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = view.findViewById(R.id.tvRemaningAmount);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String previousMonthYear = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
        Log.d("previousMonthYear", previousMonthYear);
        tvStartDate.setText(previousMonthYear);
        tvEndDate.setText(formattedDate);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        getUserTransactionList(previousMonthYear, formattedDate);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (!tvStartDate.getText().toString().equals(getString(R.string.Start_Date)) && !tvEndDate.getText().toString().equals(getString(R.string.End_Date))) {
                    getUserTransactionList(tvStartDate.getText().toString(),
                            tvEndDate.getText().toString());
                } else {
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Start_Date_and_End_Date));
                }
                break;
            case R.id.imgStartDate:
                getDate("StartDate");
                break;
            case R.id.imgEndDate:
                getDate("EndDate");
                break;
            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.tvEndDate:
                getDate("EndDate");
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
                            tvStartDate.setText(formattedDate);
                        } else {
                            tvEndDate.setText(formattedDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void setTransactionDataList(ArrayList<UserTransactionByDate> transactionDataList) {

        if (transactionDataList.size() != 0) {
            recycler_user_transactionList.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
            CustomerTransactionAdapter monthsListAdapter = new CustomerTransactionAdapter(mContext, transactionDataList);
            recycler_user_transactionList.setLayoutManager(mLayoutManager);
            recycler_user_transactionList.setAdapter(monthsListAdapter);
            monthsListAdapter.notifyDataSetChanged();
            double totalCredit = 0.0;
            double totalDebit = 0.0;
            double remain = 0.0;
            for (int i = 0; i < transactionDataList.size(); i++) {
                if (transactionDataList.get(i).type.equals("credit") || transactionDataList.get(i).type.equals("receive")) {
                    totalCredit = totalCredit + Double.parseDouble(transactionDataList.get(i).total_price);
                } else {
                    totalDebit = totalDebit + Double.parseDouble(transactionDataList.get(i).total_price);
                }
            }
            remain = totalCredit - totalDebit;
            tvTotalCredit.setText(getString(R.string.Total_Credit) + " " + getString(R.string.Rs) + "  \n" + String.format("%.2f", totalCredit));
            tvPaidAmount.setText(getString(R.string.Total_Debit) + " " + getString(R.string.Rs) + " \n" + String.format("%.2f", totalDebit));
            tvRemaningAmount.setText(getString(R.string.Remaining) + " " + getString(R.string.Rs) + " \n" + String.format("%.2f", remain));

        } else {
            recycler_user_transactionList.setVisibility(View.INVISIBLE);
            UtilityMethod.showAlertWithButton(mContext, "" + getString(R.string.No_Data_Found));
            tvTotalCredit.setText(getString(R.string.Total_Credit) + " 0.0 ");
            tvPaidAmount.setText(getString(R.string.Total_Debit) + " 0.0");
            tvRemaningAmount.setText(getString(R.string.Remaining) + " 0.0");
        }


    }


    public void getUserTransactionList(String entry_date_from, String entry_date_to) {
        final ArrayList<UserTransactionByDate> arrayList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        arrayList.add(new UserTransactionByDate(jsonObject1.getString("milk_entries_id"), jsonObject1.getString("entry_date")
                                , jsonObject1.getString("created_time"), jsonObject1.getString("entry_date_str"), jsonObject1.getString("products_name")
                                , jsonObject1.getString("total_price"), jsonObject1.getString("type"), jsonObject1.getString("transactions_ids")));
                    }
                    setTransactionDataList(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", Constant.UserID)
                .addEncoded("dairy_id", Constant.DairyNameID)
                .addEncoded("entry_date_from", entry_date_from)
                .addEncoded("entry_date_to", entry_date_to)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getCustomerTransactionList);


    }

}
