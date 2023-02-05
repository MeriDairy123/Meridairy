package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentUserTransectionsItemAdapter;
import b2infosoft.milkapp.com.Interface.OnTransactionListener;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.Model.BeanUserTransaction.getPaymentTransactionList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;

public class CustomerTransactionDataActivity extends Activity implements
        View.OnClickListener, OnTransactionListener {
    Context mContext;
    Toolbar toolbar;
    TextView tvStartDate, tvEndDate;
    ImageView imgStartDate, imgEndDate, imgPrint;
    PaymentUserTransectionsItemAdapter adapter;
    RecyclerView recyclerView;
    TextView tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    String formattedDate = "", previousMonthYear = "", strFromDate = "", strEndDate = "";
    ArrayList<BeanUserTransaction> transactionList = new ArrayList<>();


    SessionManager sessionManager;
    Button btnSubmit;
    String fileUrl = "", userGroupId = "3", strType = "user_transaction", selectedName = "", userMobileNo = "", dairy_id = "", selectedId = "", unic_customer = "";
    double totalCredit = 0.0, totalDebit = 0.0, remain = 0.0;

    Bundle bundle = null;
    View layoutSpin;
    SearchableSpinner spinUser;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_transactions);
        mContext = CustomerTransactionDataActivity.this;
        sessionManager = new SessionManager(mContext);
        initView();
    }


    private void initView() {
        sessionManager = new SessionManager(mContext);
        dairy_id = Constant.DairyNameID;
        toolbar = findViewById(R.id.toolbar);
        imgPrint = findViewById(R.id.imgPrintRecp);
        imgPrint.setVisibility(View.GONE);
        layoutSpin = findViewById(R.id.layoutSpin);
        spinUser = findViewById(R.id.spinUser);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        imgStartDate = findViewById(R.id.imgStartDate);
        imgEndDate = findViewById(R.id.imgEndDate);
        layoutSpin.setVisibility(View.GONE);
        toolbar.setTitle(mContext.getString(R.string.Transaction));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bundle = getIntent().getExtras();
        selectedId = Constant.UserID;
        dairy_id = Constant.DairyNameID;
        userMobileNo = sessionManager.getValueSesion(SessionManager.KEY_Mobile);
        selectedName = sessionManager.getValueSesion(SessionManager.KEY_Name);
        unic_customer = selectedId;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        strFromDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
        strEndDate = getSimpleDate();
        tvStartDate.setText(strFromDate);
        tvEndDate.setText(strEndDate);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvTotalCredit = findViewById(R.id.tvTotalCredit);
        tvPaidAmount = findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = findViewById(R.id.tvRemaningAmount);
        getPaymentTransactionList(mContext,dairy_id,selectedId,strType,strFromDate,strEndDate,this);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgStartDate:
            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.imgEndDate:
            case R.id.tvEndDate:
                getDate("EndDate");
                break;
            case R.id.btnSubmit:
                if (!selectedId.equals("") && !tvStartDate.getText().toString().equals("")
                        && !tvEndDate.getText().toString().equals("")) {
                    previousMonthYear = tvStartDate.getText().toString();
                    formattedDate = tvEndDate.getText().toString();
                    getPaymentTransactionList(mContext,dairy_id,selectedId,strType,strFromDate,strEndDate,this);
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                }
                break;
        }
    }


    public void initRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new PaymentUserTransectionsItemAdapter(mContext, transactionList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        remain = totalCredit - totalDebit;
        tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalCredit));
        tvPaidAmount.setText(mContext.getString(R.string.Total_Debit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalDebit));
        tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " " + mContext.getString(R.string.Rs) + " \n" + String.format("%.2f", remain)));

    }

    @Override
    public void setTransactionList(ArrayList<BeanUserTransaction> transactionList, double totalCredit, double totalDebit, String fileUrl) {
        this.transactionList=transactionList;
        this.totalCredit=totalCredit;
        this.totalDebit=totalDebit;
        this.fileUrl=fileUrl;
        initRecyclerView();

    }
    public void getDate(final String from) {
        Calendar c2 = Calendar.getInstance();
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
                        day = checkDigit(dayOfMonth);

                        formattedDate = day + "-" + month + "-" + year;
                        if (from.equals("StartDate")) {
                            strFromDate = formattedDate;
                            tvStartDate.setText(strFromDate);
                        } else {
                            strEndDate = formattedDate;
                            tvEndDate.setText(strEndDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

}