package b2infosoft.milkapp.com.Shopping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OnOrderRecievedListner;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.Model.BeanRecievedOrderItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.Adapter.RecievedOrder_item_adapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getDairyCustomersOrderListAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class CustomerOrderRecievedFragment extends Fragment implements OnOrderRecievedListner, FragmentBackPressListener {


    Context mContext;

    Toolbar toolbar;
    TextView toolbar_title;
    SearchView searchView;
    SessionManager sessionManager;

    RecievedOrder_item_adapter adapter;
    ArrayList<BeanRecievedOrderItem> mList;

    RecyclerView recyclerView;
    Fragment fragment = null;
    View view;
    String filterKeyword = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_recyclerview_with_search, container, false);

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
        toolbar_title.setText(getString(R.string.Customers) + " " + getString(R.string.Order));
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);

        initRecyclerView();
        toolbarManage();
        getOrderList();
    }

    private void initRecyclerView() {

        adapter = new RecievedOrder_item_adapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                adapter.filterSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                filterKeyword = "";
                adapter.filterSearch(filterKeyword);

                return false;
            }
        });
    }


    public void getOrderList() {

        if (isNetworkAvaliable(mContext)) {

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Order List data...", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        mList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            ArrayList<BeanOrderProductItem> mProductList = new ArrayList<BeanOrderProductItem>();
                            JSONArray ordersJsonArray = jsonObject.getJSONArray("orders");
                            BaseImageUrl = jsonObject.getString("path");
                            for (int i = 0; i < ordersJsonArray.length(); i++) {
                                JSONObject object = ordersJsonArray.getJSONObject(i);
                                mProductList = new ArrayList<>();
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
                                mList.add(new BeanRecievedOrderItem(object.getString("order_id"),
                                        object.getString("transaction_id"), object.getString("order_status"),
                                        object.getString("order_date"), object.getString("type"),
                                        object.getString("invoice_url"), object.getString("customer_id"),
                                        object.getString("name"), object.getString("phone_number"),
                                        object.getDouble("grandtotal"), mProductList));
                            }

                        }
                        initRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            serviceCaller.execute(getDairyCustomersOrderListAPI + sessionManager.getValueSesion(KEY_UserID));
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

    @Override
    public void onAdapterClick(BeanRecievedOrderItem beanRecievedOrderItem) {
        Gson gson = new Gson();
        String json = gson.toJson(beanRecievedOrderItem);
        sessionManager.setValueSession("beanRecievedOrderItem", json);
        Bundle bundle = new Bundle();
        bundle.putString("from", "order");

        Fragment fragment = new fragment_CustRecievedOrderDetails();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.dairy_container, fragment)
                .addToBackStack(null)
                .commit();

    }
}
