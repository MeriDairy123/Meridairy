package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

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

public class FragmentCustomerTransactionData extends Fragment implements
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
    String fileUrl = "", userGroupId = "4", strType = "user_transaction", selectedName = "", userMobileNo = "", dairy_id = "", selectedId = "", unic_customer = "";
    double totalCredit = 0.0, totalDebit = 0.0, remain = 0.0;

    Bundle bundle = null;
    View layoutSpin;
    SearchableSpinner spinUser;
    private int mYear, mMonth, mDay;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        initView();
        return view;
    }




    private void initView() {
        sessionManager = new SessionManager(mContext);
        dairy_id = Constant.DairyNameID;
        toolbar = view.findViewById(R.id.toolbar);
        imgPrint = view.findViewById(R.id.imgPrintRecp);
        imgPrint.setVisibility(View.GONE);
        layoutSpin = view.findViewById(R.id.layoutSpin);
        spinUser = view.findViewById(R.id.spinUser);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        layoutSpin.setVisibility(View.GONE);
        toolbar.setTitle(mContext.getString(R.string.Transaction));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        bundle = getArguments();
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

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvPaidAmount = view.findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = view.findViewById(R.id.tvRemaningAmount);
        getPaymentTransactionList(mContext,dairy_id,selectedId,strType,strFromDate,strEndDate,this);
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

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new PaymentUserTransectionsItemAdapter(mContext, transactionList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        remain = totalCredit - totalDebit;
        tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalCredit));
        tvPaidAmount.setText(mContext.getString(R.string.Total_Debit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalDebit));
        tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " " + mContext.getString(R.string.Rs) + " \n" + String.format("%.2f", remain)));

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
                        String day =  checkDigit(dayOfMonth);
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


    @Override
    public void setTransactionList(ArrayList<BeanUserTransaction> transactionList, double totalCredit, double totalDebit, String fileUrl) {
        this.transactionList=transactionList;
        this.totalCredit=totalCredit;
        this.totalDebit=totalDebit;
        this.fileUrl=fileUrl;
        initRecyclerView();
    }
}