package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.OrderOnClickListner;
import b2infosoft.milkapp.com.Model.BeanOrderItem;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.BuyerOrder_Item_adapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderListAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragment_Buyer_OrderList extends Fragment implements OrderOnClickListner {


    Context mContext;
    Toolbar toolbar;
    SessionManager sessionManager;
    BuyerOrder_Item_adapter adapter;
    ArrayList<BeanOrderItem> mList;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        mList = new ArrayList<>();

        initView();


        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.MyOrder));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getOrderList(mContext);
                pullToRefresh.setRefreshing(false);
            }
        });


        initRecyclerView();
        getOrderList(mContext);
    }

    private void initRecyclerView() {

        adapter = new BuyerOrder_Item_adapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onAdapterClick(BeanOrderItem beanOrderItem) {

        Gson gson = new Gson();
        String json = gson.toJson(beanOrderItem);
        sessionManager.setValueSession("beanOrderItem", json);

        Bundle bundle = new Bundle();
        bundle.putString("from", "order");
        Fragment fragment = new fragment_BuyerOrderDetails();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_body, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void getOrderList(final Context mContext) {

        if (isNetworkAvaliable(mContext)) {

            mList = new ArrayList<>();
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Order List data...", true) {
                @Override
                public void handleResponse(String response) {
                    ArrayList<BeanOrderProductItem> mProductList = new ArrayList<BeanOrderProductItem>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        BaseImageUrl = jsonObject.getString("path");
                        if (jsonObject.has("orders")) {
                            JSONArray ordersJsonArray = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < ordersJsonArray.length(); i++) {
                                mProductList = new ArrayList<>();
                                JSONObject object = ordersJsonArray.getJSONObject(i);
                                if (object.has("product")) {
                                    JSONArray productJsonArray = object.getJSONArray("product");
                                    for (int j = 0; j < productJsonArray.length(); j++) {
                                        JSONObject productObject = productJsonArray.getJSONObject(j);
                                        mProductList.add(new BeanOrderProductItem(
                                                productObject.getInt("product_id"),
                                                productObject.getInt("qty"), productObject.getDouble("price"),
                                                productObject.getDouble("total_price"), productObject.getString("order_id"),
                                                productObject.getString("product_name"), productObject.getString("category"),
                                                productObject.getString("payment_mode"), productObject.getString("image"),
                                                productObject.getString("thumb"), productObject.getString("description")));
                                    }
                                }

                                mList.add(new BeanOrderItem(object.getString("order_id"),
                                        object.getString("transaction_id"),
                                        nullCheckFunction(object.getString("order_status")), object.getDouble("grandtotal"),
                                        object.getString("order_date"), object.getString("invoice_url"), object.getString("type"),
                                        mProductList));
                            }
                        }
                        initRecyclerView();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            serviceCaller.execute(getOrderListAPI + sessionManager.getValueSesion(KEY_UserID));
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }
}
