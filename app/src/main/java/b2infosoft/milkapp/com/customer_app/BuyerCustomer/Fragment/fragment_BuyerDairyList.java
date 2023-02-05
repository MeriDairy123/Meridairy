package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNameTransactionPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getAllDairyNameAndTransactionAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_CustomerUserID;


public class fragment_BuyerDairyList extends Fragment {

    Context mContext;
    Toolbar toolbar;
    Fragment fragment;
    int count = 0;
    CircleProgressView mCircleProgressView;
    LinearLayout layoutTransactionTabs;
    LinearLayout layout;
    RelativeLayout layout_2;
    Button btnMoreDetail;
    SessionManager sessionManager;
    View view;
    String userId = "", userGroupId = "4";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_dairy, container, false);
        mContext = getActivity();

        sessionManager = new SessionManager(mContext);

        userId = sessionManager.getValueSesion(KEY_CustomerUserID);

        toolbar = view.findViewById(R.id.toolbar);

        layout = view.findViewById(R.id.layout1);
        layout_2 = view.findViewById(R.id.layout2);


        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.MILK_HISTORY);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        getTransactionDetails();

        return view;
    }

    public void setTransactionDetail(ArrayList<DairyNameTransactionPojo> mainList) {
        ArrayList<DairyNameTransactionPojo> this_month_transactionsList = new ArrayList<>();
        ArrayList<DairyNameTransactionPojo> previous_month_transactionsList = new ArrayList<>();

        if (!mainList.isEmpty()) {

            for (int i = 0; i < mainList.size(); i++) {
                double currentAmt = 0, currentDebit = 0, currentCredit = 0;
                double previousTotalDebit = 0, previousTotalCredit = 0, totalDebit = 0, totalCredit = 0;
                double totalDue = 0;
                String dairyCenterName = mainList.get(i).center_name;

                String dairyID = mainList.get(i).id;
                String userID = mainList.get(i).customerID;
                String customer_user_group_id = mainList.get(i).customer_user_group_id;
                String customer_unic_customer = mainList.get(i).customer_unic_customer;
                //initialize
                this_month_transactionsList = new ArrayList<>();
                previous_month_transactionsList = new ArrayList<>();
                this_month_transactionsList = mainList.get(i).this_month_transactionsList;
                previous_month_transactionsList = mainList.get(i).previous_month_transactionsList;
                //--------------------this_month_transactionsList--------------------------------------
                if (!this_month_transactionsList.isEmpty()) {
                    for (int j = 0; j < this_month_transactionsList.size(); j++) {
                        if (this_month_transactionsList.get(j).type.equals("debit")) {
                            currentDebit = currentDebit + Double.parseDouble(this_month_transactionsList.get(j).total_price);
                        } else {
                            currentCredit = currentCredit + Double.parseDouble(this_month_transactionsList.get(j).total_price);
                        }
                    }
                }
                currentAmt = currentDebit - currentCredit;

                //-------------------------previous_month_transactionsList---------------------------------------------

                if (!previous_month_transactionsList.isEmpty()) {
                    for (int k = 0; k < previous_month_transactionsList.size(); k++) {
                        if (previous_month_transactionsList.get(k).type.equals("debit")) {
                            previousTotalDebit = previousTotalDebit + Double.parseDouble(previous_month_transactionsList.get(k).total_price);
                        }
                        if (previous_month_transactionsList.get(k).type.equals("receive")) {
                            previousTotalCredit = previousTotalCredit + Double.parseDouble(previous_month_transactionsList.get(k).total_price);
                        }
                    }
                }

                //-----------------------------all_month_transactionsList-------------------------------------------------


                totalDebit = previousTotalDebit + currentDebit;
                totalCredit = previousTotalCredit + currentCredit;
                totalDue = totalDebit - totalCredit;

                addMessageBox(dairyID, dairyCenterName, "" +
                                String.format("%.2f", totalDue), "" +
                                String.format("%.2f", totalCredit), "" +
                                String.format("%.2f", totalDebit), "" +
                                String.format("%.2f", currentDebit),
                        userID, customer_unic_customer, customer_user_group_id);
            }
        }
    }


    public void addMessageBox(String dairyID, String dairyName,
                              String remainAmt, String totalPaid, String totalDebitAmt, String currentMonthAmt,
                              String userID, String customer_unic_customer, String customer_user_group_id) {
        count++;
        View descriptionLayout;
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        LayoutInflater inflater = getLayoutInflater();
        descriptionLayout = inflater.inflate(R.layout.layout_transaction_tab_show, null, false);
        TextView tvAmount, tvRemainAmount, tvPaidAmount, tvMonth, tvTotalAmount, tvDairyName;
        layoutTransactionTabs = descriptionLayout.findViewById(R.id.layoutTransactionTabs);

        tvAmount = descriptionLayout.findViewById(R.id.tvAmount);
        tvTotalAmount = descriptionLayout.findViewById(R.id.tvTotalAmount);
        tvRemainAmount = descriptionLayout.findViewById(R.id.tvRemainAmount);
        tvPaidAmount = descriptionLayout.findViewById(R.id.tvPaidAmount);
        tvMonth = descriptionLayout.findViewById(R.id.tvMonth);
        tvDairyName = descriptionLayout.findViewById(R.id.tvDairyName);
        btnMoreDetail = descriptionLayout.findViewById(R.id.btnMoreDetail);
        mCircleProgressView = descriptionLayout.findViewById(R.id.circle_progress_view);
        mCircleProgressView.setTextEnabled(false);
        mCircleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        mCircleProgressView.setStartAngle(270);
        Calendar calendar = Calendar.getInstance();
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        DateFormat dateFormat2 = new SimpleDateFormat("MMM", Locale.ENGLISH);
        Date date = new Date();
        System.out.println("currentDate====" + dateFormat.format(date));
        int currentDate = Integer.parseInt(dateFormat.format(date));
        String Month = dateFormat2.format(date);
        float percentage = (currentDate * 100) / days;
        System.out.println("percentage======" + percentage);
        mCircleProgressView.setProgressWithAnimation(percentage, 2000);
        tvDairyName.setText("" + count + ". " + dairyName);
        tvDairyName.setSelected(true);
        tvRemainAmount.setText(remainAmt);
        tvPaidAmount.setText(totalPaid);
        tvAmount.setText(currentMonthAmt);
        tvMonth.setText("" + Month);
        tvTotalAmount.setText(totalDebitAmt);
        layoutTransactionTabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.DairyNameID = dairyID;
                Constant.UserID = userID;
                Constant.SessionUserGroupID = customer_user_group_id;
                Constant.DairySize = "One";
                Fragment fragment = new FragmentCustomerTransactionData();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_body, fragment).addToBackStack(fragment.getTag()).commit();


            }
        });

        mCircleProgressView.addAnimationListener(new ProgressAnimationListener() {
            @Override
            public void onValueChanged(float value) {
                //   tvAmount.setText("Rs. " + String.format("%.2f", currentAmt));
            }

            @Override
            public void onAnimationEnd() {
                // Toast.makeText(MainActivity.this, "Animation of CircleProgressView done", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(descriptionLayout);
        btnMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnClicked======>> " + tvDairyName.getText().toString());
                fragment = new fragment_CustomerBuyerMonthlyDetails();
                Bundle bundle = new Bundle();

                bundle.putString("userID", sessionManager.getValueSesion(SessionManager.KEY_CustomerUserID));
                bundle.putString("unic_customer", customer_unic_customer);
                bundle.putString("userName", sessionManager.getValueSesion(SessionManager.KEY_Name));
                bundle.putString("userMobile", sessionManager.getValueSesion(SessionManager.KEY_Mobile));
                bundle.putString("dairyID", dairyID);
                bundle.putString("milkRate", "");
                bundle.putString("fromWhere", "CustomerDeshBoard");
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_body, fragment).addToBackStack(fragment.getTag()).commit();
                //CustomerDeshBoard
            }
        });
    }


    public void getTransactionDetails() {

        final ArrayList<DairyNameTransactionPojo> mainList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject mainObject = mainJsonArray.getJSONObject(i);
                        String id = mainObject.getString("id");
                        String dairy_name = mainObject.getString("dairy_name");
                        String center_name = mainObject.getString("center_name");
                        String phone_number = mainObject.getString("phone_number");
                        String firebase_tocan = mainObject.getString("firebase_tocan");
                        JSONObject customerObj = mainObject.getJSONObject("customer");

                          ArrayList<DairyNameTransactionPojo> this_month_transactionsList = new ArrayList<>();
                          ArrayList<DairyNameTransactionPojo> previous_month_transactionsList = new ArrayList<>();
                          ArrayList<DairyNameTransactionPojo> all_month_transactionsList = new ArrayList<>();

                        JSONArray this_month_transactionsArray = mainObject.getJSONArray("this_month_transactions");
                        for (int j = 0; j < this_month_transactionsArray.length(); j++) {
                            JSONObject monthObj = this_month_transactionsArray.getJSONObject(j);
                            this_month_transactionsList.add(new DairyNameTransactionPojo(
                                    monthObj.getString("milk_entries_id"),
                                    monthObj.getString("entry_date"),
                                    monthObj.getString("created_time"),
                                    monthObj.getString("entry_date_str"),
                                    monthObj.getString("products_name"),
                                    monthObj.getString("total_price"),
                                    monthObj.getString("type")
                            ));
                        }
                        JSONArray previous_month_transactionsArray = mainObject.getJSONArray("previous_month_transactions");
                        for (int j = 0; j < previous_month_transactionsArray.length(); j++) {
                            JSONObject monthObj = previous_month_transactionsArray.getJSONObject(j);
                            previous_month_transactionsList.add(new DairyNameTransactionPojo(
                                    monthObj.getString("milk_entries_id"),
                                    monthObj.getString("entry_date"),
                                    monthObj.getString("created_time"),
                                    monthObj.getString("entry_date_str"),
                                    monthObj.getString("products_name"),
                                    monthObj.getString("total_price"),
                                    monthObj.getString("type")
                            ));
                        }

                        JSONArray all_month_transactionsArray = mainObject.getJSONArray("all_month_transactions");
                        for (int j = 0; j < all_month_transactionsArray.length(); j++) {
                            JSONObject monthObj = all_month_transactionsArray.getJSONObject(j);
                            all_month_transactionsList.add(new DairyNameTransactionPojo(
                                    monthObj.getString("milk_entries_id"), monthObj.getString("entry_date"),
                                    monthObj.getString("created_time"),
                                    monthObj.getString("entry_date_str"),
                                    monthObj.getString("products_name"),
                                    monthObj.getString("total_price"),
                                    monthObj.getString("type")
                            ));
                        }
                        mainList.add(new DairyNameTransactionPojo(
                                id, dairy_name, center_name, phone_number, firebase_tocan,
                                customerObj.getString("id"), customerObj.getString("unic_customer"),
                                customerObj.getString("user_group_id"), customerObj.getString("firebase_tocan"),
                                this_month_transactionsList, previous_month_transactionsList, all_month_transactionsList
                        ));
                    }
                    setTransactionDetail(mainList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("userid", sessionManager.getValueSesion(KEY_CustomerUserID))
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(getAllDairyNameAndTransactionAPI);

    }

}
