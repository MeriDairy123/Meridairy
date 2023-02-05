package b2infosoft.milkapp.com.BuyPlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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

import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.Interface.PlanListListener;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.Payment_Status_activity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.getPlan;
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
 * Created by Choudhary on 26-Jan-19.
 */


public class SMSPlan_activity extends Activity implements PaytmPaymentTransactionCallback, UpdateList, PaytmCheckSumListener, PlanListListener {

    Context mContext;
    Toolbar toolbar;


    SessionManager sessionManager;
    SMSPlanAdapter adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BeanSMSPlan> mList;


    String checksum = "", order_id = "", validity = "", customer_id = "", amount = "";
    String STATUS = "", data = "", type = "sms";
    PaytmPGService Service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sms_plan);
        mContext = SMSPlan_activity.this;
        sessionManager = new SessionManager(mContext);
        customer_id = sessionManager.getValueSesion(KEY_UserID);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlanList();
                pullToRefresh.setRefreshing(false);
            }
        });

        getPlanList();

    }
   public void getPlanList(){
       getPlan(mContext, type,this);
   }

    public void initRecyclerView() {
        layoutManager = new LinearLayoutManager(mContext);
        adapter = new SMSPlanAdapter(mContext, mList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onUpdateList(int position, String from) {
        amount = mList.get(position).plan_cost;
        validity = mList.get(position).validity;
        order_id = initOrderId() + "U" + customer_id;
        Service = PaytmPGService.getProductionService();
        generateCheckSum(mContext,order_id,customer_id,amount,this);

    }


    @Override
    public void setPlan(ArrayList<BeanSMSPlan> planList) {
        mList = new ArrayList<>();
        mList.addAll(planList);
        initRecyclerView();
    }


    public void onStartTransaction() {

        Map<String, String> paramMap = new HashMap<String, String>();
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
        Service.startPaymentTransaction(mContext,true,true,this);

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
        startActivity(i);
        finish();
    }


    @Override
    public void checkSumResult(String checkSum, JSONObject jsonObject) {
        this.checksum=checkSum;
        onStartTransaction();
    }


}
