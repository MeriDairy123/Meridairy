package b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.Adapter.DeliveryBoyUserListAdapter;
import b2infosoft.milkapp.com.Interface.DeliveryBoyUserListner;
import b2infosoft.milkapp.com.Model.BeanDeliveryBoyItem;
import b2infosoft.milkapp.com.Model.BeanDeliveryUserListItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.assignDeliverBoyAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 27-July-19.
 */

public class DeliveryBoyUserListFragment extends Fragment implements DeliveryBoyUserListner {

    RecyclerView recyclerList;
    Context mContext;
    EditText et_Search;
    Toolbar toolbar;
    String dairyid = "";
    SwipeRefreshLayout pullToRefresh;

    SessionManager sessionManager;
    ArrayList<BeanDeliveryUserListItem> UserListItems;

    DeliveryBoyUserListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Button btnUpdate;

    View view;
    int deliveryBoyid = 0;
    Bundle bundle;
    List<String> updateVacList = new ArrayList<>();
    String updatedUserData = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_boy_customer_list, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        updateVacList = new ArrayList<>();
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        bundle = getArguments();
        if (bundle != null) {
            deliveryBoyid = bundle.getInt("id");
        }


        sessionManager = new SessionManager(mContext);
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanDeliveryBoyItem");
        System.out.println("UserListItems=json=>>" + json);

        BeanDeliveryBoyItem beanDeliveryBoyItem = gson.fromJson(json, BeanDeliveryBoyItem.class);
        UserListItems = beanDeliveryBoyItem.deliveryUserListItems;


        initView();
        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.USER_LIST));
        et_Search = view.findViewById(R.id.et_Search);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerList = view.findViewById(R.id.recyclerList);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        setUserList();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                pullToRefresh.setRefreshing(false);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserList();

            }
        });

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


    public void setUserList() {
        System.out.println("UserListItems==>>" + UserListItems.size());


        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new DeliveryBoyUserListAdapter(mContext, deliveryBoyid, UserListItems, this);
        recyclerList.setLayoutManager(mLayoutManager);
        recyclerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        addTextListener();
    }


    @Override
    public void onAdapterClick(ArrayList<BeanDeliveryUserListItem> UserListItems) {
        updateVacList = new ArrayList<>();
        for (int i = 0; i < UserListItems.size(); i++) {

            if (UserListItems.get(i).status == deliveryBoyid) {

                updateVacList.add(UserListItems.get(i).id);
            }
        }

        updatedUserData = TextUtils.join(", ", updateVacList);
        System.out.println("=updatedUserData=list==" + updatedUserData);
        btnUpdate.setVisibility(View.VISIBLE);
    }


    private void updateUserList() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Updating...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyid)
                    .addEncoded("deliveryboy_id", String.valueOf(deliveryBoyid))
                    .addEncoded("user_name", updatedUserData)
                    .build();
            caller.addRequestBody(body);
            caller.execute(assignDeliverBoyAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }
}