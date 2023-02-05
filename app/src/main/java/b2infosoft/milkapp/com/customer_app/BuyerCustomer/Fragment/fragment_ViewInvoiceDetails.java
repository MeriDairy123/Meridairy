package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.InvoiceMonthly_Details_Item_adapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDashboardCalenderItem;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDeliveredMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.customerMonthDataAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragment_ViewInvoiceDetails extends Fragment {


    public String monthStr = "", webMonth = "", year = "invoice";
    Context mContext;
    SessionManager sessionManager;
    View view;
    Toolbar toolbar;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    List<BeanDashboardCalenderItem> invoiceItemList;
    InvoiceMonthly_Details_Item_adapter adapter;
    String customer_id = "", dairyId = "";
    Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        invoiceItemList = new ArrayList<>();
        toolbar = view.findViewById(R.id.toolbar);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_dairy_id);
        customer_id = sessionManager.getValueSesion(SessionManager.KEY_CustomerUserID);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            webMonth = bundle.getString("month_no");
            monthStr = bundle.getString("month_str");
            year = bundle.getString("year");
        }

        toolbar.setTitle(mContext.getString(R.string.ViewInvoice) + " " + monthStr + "-" + year);
        initRecyclerView();
        getUserdata();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getUserdata();
                pullToRefresh.setRefreshing(false);
            }
        });

        return view;


    }


    private void initRecyclerView() {

        adapter = new InvoiceMonthly_Details_Item_adapter(mContext, invoiceItemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void getUserdata() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Fetching...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        invoiceItemList = new ArrayList<>();
                        if (jsonObject.getBoolean("success")) {

                            String date = jsonObject.getString("month").trim();
                            String[] split = date.split("-");


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
                                invoiceItemList.add(new BeanDashboardCalenderItem(jsonObj.getInt("day"),
                                        jsonObj.getString("status"), milkPlanList

                                ));

                            }

                        }
                        initRecyclerView();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            String apiMonth = webMonth + "-" + year;
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("customer_id", sessionManager.getValueSesion(KEY_UserID))
                    .addEncoded("month", apiMonth).build();
            caller.addRequestBody(body);
            caller.execute(customerMonthDataAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }


}



