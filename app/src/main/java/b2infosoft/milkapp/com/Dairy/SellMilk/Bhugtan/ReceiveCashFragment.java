package b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan;

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

import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.Adapter.ReceiveCashListAdapter;
import b2infosoft.milkapp.com.Model.ReceiveCashPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class ReceiveCashFragment extends Fragment implements View.OnClickListener {

    TextView tvMonth;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    RecyclerView recycler_transactionList;
    int year, currentMonth, date, ii;
    Calendar c;
    ImageView imgLeft, imgRight;
    String[] monthsList = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    String monthTxt = "";
    Fragment fragment = null;
    ReceiveCashListAdapter milkHistoryAdapter;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentreceive_cash, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
        return view;
    }

    private void initView() {


        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.RECEIVE_CASH));
        recycler_transactionList = view.findViewById(R.id.recycler_transactionList);
        imgLeft = view.findViewById(R.id.imgLeft);
        imgRight = view.findViewById(R.id.imgRight);
        tvMonth = view.findViewById(R.id.tvMonth);

        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        c = Calendar.getInstance();
        date = c.get(Calendar.DATE);
        year = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);

        for (int i = 0; i < monthsList.length; i++) {
            if (i == currentMonth) {

                ii = i + 1;
                monthTxt = monthsList[i];
                tvMonth.setText(monthsList[i] + " " + year);
                break;
            }
        }
        getMonthlyMilkDetails(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "4", "" + ii, "" + year);
        toolbarManage();
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
                getMonthlyMilkDetails(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "4", "" + ii, "" + year);
                break;
            case R.id.imgRight:
                c.add(Calendar.MONTH, 1);
                for (int i = 0; i < monthsList.length; i++) {
                    if (i == c.get(Calendar.MONTH)) {
                        ii = i + 1;
                        monthTxt = monthsList[i];
                        tvMonth.setText(monthsList[i] + " " + c.get(Calendar.YEAR));
                        break;
                    }
                }
                getMonthlyMilkDetails(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "4", "" + ii, "" + year);
                break;
        }
    }

    public void setTransactionListAdapter(ArrayList<ReceiveCashPojo> mainList) {

        milkHistoryAdapter = new ReceiveCashListAdapter(mContext, mainList);
        recycler_transactionList.setLayoutManager(new GridLayoutManager(mContext, 1));
        recycler_transactionList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        recycler_transactionList.setAdapter(milkHistoryAdapter);
    }

    public void getMonthlyMilkDetails(final Context mContext, String dairy_id, String user_group_id, String month, String year) {

        final ArrayList<ReceiveCashPojo> mainList = new ArrayList<ReceiveCashPojo>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ArrayList<ReceiveCashPojo> transactionList = new ArrayList<ReceiveCashPojo>();
                        JSONObject mJsonObject = jsonArray.getJSONObject(i);
                        JSONObject milk_shell_entryObject = mJsonObject.getJSONObject("milk_shell_entry");
                        JSONArray transactions_dataArray = mJsonObject.getJSONArray("transactions_data");
                        for (int j = 0; j < transactions_dataArray.length(); j++) {
                            JSONObject transactions_dataObj = transactions_dataArray.getJSONObject(j);
                            transactionList.add(new ReceiveCashPojo(
                                    transactions_dataObj.getString("milk_entries_id"),
                                    transactions_dataObj.getString("entry_date"),
                                    transactions_dataObj.getString("created_time"),
                                    transactions_dataObj.getString("entry_date_str"),
                                    transactions_dataObj.getString("products_name"),
                                    transactions_dataObj.getString("total_price"),
                                    transactions_dataObj.getString("type")
                            ));
                        }

                        mainList.add(new ReceiveCashPojo(
                                mJsonObject.getString("id"), mJsonObject.getString("created_by"),
                                mJsonObject.getString("unic_customer"), mJsonObject.getString("name"),
                                mJsonObject.getString("father_name"), mJsonObject.getString("phone_number"),
                                mJsonObject.getString("user_group_id"),
                                mJsonObject.getString("firebase_tocan"),
                                milk_shell_entryObject.getString("total_morning_milk"),
                                milk_shell_entryObject.getString("total_evening_milk"),
                                milk_shell_entryObject.getString("total_morning_price"),
                                milk_shell_entryObject.getString("total_evening_price"),
                                transactionList
                        ));
                    }

                    setTransactionListAdapter(mainList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("user_group_id", user_group_id)
                .addEncoded("month", month)
                .addEncoded("year", year).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMonthlyMilkSellEntryAPI);

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


}
