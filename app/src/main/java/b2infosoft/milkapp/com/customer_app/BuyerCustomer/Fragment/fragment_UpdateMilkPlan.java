package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.UpdateMilkPlan_ItemAdapter;
import b2infosoft.milkapp.com.customer_app.Interface.MilkPlanListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDairyMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getDairyPlanAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.insertSaleMilkPlanAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getWebDateYY_MM_DD;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;


public class fragment_UpdateMilkPlan extends Fragment implements View.OnClickListener, MilkPlanListner {

    Context mContext;
    Toolbar toolbar;
    View view, layoutPlan;
    UpdateMilkPlan_ItemAdapter adapterUpdatePaln;


    ArrayList<BeanDairyMilkPlan> dairyPlanList;
    RecyclerView recyclerViewPlan;
    RecyclerView recyclerViewUpdate;

    Button btnUpdate;
    CheckBox chkboxMorning, chkboxEvening;
    String previousPlanId = "";

    String morning = "1", evening = "1";
    boolean morningChecked = false;
    boolean eveningChecked = false;
    String strYes = "Yes";
    String strNo = "No";

    SessionManager sessionManager;
    Bundle bundle;
    JSONObject planObject;
    JSONArray jsonArray;
    String userGroupId = "4", customerId = "", dairyId = "";
    String date = "";
    boolean isSelectedPlan = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_milk_plan, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerViewPlan = view.findViewById(R.id.recyclerViewPlan);
        layoutPlan = view.findViewById(R.id.layoutPlan);
        recyclerViewPlan.setVisibility(View.GONE);
        layoutPlan.setVisibility(View.VISIBLE);

        recyclerViewUpdate = view.findViewById(R.id.recyclerViewUpdate);
        chkboxMorning = view.findViewById(R.id.chkboxMorning);
        chkboxEvening = view.findViewById(R.id.chkboxEvening);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        layoutPlan.setVisibility(View.VISIBLE);
        btnUpdate.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.updatePlan);
        Bundle bundle = getArguments();
        if (bundle != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            customerId = bundle.getString("customer_id");
            dairyId = bundle.getString("dairy_id");
        }
        isSelectedPlan = false;
        date = getWebDateYY_MM_DD();

        getDairyPlanList();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        initViewUpdatePlan();

        return view;
    }

    private void initViewUpdatePlan() {

        chkboxMorning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                morningChecked = isChecked;
                if (morningChecked) {
                    morning = "1";
                } else {
                    morning = "0";
                }
                System.out.println("morningChecked==" + morningChecked);

            }
        });
        chkboxEvening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                eveningChecked = isChecked;
                System.out.println("eveningChecked==" + eveningChecked);
                if (eveningChecked) {
                    evening = "1";
                } else {
                    evening = "0";
                }
            }
        });
    }


    private void initUpdateRecyclerViewPlan() {
        if (!dairyPlanList.isEmpty()) {
            btnUpdate.setVisibility(View.VISIBLE);
            btnUpdate.setText(mContext.getString(R.string.UPDATE));
        }
        adapterUpdatePaln = new UpdateMilkPlan_ItemAdapter(mContext, dairyPlanList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerViewUpdate.setLayoutManager(mLayoutManager);
        recyclerViewUpdate.setAdapter(adapterUpdatePaln);
    }


    public void getDairyPlanList() {
        if (isNetworkAvaliable(mContext)) {
            dairyPlanList = new ArrayList<BeanDairyMilkPlan>();

            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                dairyPlanList.add(new BeanDairyMilkPlan(object.getString("id"),
                                        object.getString("product_name"),
                                        object.getString("weight"), object.getString("price"),
                                        object.getString("status"), object.getInt("qty"), false));
                            }
                        }
                        initUpdateRecyclerViewPlan();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("user_id", customerId)
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                    .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                    .build();
            caller.addRequestBody(body);
            caller.execute(getDairyPlanAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:

                if (isSelectedPlan == false) {
                    showToast(mContext, "Please Select  Plan");
                }/* else if (!morningChecked && !eveningChecked) {
                    chkboxMorning.requestFocus();
                    showAlertBox(mContext, "Please choose at least one shift");
                }*/ else {
                    updatePlan(mContext);
                }


                break;
        }
    }

    @Override
    public void onAdapterClick(ArrayList<BeanDairyMilkPlan> mList) {
        try {
            isSelectedPlan = false;
            planObject = new JSONObject();
            jsonArray = new JSONArray();

            for (int i = 0; i < mList.size(); i++) {

                if (mList.get(i).isSelected()) {
                    isSelectedPlan = true;
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("user_id", customerId);
                    jsonObject.put("dairy_id", dairyId);
                    jsonObject.put("plan_id", mList.get(i).getId());
                    jsonObject.put("plan_name", mList.get(i).getProduct_name());
                    jsonObject.put("weight", mList.get(i).getWeight());
                    jsonObject.put("price", mList.get(i).getPrice());
                    jsonObject.put("qty", mList.get(i).getQty());
                    jsonObject.put("shift", "morning");
                    jsonObject.put("start_date", date);
                    jsonObject.put("end_date", date);

                    jsonArray.put(jsonObject);
                }
            }


            planObject.put("make_array", jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String shiftStatus(String status) {
        String result = "";
        if (status.equalsIgnoreCase("1")) {
            result = strYes;
        } else {
            result = strNo;
        }

        return result;
    }

    public void updatePlan(final Context mContext) {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Updating Plan...", true) {
                @Override
                public void handleResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };


            System.out.println("planObject===" + planObject.toString());
           /* RequestBody body = new FormEncodingBuilder()
                    .addEncoded("user_id", customerId)
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("make_array", jsonArray.toString())
                    .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                    .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                    .build();
            caller.addRequestBody(body);
            caller.execute(insertSaleMilkPlanAPI);*/

            RequestBody body = RequestBody.create(JSONMediaType, planObject.toString());
            caller.addRequestBody(body);
            caller.execute(insertSaleMilkPlanAPI);


        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

}