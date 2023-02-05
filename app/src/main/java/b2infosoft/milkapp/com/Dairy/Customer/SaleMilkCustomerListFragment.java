package b2infosoft.milkapp.com.Dairy.Customer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.Customer.Adapter.BuyerCustomerListAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BuyerMilkCustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BuyerFirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class SaleMilkCustomerListFragment extends Fragment {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recycler_buyerCustomerList;
    BuyerCustomerListAdapter mAdapter;
    ArrayList<BuyerMilkCustomerListPojo> mList;
    EditText et_Search;
    DatabaseHandler databaseHandler;
    String dairyID = "", userGroupid = "4";
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sale_milk_customer_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        mList = new ArrayList<>();
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        dairyID = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        initView(view);
        return view;

    }


    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        et_Search = view.findViewById(R.id.et_Search1);
        recycler_buyerCustomerList = view.findViewById(R.id.recycler_buyerCustomerList);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recycler_buyerCustomerList = view.findViewById(R.id.recycler_buyerCustomerList);

        toolbar_title.setText(mContext.getString(R.string.MILK_BUYER_LIST));
        getBuyerMilkCustomerList();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getBuyerMilkCustomerList();

                pullToRefresh.setRefreshing(false);
            }
        });


    }

    public void setBuyerCustomerList() {

        ArrayList<BuyerMilkCustomerListPojo> filteredList = databaseHandler.getBuyerListByGroupId(userGroupid);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_buyerCustomerList.setHasFixedSize(true);
        mAdapter = new BuyerCustomerListAdapter(mContext, filteredList);
        recycler_buyerCustomerList.setLayoutManager(mLayoutManager);
        recycler_buyerCustomerList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                String querText = query.toString().toLowerCase();

                mAdapter.filterSearch(querText);

            }
        });
    }

    public void getBuyerMilkCustomerList() {


        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    mList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            databaseHandler.deleteBuyerCustomer();
                        }

                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject obj = mainJsonArray.getJSONObject(i);


                            mList.add(new BuyerMilkCustomerListPojo(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan")));

                            databaseHandler.addBuyerCustomer(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan"));

                        }


                        BuyerFirstTime = "Loaded";
                        setBuyerCustomerList();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()

                .addEncoded("dairy_id", dairyID).build();
        caller.addRequestBody(body);
        caller.execute(getBuyerMilkListAPI);
    }

}
