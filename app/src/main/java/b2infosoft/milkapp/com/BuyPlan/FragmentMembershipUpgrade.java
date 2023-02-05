package b2infosoft.milkapp.com.BuyPlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.Interface.PlanListListener;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.Payment_Status_activity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bundleToJson;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.initOrderId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 27-Jan-20.
 */

public class FragmentMembershipUpgrade extends Fragment implements PaytmPaymentTransactionCallback, UpdateList, PaytmCheckSumListener, PlanListListener {


    Context mContext;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BeanSMSPlan> mList=new ArrayList<>();
    String strFrom = "", checksum = "", order_id = "", validity = "UnLimited",
            customer_id = "", amount = "", type = "membership", data = "", strTitle = "";
    String STATUS = "TXN_FAILURE";
    PaytmPGService Service;
    View view;
    Bundle bundle = null;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private MembershipItem_Adapter adapter;
    private SwipeRefreshLayout pullToRefresh;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        toolbar.setVisibility(View.GONE);
        sessionManager = new SessionManager(mContext);
        bundle = getArguments();
        customer_id = sessionManager.getValueSesion(KEY_UserID);
        if (bundle != null) {
            strFrom = bundle.getString("from");
            type = bundle.getString("type");
            if (strFrom.equalsIgnoreCase("dashboard")) {
                toolbar.setVisibility(View.VISIBLE);
                manageToolbar();
            }
        }
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlan();
                pullToRefresh.setRefreshing(false);
            }
        });
        getPlan();
        return view;
    }

    private void manageToolbar() {
        toolbar.setTitle(mContext.getString(R.string.membership));
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void getPlan() {
        BeanSMSPlan.getPlan(mContext,type,this);
    }
    @Override
    public void setPlan(ArrayList<BeanSMSPlan> planList) {
        mList = new ArrayList<>();
        mList.addAll(planList);
        initRecyclerView();
    }

    public void initRecyclerView() {
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new MembershipItem_Adapter(mContext, mList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onUpdateList(int position, String from) {
        amount = mList.get(position).plan_cost;
        validity = mList.get(position).plan_day;
        checkSumGenerate();

    }

    private void checkSumGenerate() {
        Service = PaytmPGService.getProductionService();
        order_id = initOrderId() + "U" + customer_id;
        Service = PaytmPGService.getProductionService();
        generateCheckSum(mContext,order_id,customer_id,amount,this);
    }

    @Override
    public void checkSumResult(String checkSum, JSONObject jsonObject) {
        this.checksum=checkSum;
        onStartTransaction();
    }


    public void onStartTransaction() {
        Map<String, String> paramMap = new HashMap<String, String>();
        //   paramMap.put("type", type);
        paramMap.put("MID", MID);
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("CUST_ID", customer_id);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", CALLBACK_URL + order_id);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("CHECKSUMHASH", checksum);

        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(mContext, true,
                true, this);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        data = bundleToJson(inResponse).toString();
        moveNextActivity();
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
    }

    @Override
    public void onBackPressedCancelTransaction() {

        STATUS = "TXN_FAILURE";
        moveNextActivity();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

        data = bundleToJson(inResponse).toString();
        STATUS = "TXN_FAILURE";
        moveNextActivity();

    }


    public void moveNextActivity() {

        if (data.length() > 0) {
            try {
                JSONObject object = new JSONObject(data);
                STATUS = object.getString("STATUS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(mContext, Payment_Status_activity.class);
        i.putExtra("order_id", order_id);
        i.putExtra("amount", amount);
        i.putExtra("STATUS", STATUS);
        i.putExtra("type", type);
        i.putExtra("data", data);
        i.putExtra("validity", validity);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }



}