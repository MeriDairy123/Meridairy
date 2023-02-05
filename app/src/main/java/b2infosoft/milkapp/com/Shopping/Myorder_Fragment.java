package b2infosoft.milkapp.com.Shopping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OrderOnClickListner;
import b2infosoft.milkapp.com.Model.BeanOrderItem;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.adapter.Order_item_adapter;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerOrderDetails;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderListAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class Myorder_Fragment extends Fragment implements OrderOnClickListner, FragmentBackPressListener {


    Context mContext;

    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;

    Order_item_adapter adapter;
    ArrayList<BeanOrderItem> mList;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    Fragment fragment = null;
    Bundle bundle = null;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_shop_product, container, false);

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
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.MyOrder));
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getOrderList(mContext);
                pullToRefresh.setRefreshing(false);
            }
        });

        getOrderList(mContext);
        initRecyclerView();
        toolbarManage();


    }

    private void initRecyclerView() {

        adapter = new Order_item_adapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onAdapterClick(BeanOrderItem beanOrderItem) {

        Gson gson = new Gson();
        String json = gson.toJson(beanOrderItem);
        sessionManager.setValueSession("beanOrderItem", json);
        fragment = new fragment_BuyerOrderDetails();
        goNextFragmentWithBackStack(mContext, fragment);
    }

    public void getOrderList(final Context mContext) {

        if (isNetworkAvaliable(mContext)) {

            final ArrayList<BeanOrderItem> mOrderList = new ArrayList<BeanOrderItem>();

            NetworkTask caller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Order List data...", true) {
                @Override
                public void handleResponse(String response) {
                    ArrayList<BeanOrderProductItem> mProductList = new ArrayList<BeanOrderProductItem>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
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
                                                productObject.getString("thumb"), productObject.getString("description")
                                        ));


                                    }
                                }

                                mOrderList.add(new BeanOrderItem(object.getString("order_id"),
                                        object.getString("transaction_id"),
                                        nullCheckFunction(object.getString("order_status")), object.getInt("grandtotal"),
                                        object.getString("order_date"), object.getString("invoice_url"), object.getString("type"),
                                        mProductList));
                            }

                            if (!mOrderList.isEmpty()) {
                                mList = new ArrayList<>();
                                mList.addAll(mOrderList);
                                initRecyclerView();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            caller.execute(getOrderListAPI + sessionManager.getValueSesion(KEY_UserID));
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

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


    @Override
    public void OnFragmentBackPressListener() {
        fragment = new DairyDeshboardFragment();
        goNextFragmentWithBackStack(mContext, fragment);
    }
}
