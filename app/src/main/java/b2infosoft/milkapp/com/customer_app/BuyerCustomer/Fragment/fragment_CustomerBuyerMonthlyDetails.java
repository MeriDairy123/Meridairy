package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Dairy.SellMilk.Adapter.BuyerMonthlyDetailsListAdapter;
import b2infosoft.milkapp.com.Interface.RefreshBuyerMonthList;
import b2infosoft.milkapp.com.Model.BuyerMonthlyListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.buyerMonthlyRecordListAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class fragment_CustomerBuyerMonthlyDetails extends Fragment implements View.OnClickListener, RefreshBuyerMonthList {

    public TextView tvTotalMorning, tvTotalEvening, tvTotalAmount, tvUserRate;
    Toolbar toolbar;
    Context mContext;
    TextView tvUserID, tvUserName, tvUserMobile, tvMonth;
    ImageView imgLeft, imgRight;
    RecyclerView recycler_transactionList;
    ArrayList<BuyerMonthlyListPojo> milkHistoryListPojos;
    BuyerMonthlyDetailsListAdapter mAdapter;
    String[] monthsList = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    int year, currentMonth, date, ii;
    Calendar c;
    SessionManager sessionManager;
    String milkRate = "";
    String userGroupId = "4", monthTxt = "", fromWhere = "", dairyID = "", userID = "";

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_monthly_details, container, false);
        mContext = getActivity();

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();

        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvUserID = toolbar.findViewById(R.id.tvUserID);
        tvUserName = toolbar.findViewById(R.id.tvUserName);
        tvUserMobile = toolbar.findViewById(R.id.tvUserMobile);
        tvMonth = view.findViewById(R.id.tvMonth);
        tvUserRate = toolbar.findViewById(R.id.tvUserRate);
        tvTotalMorning = view.findViewById(R.id.tvTotalMorning);
        tvTotalEvening = view.findViewById(R.id.tvTotalEvening);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        imgLeft = view.findViewById(R.id.imgLeft);
        imgRight = view.findViewById(R.id.imgRight);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        Bundle bundle = getArguments();
        String id = bundle.getString("unic_customer");
        userID = bundle.getString("userID");
        if (id != null) {
            tvUserID.setText(id + ",");
        } /*else {
            tvUserID.setText(bundle.getString("userID") + ",");
        }*/
        tvUserName.setText(bundle.getString("userName") + ",");
        tvUserMobile.setText(bundle.getString("userMobile"));
        milkRate = bundle.getString("milkRate");
        fromWhere = bundle.getString("fromWhere");
        dairyID = bundle.getString("dairyID");


        // tvUserRate.setText("" + milkRate + " /Ltr");
        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        c = Calendar.getInstance();
        date = c.get(Calendar.DATE);
        year = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        System.out.println("Current==>>>" + "Month:- " + currentMonth + " Year:-" + year);
        for (int i = 0; i < monthsList.length; i++) {
            if (i == currentMonth) {
                System.out.println("monthTxt>>>" + monthsList[i]);
                ii = i + 1;
                monthTxt = monthsList[i];
                tvMonth.setText(monthsList[i] + " " + year);
                break;
            }
        }
        getMonthlyRecord(mContext, dairyID, userID, "" + ii, "" + year);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgLeft:
                c.add(Calendar.MONTH, -1);
                for (int i = 0; i < monthsList.length; i++) {
                    if (i == c.get(Calendar.MONTH)) {
                        ii = i + 1;
                        System.out.println("monthTxt===>>>" + monthsList[i]);
                        monthTxt = monthsList[i];
                        tvMonth.setText(monthsList[i] + " " + c.get(Calendar.YEAR));
                        break;
                    }
                }
                getMonthlyRecord(mContext, dairyID, userID, "" + ii, "" + c.get(Calendar.YEAR));
                break;
            case R.id.imgRight:
                c.add(Calendar.MONTH, 1);
                for (int i = 0; i < monthsList.length; i++) {
                    if (i == c.get(Calendar.MONTH)) {
                        System.out.println("monthTxt===>>>" + monthsList[i]);
                        ii = i + 1;
                        monthTxt = monthsList[i];
                        tvMonth.setText(monthsList[i] + " " + c.get(Calendar.YEAR));
                        break;
                    }
                }
                getMonthlyRecord(mContext, dairyID, userID, "" + ii, "" + c.get(Calendar.YEAR));
                break;
        }
    }

    public void setMilkHistoryList() {


        recycler_transactionList = view.findViewById(R.id.recycler_transactionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_transactionList.setHasFixedSize(true);
        mAdapter = new BuyerMonthlyDetailsListAdapter(mContext, milkHistoryListPojos, dairyID, this);
        recycler_transactionList.setLayoutManager(mLayoutManager);
        recycler_transactionList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        double morningWeight = 0.0, eveningWeight = 0.0, morningPrice = 0.0, eveningPrice = 0.0;
        double morningTotAmt = 0.0, eveningTotAmt = 0.0;
        double morningTotWeight = 0.0, eveningTotWeight = 0.0, actualPrice = 0;

        for (int i = 0; i < milkHistoryListPojos.size(); i++) {

            if (i == 0) {
                actualPrice = Double.parseDouble(milkHistoryListPojos.get(i).morning_rate);
            }
            if (milkHistoryListPojos.get(i).morning_sell_sts.equals("yes")) {

                if (!milkHistoryListPojos.get(i).morning_milk.equals("")) {
                    morningPrice = Double.parseDouble(milkHistoryListPojos.get(i).morning_rate);
                    morningWeight = Double.parseDouble(milkHistoryListPojos.get(i).morning_milk);
                    morningTotWeight = morningTotWeight + morningWeight;
                    morningTotAmt = morningTotAmt + (morningWeight * morningPrice);
                }
            }
            if (milkHistoryListPojos.get(i).evening_sell_sts.equals("yes")) {

                if (!milkHistoryListPojos.get(i).evening_milk.equals("")) {
                    eveningPrice = Double.parseDouble(milkHistoryListPojos.get(i).evening_rate);
                    eveningWeight = Double.parseDouble(milkHistoryListPojos.get(i).evening_milk);
                    eveningTotWeight = eveningTotWeight + eveningWeight;
                    eveningTotAmt = eveningTotAmt + (eveningWeight * eveningPrice);
                }
            }


        }
        tvUserRate.setText(getString(R.string.Rs) + " " + String.format("%.2f", actualPrice) + " /" + getString(R.string.Ltr));
        tvTotalEvening.setText(getString(R.string.Evening_Total) + "\n" + String.format("%.3f", eveningTotWeight) + " " + getString(R.string.Ltr));
        tvTotalMorning.setText(getString(R.string.Morning_Total) + "\n" + String.format("%.3f", morningTotWeight) + " " + getString(R.string.Ltr));
        tvTotalAmount.setText(getString(R.string.Total_Amount) + "\n" + String.format("%.2f", (morningTotAmt + eveningTotAmt)) + " " + getString(R.string.Rs));

    }


    @Override
    public void refreshBuyerData(ArrayList<BuyerMonthlyListPojo> milkHistoryList) {
        milkHistoryListPojos = new ArrayList<>();
        milkHistoryListPojos.addAll(milkHistoryList);


        double morningWeight = 0.0, eveningWeight = 0.0, totalAmt = 0.0, pricePerLtr = 0.0, morningPrice = 0.0, eveningPrice = 0.0;
        double morningTotAmt = 0.0, eveningTotAmt = 0.0;
        for (int i = 0; i < milkHistoryListPojos.size(); i++) {
            if (milkHistoryListPojos.get(i).morning_sell_sts.equals("yes")) {
                if (!milkHistoryListPojos.get(i).morning_milk.equals("")) {
                    morningPrice = Double.parseDouble(milkHistoryListPojos.get(i).morning_rate);
                    morningWeight = morningWeight + Double.parseDouble(milkHistoryListPojos.get(i).morning_milk);
                    morningTotAmt = morningTotAmt + (morningWeight * morningPrice);
                }
            }
            if (milkHistoryListPojos.get(i).evening_sell_sts.equals("yes")) {
                if (!milkHistoryListPojos.get(i).evening_milk.equals("")) {
                    eveningPrice = Double.parseDouble(milkHistoryListPojos.get(i).evening_rate);
                    eveningWeight = eveningWeight + Double.parseDouble(milkHistoryListPojos.get(i).evening_milk);
                    eveningTotAmt = eveningTotAmt + (eveningWeight * eveningPrice);
                }
            }
        }
        tvTotalEvening.setText(getString(R.string.Evening_Total) + "\n" + String.format("%.3f", eveningWeight) + " " + getString(R.string.Ltr));
        tvTotalMorning.setText(getString(R.string.Morning_Total) + "\n" + String.format("%.3f", morningWeight) + " " + getString(R.string.Ltr));
        tvTotalAmount.setText(getString(R.string.Total_Amount) + "\n" + String.format("%.2f", (morningTotAmt + eveningTotAmt)) + " " + getString(R.string.Rs));
    }

    public void getMonthlyRecord(final Context context, String dairy_id, String customer_id, String month, String year) {
        milkHistoryListPojos = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, context, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        milkHistoryListPojos.add(new BuyerMonthlyListPojo(
                                jsonObject1.getString("selling_date"),
                                jsonObject1.getString("customer_id"),
                                jsonObject1.getString("dairy_id"),
                                jsonObject1.getString("morning_milk"),
                                jsonObject1.getString("evening_milk"),
                                jsonObject1.getString("milk_rate")
                                , jsonObject1.getString("morning_sell_sts")
                                , jsonObject1.getString("evening_sell_sts")
                                , jsonObject1.getString("morning_rate")
                                , jsonObject1.getString("evening_rate")));
                    }
                    setMilkHistoryList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_id", customer_id)
                .addEncoded("month", month)
                .addEncoded("year", year)
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(buyerMonthlyRecordListAPI);
    }

}
