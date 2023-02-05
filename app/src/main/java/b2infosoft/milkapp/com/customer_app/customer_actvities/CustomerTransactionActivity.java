package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

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

import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;

public class CustomerTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    TextView tvStartDate, tvEndDate, tvTotalWeight, tvTotalFat, tvFatRat, tvTotalAmt;
    ImageView imgStartDate, imgEndDate, ivUserIcon;
    Button btnSubmit;
    RecyclerView recycler_user_transactionList;
    CircularProgressBar yourCircularProgressbar;
    Context mContext;
    String formattedDate = "";
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    TextView tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customer_transaction);
        mContext = CustomerTransactionActivity.this;
        initView();
    }

    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.View_All_Entry));


        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvTotalWeight = findViewById(R.id.tvTotalWeight);
        tvTotalFat = findViewById(R.id.tvTotalFat);
        tvFatRat = findViewById(R.id.tvFatRat);
        tvTotalAmt = findViewById(R.id.tvTotalAmt);
        imgStartDate = findViewById(R.id.imgStartDate);
        imgEndDate = findViewById(R.id.imgEndDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        recycler_user_transactionList = findViewById(R.id.recycler_user_transactionList);
        yourCircularProgressbar = findViewById(R.id.yourCircularProgressbar);
        tvTotalCredit = findViewById(R.id.tvTotalCredit);
        tvPaidAmount = findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = findViewById(R.id.tvRemaningAmount);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String previousMonthYear = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
        System.out.println("previousMonthYear" + previousMonthYear);
        tvStartDate.setText(previousMonthYear);
        tvEndDate.setText(formattedDate);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        UserTransactionByDate.getUserTransactionList(mContext, Constant.DairyNameID, Constant.UserID, previousMonthYear, formattedDate);
    }

    @Override
     public void onBackPressed() {

         finish();

        /*if (Constant.DairySize.equals("One")) {
            super.onBackPressed();
            UtilityMethod.goNextClass(mContext, CustomerDeshBoardActivity.class);
        }else if (Constant.DairySize.equals("Dairy")) {
            finish();

        } else {
            super.onBackPressed();
            UtilityMethod.goNextClass(mContext, CustomerDairyListWithBoxActivity.class);
        } */

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (!tvStartDate.getText().toString().equals(getString(R.string.Start_Date)) && !tvEndDate.getText().toString().equals(getString(R.string.End_Date))) {
                    UserTransactionByDate.getUserTransactionList(mContext, Constant.DairyNameID, Constant.UserID, tvStartDate.getText().toString(),
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
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(this,
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
                        System.out.println("Month>>>" + month);
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
}
