package b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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

import b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.Adapter.DeliveryBoyListAdapter;
import b2infosoft.milkapp.com.Model.BeanDeliveryBoyItem;
import b2infosoft.milkapp.com.Model.BeanDeliveryUserListItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.deliverBoyListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 27-July-19.
 */
public class DeliveryBoyListFragment extends Fragment {

    RecyclerView recyclerDeliveryList;
    Context mContext;
    EditText et_Search;
    Toolbar toolbar;
    TextView btnAddNew;
    SessionManager sessionManager;
    TextView tvId, tvName, tvFatherName, tvAmount;
    ArrayList<BeanDeliveryBoyItem> deliveryBoyList;

    SwipeRefreshLayout pullToRefresh;

    DeliveryBoyListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    String dairyid = "";
    View view;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dairy_delivery_list, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        sessionManager = new SessionManager(mContext);
        deliveryBoyList = new ArrayList<>();

        initView();
        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        btnAddNew = toolbar.findViewById(R.id.btnAddNew);
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.ic_nav_drawer));
        et_Search = view.findViewById(R.id.et_Search);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerDeliveryList = view.findViewById(R.id.recyclerList);


        tvId = view.findViewById(R.id.tvSellerId);
        tvName = view.findViewById(R.id.tvSellerName);
        tvFatherName = view.findViewById(R.id.tvFatherName);
        tvAmount = view.findViewById(R.id.tvSellerAmount);

        getDeliveryBoyList();

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_location_address = "";
                str_location_city = "";
                str_location_state = "";
                str_location_postCode = "";
                str_location_Latitude = "";
                str_location_Longitude = "";
                Fragment fragment = new AddOrEditDeliveryBoyFragment();
                goNextFragmentWithBackStack(mContext, fragment);
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getDeliveryBoyList();
                pullToRefresh.setRefreshing(false);
            }
        });
        setDataList();

    }

    private void getDeliveryBoyList() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
                @Override
                public void handleResponse(String response) throws JSONException {
                    try {
                        deliveryBoyList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {

                            JSONArray jsonArrayData = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                ArrayList<BeanDeliveryUserListItem> deliveryUserListItems = new ArrayList<>();

                                JSONObject object = jsonArrayData.getJSONObject(i);
                                if (object.has("users")) {

                                    JSONArray jsonUserData = object.getJSONArray("users");
                                    for (int j = 0; j < jsonUserData.length(); j++) {

                                        JSONObject userObject = jsonUserData.getJSONObject(j);
                                        deliveryUserListItems.add(new BeanDeliveryUserListItem(
                                                userObject.getString("id"),
                                                userObject.getString("unic_customer"),
                                                userObject.getString("name"),
                                                userObject.getString("phone_number"),
                                                userObject.getInt("deliveryboy")));
                                    }
                                }


                                deliveryBoyList.add(new BeanDeliveryBoyItem(
                                        object.getString("id"),
                                        object.getString("name"),
                                        object.getString("father_name"),
                                        object.getString("phone_number"),
                                        object.getString("address"),
                                        object.getInt("status"),
                                        object.getDouble("lat"),
                                        object.getDouble("lng"), deliveryUserListItems
                                ));
                            }

                        }
                        setDataList();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyid)
                    .build();
            caller.addRequestBody(body);
            caller.execute(deliverBoyListAPI);

        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }


    public void addTextListener() {


        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                adapter.filterSearch(query.toString());
            }
        });
    }


    public void setDataList() {
        System.out.println("deliveryBoy==>>" + deliveryBoyList.size());
        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new DeliveryBoyListAdapter(mContext, deliveryBoyList);
        recyclerDeliveryList.setLayoutManager(mLayoutManager);
        recyclerDeliveryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        addTextListener();
    }


}