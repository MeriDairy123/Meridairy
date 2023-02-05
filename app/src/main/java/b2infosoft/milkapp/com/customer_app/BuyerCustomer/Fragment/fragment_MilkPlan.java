package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.MyPlan_Item_adapter;
import b2infosoft.milkapp.com.customer_app.Interface.MyPlanListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getMilkPlanAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_MilkPlan extends Fragment implements View.OnClickListener, MyPlanListner {

    Context mContext;
    Toolbar toolbar;
    View view, layoutPlan;

    MyPlan_Item_adapter adapterPlan;
    ArrayList<BeanMilkPlan> myPlanList;
    RecyclerView recyclerViewPlan;
    RecyclerView recyclerViewUpdate;
    Button btnUpdate;
    CheckBox chkboxMorning, chkboxEvening;
    SessionManager sessionManager;
    String customerid = "", dairyId = "";

    Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_milk_plan, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        myPlanList = new ArrayList<>();

        toolbar = view.findViewById(R.id.toolbar);
        recyclerViewPlan = view.findViewById(R.id.recyclerViewPlan);
        recyclerViewPlan.setVisibility(View.VISIBLE);
        layoutPlan = view.findViewById(R.id.layoutPlan);
        recyclerViewUpdate = view.findViewById(R.id.recyclerViewUpdate);
        chkboxMorning = view.findViewById(R.id.chkboxMorning);
        chkboxEvening = view.findViewById(R.id.chkboxEvening);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        layoutPlan.setVisibility(View.GONE);
        btnUpdate.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.MyPlan);
        customerid = sessionManager.getValueSesion(SessionManager.KEY_CustomerUserID);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_dairy_id);

        getPlanList();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        return view;
    }


    private void initRecyclerViewPlan() {
        btnUpdate.setVisibility(View.VISIBLE);

        adapterPlan = new MyPlan_Item_adapter(mContext, myPlanList, this, "Customer");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerViewPlan.setLayoutManager(mLayoutManager);
        recyclerViewPlan.setAdapter(adapterPlan);
    }


    public void getPlanList() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        myPlanList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                myPlanList.add(new BeanMilkPlan(object.getString("id"),
                                        object.getString("plan_name"),
                                        object.getString("weight"), object.getString("price"),
                                        object.getString("shift"), object.getString("status"), false));
                            }
                        }
                        btnUpdate.setVisibility(View.VISIBLE);
                        if (!myPlanList.isEmpty()) {
                            btnUpdate.setText(mContext.getString(R.string.UPDATE));
                            initRecyclerViewPlan();
                        } else {
                            btnUpdate.setText(mContext.getString(R.string.Add_Milk_Plan));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("customer_id", customerid)
                    .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                    .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                    .build();
            caller.addRequestBody(body);
            caller.execute(getMilkPlanAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:

                Gson gson = new Gson();
                String json = gson.toJson(myPlanList);
                sessionManager.setValueSession("myPlanList", json);
                Bundle bundle = new Bundle();
                bundle.putString("customer_id", customerid);
                bundle.putString("dairy_id", dairyId);

                fragment = new fragment_UpdateMilkPlan();

                fragment.setArguments(bundle);
                FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                transaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();
                break;
        }
    }


    @Override
    public void refreshList() {
        getPlanList();
    }
}